package com.internet.shop.dao.jdbc.impl;

import com.internet.shop.dao.UserDao;
import com.internet.shop.exceptions.DataProcessingException;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.Role;
import com.internet.shop.model.User;
import com.internet.shop.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Dao
public class UserDaoJdbcImpl implements UserDao {

    @Override
    public Optional<User> findByLogin(String login) {
        User user = null;
        String query = "SELECT * FROM users WHERE isDeleted = false AND login = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = extractValue(resultSet);
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to get the user "
                    + "with login: " + login, exception);
        }
        return addRolesToUser(user);
    }

    @Override
    public User create(User user) {
        String query = "INSERT INTO users(name, login, password) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to create the user: "
                    + user.getName(), exception);
        }
        return addRoles(user);
    }

    @Override
    public Optional<User> get(Long id) {
        User user = null;
        String query = "SELECT * FROM users WHERE isDeleted = false AND id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = extractValue(resultSet);
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to get the user "
                    + "with id: " + id, exception);
        }
        return addRolesToUser(user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(extractValue(resultSet));
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to get data", exception);
        }
        for (int index = 0; index < users.size(); index++) {
            users.get(index).setRoles(getRoles(users.get(index).getId()));
        }
        return users;
    }

    @Override
    public User update(User user) {
        Long userId = user.getId();
        String query = "UPDATE users SET name = ?, login = ?, password = ? WHERE id = ? "
                + "AND deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setString(4, String.valueOf(userId));
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to update the user "
                    + "with id: " + userId, exception);
        }
        deleteRoles(userId);
        return addRoles(user);
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE users SET deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() != 0;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to delete the user "
                    + "with id: " + id, exception);
        }
    }

    private Optional<User> addRolesToUser(User user) {
        if (user != null) {
            user.setRoles(getRoles(user.getId()));
            return Optional.of(user);
        }
        return Optional.empty();
    }

    private User addRoles(User user) {
        String query = "INSERT INTO users_roles(user_id, role_id) "
                + "VALUES (?, (SELECT id FROM roles WHERE name = ?))";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, user.getId());
            for (Role role : user.getRoles()) {
                statement.setString(2, role.getRoleName().name());
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to add the roles "
                    + "to the user: " + user.getName(), exception);
        }
        user.setRoles(getRoles(user.getId()));
        return user;
    }

    private User extractValue(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String password = resultSet.getString("password");
        return new User(id, name, login, password);
    }

    private Set<Role> getRoles(Long userId) {
        String query = "SELECT * FROM roles r INNER JOIN users_roles ur ON ur.id_role = r.id "
                + "WHERE ur.id_user = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            Set<Role> roles = new HashSet<>();
            while (resultSet.next()) {
                Role role = Role.of(resultSet.getString("name"));
                role.setId(resultSet.getLong("id"));
                roles.add(role);
            }
            return roles;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to get the roles "
                    + "of the user with id: " + userId, exception);
        }
    }

    private boolean deleteRoles(Long userId) {
        String query = "DELETE FROM user_role WHERE id_user = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() != 0;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to delete the user's roles "
                    + "by user id: " + userId, exception);
        }
    }
}

package com.internet.shop.dao.jdbc.impl;

import com.internet.shop.dao.UserDao;
import com.internet.shop.exceptions.DataProcessingException;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.Role;
import com.internet.shop.model.User;
import com.internet.shop.util.ConnectionUtil;
import java.sql.*;
import java.util.*;

@Dao
public class UserDaoJdbcImpl implements UserDao {
    @Override
    public Optional<User> findByLogin(String login) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String query = "SELECT * FROM users WHERE login = ? AND isDeleted = false;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getUserFromResultSet(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find the user with login " + login
                    + " in the database.", e);
        }
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
            addUsersRoles(user, connection);
            return user;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to create the user: "
                    + user.getName(), exception);
        }
    }

    @Override
    public Optional<User> get(Long id) {
        User user = new User();
        String query = "SELECT * FROM user WHERE isDeleted = false AND id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = Optional.of(getUserFromResultSet(resultSet)).get();
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to get the user "
                    + "with id: " + id, exception);
        }
        user.setRoles(getUsersRole(id));
        return Optional.of(user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Getting all users was failed. ", e);
        }
        return users;
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET login = ?, password = ? WHERE user_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            deleteRoles(user.getId());
            addUsersRoles(user, connection);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setLong(3, user.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to update the user "
                    + "with id: " + user.getId(), exception);
        }
        return user;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE user SET deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() != 0;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to delete the user "
                    + "with id: " + id, exception);
        }
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String password = resultSet.getString("password");
        Long id = resultSet.getLong("user_id");
        User user = new User(name, login, password);
        user.setId(id);
        return user;
    }

    private Set<Role> getUsersRole(Long id) {
        String query = "SELECT * FROM role r INNER JOIN user_role ur ON ur.id_role = r.id "
                + "WHERE ur.id_user = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
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
                    + "of the user with id: " + id, exception);
        }
    }

    private void addUsersRoles(User user, Connection connection) throws SQLException {
        String query = "INSERT INTO users_roles (id_user, id_role) VALUES (?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, user.getId());
            for (Role role : user.getRoles()) {
                statement.setLong(2, role.getRoleName().ordinal() + 1);
                statement.executeUpdate();
            }
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

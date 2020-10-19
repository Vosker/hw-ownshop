package com.internet.shop.dao.jdbc.storage;

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
    public User create(User user) {
        String query = "INSERT INTO users (name, login, password, salt) VALUES (?, ?, ?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setBytes(4, user.getSalt());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
            }
            insertUserRoles(user, connection);
            return user;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add user " + user + " to the database.", e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String query = "SELECT * FROM users WHERE login = ? AND isDeleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getUserFromResultSet(resultSet, connection));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find the user with login " + login
                    + " in the database.", e);
        }
    }

    @Override
    public Optional<User> get(Long id) {
        String query = "SELECT * FROM users WHERE user_id = ? AND isDeleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = getUserFromResultSet(resultSet, connection);
            }
            statement.close();
            Set<Role> roles = getUserRoles(id, connection);
            user.setRoles(roles);
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find the user with id " + id
                    + " in the database.", e);
        }
    }

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users WHERE isDeleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet, connection));
            }
            for (User user : users) {
                Long id = user.getId();
                Set<Role> roles = getUserRoles(id, connection);
                user.setRoles(roles);
            }
            return users;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't retrieve users from the database.", e);
        }
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET name = ?, login = ?, password = ?, salt = ? "
                + "WHERE user_id = ? AND isDeleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setBytes(4, user.getSalt());
            statement.setLong(5, user.getId());
            statement.executeUpdate();
            deleteUserRoles(user, connection);
            insertUserRoles(user, connection);
            return user;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update " + user + " in the database.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE users SET isDeleted = true "
                + "WHERE user_id = ? AND isDeleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete the user with id " + id
                    + " from the database.", e);
        }
    }

    private void insertUserRoles(User user, Connection connection) throws SQLException {
        String query = "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?);";
        for (Role role : user.getRoles()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, user.getId());
            statement.setLong(2, getRoleId(role.getRoleName(), connection));
            statement.executeUpdate();
        }
    }

    private void deleteUserRoles(User user, Connection connection) throws SQLException {
        String query = "DELETE FROM users_roles WHERE user_id = ?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, user.getId());
        statement.executeUpdate();
    }

    private Set<Role> getUserRoles(Long id, Connection connection) throws SQLException {
        String query = "SELECT role_name FROM roles JOIN users_roles "
                + "ON users_roles.role_id = roles.role_id "
                + "WHERE user_id = ?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        Set<Role> roles = new HashSet<>();
        while (resultSet.next()) {
            roles.add(Role.of(resultSet.getString("role_name")));
        }
        statement.close();
        return roles;
    }

    private User getUserFromResultSet(ResultSet resultSet, Connection connection)
            throws SQLException {
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String password = resultSet.getString("password");
        byte[] salt = resultSet.getBytes("salt");
        Long id = resultSet.getLong("user_id");
        User user = new User(name, login, password);
        user.setSalt(salt);
        user.setId(id);
        return user;
    }

    private Long getRoleId(Role.RoleName roleName, Connection connection) throws SQLException {
        String query = "SELECT role_id FROM roles WHERE role_name = ?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, roleName.name());
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("role_id");
        }
        throw new RuntimeException("Can't get role ID for " + roleName);
    }
}

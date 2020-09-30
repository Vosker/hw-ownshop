package com.internet.shop.dao.jdbc.impl;

import com.internet.shop.dao.OrderDao;
import com.internet.shop.exceptions.DataProcessingException;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.Order;
import com.internet.shop.model.Product;
import com.internet.shop.util.ConnectionUtil;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class OrderDaoJdbcImpl implements OrderDao {
    @Override
    public List<Order> getUserOrders(Long userId) {
        String query = "SELECT * FROM orders WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(getOrderFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get orders for user with id " + userId, e);
        }
        for (Order order : orders) {
            order.setProducts(getProducts(order.getId()));
        }
        return orders;
    }

    @Override
    public Order create(Order order) {
        String query = "INSERT INTO orders(user_id) VALUE (?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, order.getUserId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                order.setId(resultSet.getLong(1));
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to create the order: "
                    + order.getId(), exception);
        }
        addProducts(order.getProducts(), order.getId());
        return order;
    }

    @Override
    public Optional<Order> get(Long orderId) {
        String query = "SELECT * FROM orders WHERE order_id = ? AND isDeleted = FALSE";
        List<Product> productsInOrder = getProducts(orderId);
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Order searchedOrder = getOrderFromResultSet(resultSet);
                searchedOrder.setProducts(productsInOrder);
                return Optional.of(searchedOrder);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Finding the order by id: "
                    + orderId + "was failed. ", e);
        }
    }

    @Override
    public List<Order> getAll() {
        List<Order> allOrders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE isDeleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allOrders.add(getOrderFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Getting all orders from DB was failed", e);
        }
        for (Order order : allOrders) {
            order.setProducts(getProducts(order.getId()));
        }
        return allOrders;
    }

    @Override
    public Order update(Order order) {
        Long orderId = order.getId();
        List<Product> productsInOrder = order.getProducts();
        String query = "UPDATE orders SET user_id = ? WHERE id = ? "
                + "AND isDeleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, order.getUserId());
            statement.setLong(2, orderId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to update the order "
                    + "with id: " + orderId, exception);
        }
        deleteProducts(orderId);
        addProducts(productsInOrder, orderId);
        return order;
    }

    @Override
    public boolean delete(Long id) {
        deleteProducts(id);
        String query = "UPDATE orders SET isDeleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() != 0;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to delete the order "
                    + "with id: " + id, exception);
        }
    }

    private void addProducts(List<Product> products, long orderId) {
        String query = "INSERT INTO orders_products (order_id, product_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (Product product : products) {
                statement.setLong(1, orderId);
                statement.setLong(2, product.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Adding products to order by order id: "
                    + orderId + " was failed. ", e);
        }
    }

    private List<Product> getProducts(Long orderId) {
        String query = "SELECT * FROM product p INNER JOIN order_product op "
                + "ON op.id_product = p.id WHERE op.id_order = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            List<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                long id = resultSet.getLong("product_id");
                String productName = resultSet.getString("type");
                double price = resultSet.getDouble("price");
                Product product = new Product(productName, price);
                product.setId(id);
                products.add(product);
            }
            return products;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to get the products "
                    + "of the order with id: " + orderId, exception);
        }
    }

    private boolean deleteProducts(Long orderId) {
        String query = "DELETE FROM order_product WHERE id_order = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderId);
            return statement.executeUpdate() != 0;
        } catch (SQLException exception) {
            throw new DataProcessingException("Failed to delete the order's "
                    + "products with id: " + orderId, exception);
        }
    }

    private Order getOrderFromResultSet(ResultSet resultSet) throws SQLException {
        Long orderId = resultSet.getLong("order_id");
        Long userId = resultSet.getLong("user_id");
        Order order = new Order(userId);
        order.setId(orderId);
        return order;
    }
}

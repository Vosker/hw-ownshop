package com.internet.shop.dao.jdbc.impl;

import com.internet.shop.dao.ShoppingCartDao;
import com.internet.shop.exceptions.DataProcessingException;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class ShoppingCartDaoJdbcImpl implements ShoppingCartDao {
    @Override
    public Optional<ShoppingCart> getByUserId(Long userId) {
        String query = "SELECT * FROM shopping_carts WHERE user_id = ? AND isDeleted = FALSE";
        Optional<ShoppingCart> neededCart = Optional.empty();
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                neededCart = Optional.of(composeExtractedCart(resultSet));
            }
            if (neededCart.isEmpty()) {
                return neededCart;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't find cart by specified cartID: "
                    + userId, e);
        }
        neededCart.get().setProducts(composeProductsListByCartId(neededCart.get().getId()));
        return neededCart;
    }

    @Override
    public ShoppingCart create(ShoppingCart cart) {
        String query = "INSERT INTO shopping_carts (user_id) VALUES (?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, cart.getUserId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                cart.setId(resultSet.getLong(1));
            }
            return cart;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create provided cart: " + cart, e);
        }
    }

    @Override
    public Optional<ShoppingCart> get(Long id) {
        String query = "SELECT * FROM shopping_carts WHERE cart_id = ? AND isDeleted = FALSE";
        List<Product> productsInCart = composeProductsListByCartId(id);
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ShoppingCart searchedCart = composeExtractedCart(resultSet);
                searchedCart.setProducts(productsInCart);
                return Optional.of(searchedCart);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't find cart by specified cartID: "
                    + id, e);
        }
    }

    @Override
    public List<ShoppingCart> getAll() {
        List<ShoppingCart> allCarts = new ArrayList<>();
        String query = "SELECT * FROM shopping_carts WHERE isDeleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allCarts.add(composeExtractedCart(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get carts from DataBase", e);
        }
        for (ShoppingCart cart : allCarts) {
            cart.setProducts(composeProductsListByCartId(cart.getId()));
        }
        return allCarts;
    }

    @Override
    public ShoppingCart update(ShoppingCart cart) {
        deleteProducts(cart.getId());
        addProducts(cart.getProducts(), cart.getId());
        return cart;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE shopping_carts"
                + " SET isDeleted = TRUE "
                + "WHERE cart_id = ? "
                + "AND isDeleted = FALSE";
        deleteProducts(id);
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cart by specified cartID: "
                    + id, e);
        }
    }

    private ShoppingCart composeExtractedCart(ResultSet resultSet) throws SQLException {
        long cartId = resultSet.getLong("cart_id");
        long userId = resultSet.getLong("user_id");
        ShoppingCart newCart = new ShoppingCart(userId);
        newCart.setId(cartId);
        return newCart;
    }

    private List<Product> composeProductsListByCartId(long cartId) {
        List<Product> cartProducts = new ArrayList<>();
        String query = "SELECT product_id, product_name, price FROM products "
                + "JOIN shopping_carts_products USING (product_id) "
                + "WHERE cart_id = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, cartId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("product_id");
                String productName = resultSet.getString("product_name");
                double price = resultSet.getDouble("price");
                Product productFromCart = new Product(productName, price);
                productFromCart.setId(id);
                cartProducts.add(productFromCart);
            }
            return cartProducts;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get Products from DataBase "
                    + "by specified cartID:" + cartId, e);
        }
    }

    private void addProducts(List<Product> products, long cartId) {
        String query = "INSERT INTO shopping_carts_products (cart_id, product_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (Product product : products) {
                statement.setLong(1, cartId);
                statement.setLong(2, product.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add products by specified cartID: "
                    + cartId, e);
        }
    }

    private void deleteProducts(long cartId) {
        String query = "DELETE FROM shopping_carts_products WHERE cart_id = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, cartId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete products by specified cartID: "
                    + cartId, e);
        }
    }
}

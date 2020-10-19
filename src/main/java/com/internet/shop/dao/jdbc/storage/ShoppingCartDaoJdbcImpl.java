package com.internet.shop.dao.jdbc.storage;

import com.internet.shop.dao.ShoppingCartDao;
import com.internet.shop.exceptions.DataProcessingException;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                neededCart = Optional.of(createExtractedCart(resultSet));
            }
            if (neededCart.isEmpty()) {
                return neededCart;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find cart by cartID: "
                    + userId, e);
        }
        neededCart.get().setProducts(createProductsListByCartId(neededCart.get().getId()));
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
            throw new DataProcessingException("Can't create cart: " + cart, e);
        }
    }

    @Override
    public Optional<ShoppingCart> get(Long id) {
        ShoppingCart shoppingCart = new ShoppingCart();
        String query = "SELECT * FROM shopping_carts WHERE cart_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                shoppingCart = createExtractedCart(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get shopping cart with ID = "
                    + id, e);
        }
        shoppingCart.setProducts(createProductsListByCartId(shoppingCart.getId()));
        return Optional.of(shoppingCart);
    }

    @Override
    public List<ShoppingCart> getAll() {
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        String query = "SELECT * FROM shopping_carts WHERE isDeleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                shoppingCarts.add(createExtractedCart(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get shopping carts", e);
        }
        for (ShoppingCart shoppingCart : shoppingCarts) {
            shoppingCart.setProducts(createProductsListByCartId(shoppingCart.getId()));
        }
        return shoppingCarts;
    }

    @Override
    public ShoppingCart update(ShoppingCart cart) {
        String queryToDeleteProducts = "DELETE FROM shopping_carts_products WHERE cart_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryToDeleteProducts)) {
            statement.setLong(1, cart.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete products from shopping cart - "
                    + cart, e);
        }
        addProduct(cart);
        return cart;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE shopping_carts SET isDeleted = true WHERE cart_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete shopping cart with ID = " + id, e);
        }
    }

    private ShoppingCart createExtractedCart(ResultSet resultSet) throws SQLException {
        long cartId = resultSet.getLong("cart_id");
        long userId = resultSet.getLong("user_id");
        ShoppingCart newCart = new ShoppingCart(userId);
        newCart.setId(cartId);
        return newCart;
    }

    private List<Product> createProductsListByCartId(long cartId) {
        List<Product> cartProducts = new ArrayList<>();
        String query = "SELECT product_id, name, price FROM products p "
                + "JOIN shopping_carts_products scp "
                + "ON p.product_id = scp.product_id "
                + "WHERE cart_id = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, cartId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("product_id");
                String productName = resultSet.getString("name");
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

    private ShoppingCart addProduct(ShoppingCart shoppingCart) {
        String queryToUpdateProducts = "INSERT INTO shopping_carts_products(cart_id, product_id) "
                + "VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryToUpdateProducts)) {
            statement.setLong(1, shoppingCart.getId());
            for (Product product : shoppingCart.getProducts()) {
                statement.setLong(2, product.getId());
                statement.executeUpdate();
            }
            return shoppingCart;
        } catch (SQLException e) {
            throw new DataProcessingException("Not able to update cart " + shoppingCart, e);
        }
    }
}

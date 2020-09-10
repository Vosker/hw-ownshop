package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.model.User;
import com.internet.shop.service.ProductService;
import com.internet.shop.service.ShoppingCartService;
import com.internet.shop.service.UserService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InjectDataController extends HttpServlet {
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private final ProductService productService =
            (ProductService) injector.getInstance(ProductService.class);
    private final UserService userService =
            (UserService) injector.getInstance(UserService.class);
    private final ShoppingCartService shoppingCartService =
            (ShoppingCartService) injector.getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Product banana = new Product("Banana", 1);
        Product orange = new Product("Orange", 2);
        Product apple = new Product("Apple", 3);
        productService.create(banana);
        productService.create(orange);
        productService.create(apple);

        User nikita = new User("Nikita", "qwe", "rty");
        User daniil = new User("Daniil", "asd", "dfg");
        User anton = new User("Anton", "zxc", "vbn");
        userService.create(nikita);
        userService.create(daniil);
        userService.create(anton);

        ShoppingCart shoppingCart1 = new ShoppingCart(nikita.getId());
        ShoppingCart shoppingCart2 = new ShoppingCart(daniil.getId());
        ShoppingCart shoppingCart3 = new ShoppingCart(anton.getId());
        shoppingCartService.create(shoppingCart1);
        shoppingCartService.create(shoppingCart2);
        shoppingCartService.create(shoppingCart3);

        req.getRequestDispatcher("/WEB-INF/views/injectData.jsp").forward(req, resp);
    }
}

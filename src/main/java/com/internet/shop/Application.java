package com.internet.shop;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.model.User;
import com.internet.shop.service.OrderService;
import com.internet.shop.service.ProductService;
import com.internet.shop.service.ShoppingCartService;
import com.internet.shop.service.UserService;

public class Application {

    private static Injector injector = Injector.getInstance("com.internet.shop");

    public static void main(String[] args) {
        ProductService productService = (ProductService) injector.getInstance(ProductService.class);
        System.out.println("Products");
        Product banana = new Product("banana", 1);
        productService.create(banana);
        Product apple = new Product("apple", 12);
        productService.create(apple);
        Product orange = new Product("orange", 123);
        productService.create(orange);
        System.out.println(productService.getAll().toString());
        System.out.println("Products after update");
        Product product = new Product("banana", 11);
        product.setId(1L);
        productService.update(product);
        System.out.println(productService.getAll().toString());
        System.out.println("Products after remove");
        productService.delete(3L);
        System.out.println(productService.getAll().toString());
        UserService userService = (UserService) injector.getInstance(UserService.class);
        System.out.println("Users");
        User user1 = new User("Nikita", "qwer", "qwerty");
        User user2 = new User("Vaganov", "asdf", "asdfgh");
        User user3 = new User("Anton", "aassdf", "aaasdfgh");
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        System.out.println(userService.getAll().toString());
        System.out.println("Users after update");
        String usersName = "Kostya";
        User user = new User(usersName, user2.getLogin(), user2.getPassword());
        user.setId(2L);
        userService.update(user);
        System.out.println(userService.getAll().toString());
        System.out.println("Users after remove");
        userService.delete(3L);
        System.out.println(userService.getAll().toString());
        ShoppingCartService shoppingCartService =
                (ShoppingCartService) injector.getInstance(ShoppingCartService.class);
        System.out.println("Shopping carts");
        ShoppingCart shoppingCart1 = new ShoppingCart(1L);
        shoppingCartService.addProduct(shoppingCart1, banana);
        shoppingCartService.addProduct(shoppingCart1, apple);
        shoppingCartService.create(shoppingCart1);
        System.out.println("First cart");
        System.out.println(shoppingCartService.getByUserId(1L));
        ShoppingCart shoppingCart2 = new ShoppingCart(2L);
        shoppingCartService.addProduct(shoppingCart2, banana);
        shoppingCartService.create(shoppingCart2);
        System.out.println("Second cart");
        System.out.println(shoppingCartService.getByUserId(2L));
        shoppingCartService.deleteProduct(shoppingCart1, banana);
        System.out.println("Delete banana from first cart");
        System.out.println(shoppingCartService.getByUserId(1L));
        OrderService orderService = (OrderService) injector.getInstance(OrderService.class);
        System.out.println("Orders");
        orderService.completeOrder(shoppingCart1);
        orderService.completeOrder(shoppingCart2);
        ShoppingCart shoppingCart3 = new ShoppingCart(2L);
        shoppingCartService.addProduct(shoppingCart3, banana);
        shoppingCartService.addProduct(shoppingCart3, apple);
        shoppingCartService.create(shoppingCart3);
        orderService.completeOrder(shoppingCart3);
        System.out.println(orderService.getAll().toString());
        System.out.println("Orders of second user");
        System.out.println(orderService.getUserOrders(2L).toString());
        System.out.println("All orders");
        System.out.println(orderService.getAll());

    }
}

package com.internet.shop;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.service.ProductService;

public class Application {

    private static Injector injector = Injector.getInstance("com.internet.shop");

    public static void main(String[] args) {
        ProductService productService = (ProductService) injector.getInstance(ProductService.class);
        System.out.println("Initial DB");
        productService.getAll().forEach(System.out::println);
        Product banana = new Product("banana", 1);
        System.out.println("Add product to DB");
        banana = productService.create(banana);
        productService.getAll().forEach(System.out::println);

        System.out.println("Get product from DB");
        System.out.println(productService.get(banana.getId()).toString());

        System.out.println("Update product in DB");
        banana.setName("Apple");
        banana.setPrice(2);
        productService.update(banana);
        System.out.println(productService.get(banana.getId()));

        System.out.println("Get all products");
        productService.getAll().forEach(System.out::println);

        System.out.println("Delete product");
        System.out.println(productService.delete(banana.getId()));
        productService.getAll().forEach(System.out::println);
    }
}

package com.internet.shop;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.service.ProductService;

public class Application {

    private static final Injector injector = Injector.getInstance("com");

    public static void main(String[] args) {
        ProductService productService = (ProductService) injector.getInstance(ProductService.class);

        productService.create(new Product("banana", 1));
        productService.create(new Product("apple", 12));
        productService.create(new Product("orange", 123));
        System.out.println(productService.getAll().toString());

        System.out.println("After update");
        for (int i = 0; i < 3; i++) {
            String name = "maslo";
            Product product = new Product(name, i);
            product.setId(i + 1L);
            productService.update(product);
        }
        System.out.println(productService.getAll().toString());

        System.out.println("After remove");
        productService.delete(1L);
        productService.delete(2L);
        System.out.println(productService.getAll().toString());
    }
}

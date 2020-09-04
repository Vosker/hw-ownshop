package com.dao.impl;

import com.dao.ProductDao;
import com.lib.Dao;
import com.models.Product;
import com.storage.Storage;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Dao
public class ProductDaoImpl implements ProductDao {

    @Override
    public Product create(Product product) {
        Storage.addProduct(product);
        return product;
    }

    @Override
    public Optional<Product> get(Long id) {
        return Storage.products.stream().filter(product -> Objects.equals(product.getId(), (id))).findFirst();
    }

    @Override
    public List<Product> getAll() {
        return Storage.products;
    }

    @Override
    public Product update(Product product) {
        for (int i = 0; i < Storage.products.size(); i++) {
            if (product.getId().equals(Storage.products.get(i).getId())) {
                Storage.products.set(i, product);
            }
        }
        return product;
    }

    @Override
    public boolean delete(Long id) {
        return Storage.products.removeIf(product -> Objects.equals(product.getId(), id));
    }
}

package com.dao;

import com.lib.Dao;
import com.models.Product;
import com.storage.Storage;
import java.util.List;
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
        for (int i = 0; i < Storage.products.size(); i++) {
            if (id.equals(Storage.products.get(i).getId())) {
                return Optional.ofNullable(Storage.products.get(i));
            }
        }
        return Optional.empty();
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
        for (int i = 0; i < Storage.products.size(); i++) {
            if (Storage.products.get(i).getId().equals(id)) {
                Storage.products.remove(i);
                return true;
            }
        }
        return false;
    }
}

package com.internet.shop.dao.impl;

import com.internet.shop.dao.UserDao;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.User;
import com.internet.shop.storage.Storage;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Dao
public class UserDaoImpl implements UserDao {

    @Override
    public User create(User user) {
        Storage.addUser(user);
        return user;
    }

    @Override
    public Optional<User> get(Long id) {
        return Storage.users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public List<User> getAll() {
        return Storage.users;
    }

    @Override
    public User update(User user) {
        IntStream.range(0, Storage.users.size())
                .filter(i -> Storage.users.get(i).getId().equals(user.getId()))
                .forEach(item -> Storage.users.set(item, user));
        return user;
    }

    @Override
    public boolean delete(Long id) {
        return Storage.users.removeIf(user -> user.getId().equals(id));
    }
}

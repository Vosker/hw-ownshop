package com.internet.shop.model;

import java.util.Set;

public class User {
    private Long id;
    private String name;
    private String login;
    private String password;
    private Set<Role> roles;
    private byte[] salt;

    public User(String name, String login,
                String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public User(Long id, String name,
                String login, String password) {
        this(name, login, password);
        this.id = id;
    }

    public User(String name, String login, String password, Set<Role> roles, byte[] salt) {
        this(name, login, password);
        this.roles = roles;
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        Set<Role> test = roles;
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", login='" + login + '\''
                + ", password='" + password + '\'' + '}';
    }
}

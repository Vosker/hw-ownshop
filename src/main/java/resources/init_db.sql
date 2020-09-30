CREATE SCHEMA `own-shop` DEFAULT CHARACTER SET utf8;

CREATE TABLE `own-shop`.`products`
(
    `id`      BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(255) NOT NULL,
    `price`   DOUBLE       NOT NULL,
    `isDeleted` TINYINT    NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE `own-shop`.`roles`
(
    `id`   BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `own-shop`.`users`
(
    `id`        BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(255) NOT NULL,
    `login`     VARCHAR(255) NOT NULL,
    `password`  VARCHAR(255) NOT NULL,
    `isDeleted` TINYINT GENERATED ALWAYS AS (0) VIRTUAL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `own-shop`.`users_roles`
(
    `user_id` BIGINT(11) NOT NULL,
    `role_id` BIGINT(11) NOT NULL,
    INDEX `ur_role_id_fk_idx` (`role_id` ASC) VISIBLE,
    CONSTRAINT `ur_role_id_fk`
        FOREIGN KEY (`role_id`)
            REFERENCES `own-shop`.`roles` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `ur_user_id_fk`
        FOREIGN KEY (`user_id`)
            REFERENCES `own-shop`.`users` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE `own-shop`.`orders`
(
    `id`        BIGINT(11) NOT NULL AUTO_INCREMENT,
    `user_id`   BIGINT(11) NOT NULL,
    `isDeleted` TINYINT GENERATED ALWAYS AS (0) VIRTUAL,
    CONSTRAINT `o_user_id_fk`
        FOREIGN KEY (`user_id`)
            REFERENCES `own-shop`.`users` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    PRIMARY KEY (`id`)
);

CREATE TABLE `own-shop`.`shopping_carts`
(
    `id`        BIGINT(11) NOT NULL AUTO_INCREMENT,
    `user_id`   BIGINT(11) NOT NULL,
    `isDeleted` TINYINT GENERATED ALWAYS AS (0) VIRTUAL,
    CONSTRAINT `sc_user_id_fk`
        FOREIGN KEY (`user_id`)
            REFERENCES `own-shop`.`users` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    PRIMARY KEY (`id`)
);

CREATE TABLE `own-shop`.`shopping_carts_products`
(
    `cart_id`    BIGINT(11) NOT NULL,
    `product_id` BIGINT(11) NOT NULL,
    INDEX `scu_product_id_fk_idx` (`product_id` ASC) VISIBLE,
    CONSTRAINT `scu_cart_id_fk`
        FOREIGN KEY (`cart_id`)
            REFERENCES `own-shop`.`shopping_carts` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `scu_product_id_fk`
        FOREIGN KEY (`product_id`)
            REFERENCES `own-shop`.`products` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE `own-shop`.`orders_products`
(
    `order_id`   BIGINT(11) NOT NULL,
    `product_id` BIGINT(11) NOT NULL,
    INDEX `op_product_id_fk_idx` (`product_id` ASC) VISIBLE,
    CONSTRAINT `op_order_id_fk`
        FOREIGN KEY (`order_id`)
            REFERENCES `own-shop`.`orders` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `op_product_id_fk`
        FOREIGN KEY (`product_id`)
            REFERENCES `own-shop`.`products` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);
INSERT INTO `own-shop`.roles(name) VALUE ('ADMIN');
INSERT INTO `own-shop`.roles(name) VALUE ('USER');
INSERT INTO `own-shop`.products(name, price) VALUES ('Lemon', 10);
INSERT INTO `own-shop`.products(name, price) VALUES ('Mellon', 20);
INSERT INTO `own-shop`.products(name, price) VALUES ('Watermellon', 30);
INSERT INTO `own-shop`.users(name, login, password) VALUES ('Nikita', 'qwe', 'rty');
INSERT INTO `own-shop`.users(name, login, password) VALUES ('Anton', 'asd', 'fgh');
INSERT INTO `own-shop`.users(name, login, password) VALUES ('Daniil', 'zxc', 'vbn');
INSERT INTO `own-shop`.users_roles(user_id, role_id) VALUES (1, 1);
INSERT INTO `own-shop`.users_roles(user_id, role_id) VALUES (2, 2);
INSERT INTO `own-shop`.users_roles(user_id, role_id) VALUES (3, 2);
INSERT INTO `own-shop`.shopping_carts(user_id) VALUES (2);
INSERT INTO `own-shop`.shopping_carts(user_id) VALUES (3);

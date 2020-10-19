CREATE SCHEMA `own-shop` DEFAULT CHARACTER SET utf8;

CREATE TABLE `own-shop`.`products`
(
    `product_id`  BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255) NOT NULL,
    `price`       DOUBLE       NOT NULL,
    `isDeleted`   TINYINT    NOT NULL DEFAULT 0,
    PRIMARY KEY (`product_id`)
);

CREATE TABLE `own-shop`.`roles`
(
    `role_id`   BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(255) NOT NULL,
    PRIMARY KEY (`role_id`)
);

CREATE TABLE `own-shop`.`users`
(
    `user_id`   BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(255) NOT NULL,
    `login`     VARCHAR(255) NOT NULL,
    `password`  VARCHAR(255) NOT NULL,
    `salt`      VARBINARY(255) NOT NULL,
    `isDeleted` TINYINT NOT NULL NULL DEFAULT 0,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `own-shop`.`users_roles`
(
    `id`      BIGINT(11) NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT(11) NOT NULL,
    `role_id` BIGINT(11) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `user_roles`
        FOREIGN KEY (`role_id`)
        REFERENCES `own-shop`.`roles` (`role_id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT `role_roles`
        FOREIGN KEY (`user_id`)
        REFERENCES `own-shop`.`users` (`user_id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION);

CREATE TABLE `own-shop`.`shopping_carts`
(
    `cart_id` BIGINT(11) NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT(11) NOT NULL,
    `isDeleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`cart_id`),
    CONSTRAINT `user_cart`
        FOREIGN KEY (`user_id`)
            REFERENCES `own-shop`.`users` (`user_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE `own-shop`.`shopping_carts_products`
(
    `id`         BIGINT(11) NOT NULL AUTO_INCREMENT,
    `cart_id`    BIGINT(11) NOT NULL,
    `product_id` BIGINT(11) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `cart`
        FOREIGN KEY (`cart_id`)
            REFERENCES `own-shop`.`shopping_carts` (`cart_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `product`
        FOREIGN KEY (`product_id`)
            REFERENCES `own-shop`.`products` (`product_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE `own-shop`.`orders`
(
    `order_id`  BIGINT(11) NOT NULL AUTO_INCREMENT,
    `user_id`   BIGINT(11) NOT NULL,
    `isDeleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`order_id`),
    CONSTRAINT `user`
        FOREIGN KEY (`user_id`)
            REFERENCES `own-shop`.`users` (`user_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE `own-shop`.`orders_products`
(
    `id`         BIGINT(11) NOT NULL AUTO_INCREMENT,
    `order_id`   BIGINT(11) NOT NULL,
    `product_id` BIGINT(11) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `product`
        FOREIGN KEY (`product_id`)
            REFERENCES `own-shop`.`products` (`product_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `order`
        FOREIGN KEY (`order_id`)
            REFERENCES `own-shop`.`orders` (`order_id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

INSERT INTO `own-shop`.roles(name) VALUE ('ADMIN');
INSERT INTO `own-shop`.roles(name) VALUE ('USER');

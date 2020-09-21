CREATE SCHEMA `own_shop` DEFAULT CHARACTER SET utf8;

CREATE TABLE `own_shop`.`products`
(
    `productId`  BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(255) NOT NULL,
    `price`      DOUBLE       NOT NULL,
    `isDeleted`  TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`productId`)
);
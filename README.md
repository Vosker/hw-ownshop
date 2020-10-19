# INTERNET SHOP

The main idea of this project is to implement a basic model of secured internet shop.

I developed this project using SOLID principles. Also, I added authorization and authentication with RBAC filter strategy.

#### As user, you can:
- register on the website + log in and out
- check the list of available products
- add and remove products from shopping cart (each user has own shopping cart)
- create an order

#### As admin, you can:
- view and edit a list of available products
- view and delete users
- view the list of orders of a specific user

#### Technologies used:
- Java 8
- Servlets
- Filters
- JSP
- Bootstrap
- Maven
- Tomcat
- DAO pattern
- MySQL
- JDBC

#### To run this project on your PC you need to:

1. Configure Tomcat
2. Set up MySql DB and Workbench
3. Run the script init_db.sql located in 'resources' package
4. Use your credentials in ConnectionUtil class

# BrokageFirm

## Introduction

There are couple of endpoints which are listed in postman scripts. 

### Simple Scenario

1- Create Employee with admin permissions

2- Create Customer with user permissions

3- Get Token with Employee

4- Deposit Amount for customer with Employee Token

5- Create Buy Order with given body and Employee Token

6- Check order list with range for date or simple customer name(id)

7- You can also try withdraw try money from customer.

## Details

Order, Asset, Customer, Employee are persisted to h2 db.

There are couple of hibernate validations to check body.

Permissions are checked for example Customer can not change other Customer.

Admin has special access for some endpoint unlike customer.

Simple JWT token is used for authorization and authentication.

There is a order scheduler job which behaves like master-slave. Only one instance can complete 50 orders and others are waiting it for end it. I used pessimistic lock in database to avoid other tools.

### Internal

User orders a asset and this order is stored in h2 database. After storing it, scheduler job gets pending orders. Checks validations and simply try to finish it(There is a retry mechanism to avoid some errors)

If it is completed status is MATCHED otherwise it is CANCELED.

Customer has TRY amount and Employee or himself/herself can deposit or withdraw money from bank.

### BASIC Validations

1- Customer money check for BUY ORDER

2- Total size is checked for SELL ORDER

3- Employee name or Customer name must be unique.

4- JWT Filter checks jwt token and security validates it.


# Missing Points

Tests are not written because it gets bigger.

Security is too simple and it can not be used in production.



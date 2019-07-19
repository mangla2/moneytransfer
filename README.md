# Money Transfer between accounts

A Java RESTful API for money transfers between users accounts

Using:

⋅⋅* JAX-RS API
⋅⋅* H2 in memory database
⋅⋅* Log4j
⋅⋅* Jetty Container (for Test and Demo app)
⋅⋅* Apache HTTP Client

How to run demo app
------

Clone from GitHub:
```
git clone https://github.com/mangla2/moneytransfer.git
```
Build the application:
```
mvn clean install
```
Executing Demo App:

After installation completes and tests are passed, you may find a .jar-file in target-folder.Run that jar.
```
java -jar moneytransfer-1.0-jar-with-dependencies.jar
```

The demo app starts a jetty server on localhost port 8081 .
An H2 in memory database initialized with some sample user and account data. You can check the below services from any rest client.

Available Services
======

| HTTP METHOD | PATH | USAGE |
| --- | --- | --- |
| GET | /user/{email} | get user by email |
| GET | /user/account/{email} | get all user accounts by email |
| GET | /user/all | get all users |
| POST | /user/create | create a new user |
| DELETE | /user/delete/{email} | remove user by email |
| POST | /account/create | create a new account  |
| GET | /account/{accountNumber} |  get account info by account number|
| GET | /account/all | get all accounts available |
| DELETE | /account/delete/{accountNumber} | remove account by account number |
| PUT | /account/withdraw/{accountNumber}?amount= | withdraw money from account |
| PUT | /account/deposit/{accountNumber}?amount= | deposit money to account |
| GET | /account/transactions/{accountNumber} | get all transaction history by account number |
| PUT | /transfer | perform transaction between 2 accounts |

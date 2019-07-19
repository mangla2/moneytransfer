# Money Transfer between accounts

A Java RESTful API for money transfers between users accounts

Using:

* JAX-RS API
* H2 in memory database
* Log4j
* Jetty Container (for Test and Demo app)
* Apache HTTP Client

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
Starting the application:

After installation completes and tests are passed, you may find a .jar-file in target-folder. Run that jar.
```
java -jar target/moneytransfer-1.0-jar-with-dependencies.jar
```

The demo app starts a jetty server on localhost port 8081 .
An H2 in memory database initialized with some sample user and account data. You can check the below services from any rest client.

Features
======

* Create user. An account will be implicity created with default balance and currencyCode
* Create an account or add multiple accounts for a unique user email
* Deposit to or withdraw money from an account.
* Transfer money between accounts.
* List all users or accounts stored in the DB.
* Delete users and accounts 
* Get all transaction history for a given account
* In-memory database to store all transactions and accounts.
* Thread-safe
* Error capturing and returning appropriate Error response codes.
* Unit tests using TDD.

Available API's
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


Error Codes in Response
------

| ErrorCode | Desc |
| --- | --- |
| 501 | ERROR_CODE_VALIDATION |
| 502 | ERROR_CODE_PROCESSING |
| 503 | ERROR_CODE_EXCEPTION |
| 504 | ERROR_CODE_NONE |

Using the REST API
------
### Creating User
### Creating Account
### Get all users
### Get all accounts of a user
### Deposit or Withdraw Amount
### Transfer money
### Transaction History

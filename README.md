# Money Transfer between accounts

A Java RESTful API for money transfers between users accounts

Using:

* JAX-RS API
* H2 in memory database
* Log4j
* Jetty Container
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

* Create user. An account will be implicity created with default balance(5000) and currencyCode(GBP)
* Create an account or add multiple accounts for a unique user email
* Deposit to or withdraw money from an account.
* Transfer money between accounts.
* Multiple currencies supported for money transfer
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
URL:- http://localhost:8081/user/create <br/>
Request Body
```
{
  "firstName": "Mark",
  "lastName": "Woods",
  "email": "mark.woods@app.com",
  "currencyCode":"USD"
}
```
Response 
```
{
    "status": true,
    "data": {
        "firstName": "Mark",
        "lastName": "Woods",
        "email": "mark.woods@app.com",
        "accounts": [
            {
                "accountNumber": "601729781134364672",
                "balance": 5000,
                "currencyCode": "GBP",
                "transactionsList": []
            }
        ]
    }
}
```

### Creating Account
URL:- http://localhost:8081/account/create <br/>
Request
```
{
  "email":"steve.w@email.com",
  "balance":"50000.00",
  "currencyCode":"GBP"
}
```
Response

```
{
    "status": true,
    "data": {
        "accountNumber": "601755249463152640",
        "email": "steve.w@email.com",
        "balance": 50000,
        "currencyCode": "GBP"
    }
}
```

### Get all users
URL:- http://localhost:8081/user/all <br/>
Response

```
{
    "status": true,
    "data": [
        {
            "firstName": "Mark",
            "lastName": "Boucher",
            "email": "mark.b@email.com",
            "accounts": [
                {
                    "accountNumber": "701729781134364700",
                    "balance": 5000,
                    "currencyCode": "GBP"
                }
            ]
        },
        {
            "firstName": "Steve",
            "lastName": "Waugh",
            "email": "steve.w@email.com",
            "accounts": [
                {
                    "accountNumber": "801729781134364219",
                    "balance": 500,
                    "currencyCode": "EUR"
                },
                {
                    "accountNumber": "601755249463152640",
                    "balance": 50000,
                    "currencyCode": "GBP"
                }
            ]
        }
    ]
}
```

### Get all accounts of a user
URL:- http://localhost:8081/user/account/steve.w@email.com <br/>
Response

```
{
    "status": true,
    "data": [
        {
            "accountNumber": "801729781134364219",
            "balance": 500,
            "currencyCode": "EUR"
        },
        {
            "accountNumber": "601755249463152640",
            "balance": 50000,
            "currencyCode": "GBP"
        }
    ]
}
```

### Deposit or Withdraw Amount
URL:- http://localhost:8081/account/deposit/701729781134364700?amount=100 <br/>
Response

```
{
  "status": true,
  "data": "Amount 100EUR has been deposited successfully"
}
```

URL:- http://localhost:8081/account/withdraw/701729781134364700?amount=100

```
{
  "status": true,
  "data": "Amount 100EUR has been withdrawn successfully"
}
```

### Transfer money
URL: http://localhost:8081/transfer <br/>
Request Body

```
{
  "accountFrom":"801729781134364219",
  "accountTo":"701729781134364700",
  "amount":"100",
  "notes":"Pilot project"
}
```

Response
```
{
  "status": true,
  "data": {
     "transactionId": "1563540411921",
     "amount": 100,
     "createdAt": "2019-07-19 18:16:51",
     "currencyCode": "EUR"
  },
  "message": "Transaction is successful"
}
```

### Transaction History
URL: http://localhost:8081/account/transactions/601729781134364672 <br/>

Response
```
{
    "status": true,
    "data": {
        "balance": 400,
        "currencyCode": "EUR",
        "transactionList": [
            {
                "transactionId": "1563540285651",
                "amount": 100,
                "accountTo": "801729781134364219",
                "type": "CREDIT",
                "notes": "CREDIT",
                "createdAt": "2019-07-19 18:14:45",
                "currencyCode": "EUR"
            },
            {
                "transactionId": "1563540331826",
                "amount": 100,
                "accountTo": "801729781134364219",
                "type": "DEBIT",
                "notes": "DEBIT",
                "createdAt": "2019-07-19 18:15:31",
                "currencyCode": "EUR"
            },
            {
                "transactionId": "1563540411921",
                "amount": 100,
                "accountTo": "701729781134364700",
                "type": "DEBIT",
                "notes": "Pilot project",
                "createdAt": "2019-07-19 18:16:51",
                "currencyCode": "EUR"
            }
        ]
    }
}
```

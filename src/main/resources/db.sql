DROP TABLE IF EXISTS users;

CREATE TABLE users(
   id INT(11) AUTO_INCREMENT PRIMARY KEY,
   firstName VARCHAR(100) NOT NULL,
   lastName VARCHAR(100) NOT NULL,
   email VARCHAR(80) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS account;

CREATE TABLE account(
  accountNumber VARCHAR(20) NOT NULL PRIMARY KEY,
  userId INT(11) NOT NULL,
  balance DECIMAL(10) NOT NULL,
  currencyCode VARCHAR(5) NOT NULL,
  FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE transaction(
  transactionId LONG(20) NOT NULL PRIMARY KEY,
  accountFrom VARCHAR(20) NOT NULL,
  accountTo VARCHAR(20) NOT NULL,
  amount DECIMAL(10) NOT NULL,
  notes VARCHAR(100),
  createdAt VARCHAR(30),
  currencyCode VARCHAR(5) NOT NULL,
);

INSERT INTO users(firstName, lastName, email) VALUES ('Shubham','Mangla','shubham.mangla@email.com');
INSERT INTO users(firstName, lastName, email) VALUES ('Mark','Boucher','mark.b@email.com');
INSERT INTO users(firstName, lastName, email) VALUES ('Steve','Waugh','steve.w@email.com');
INSERT INTO account(accountNumber, userId, balance, currencyCode) VALUES ('601729781134364672','1','50000','INR');
INSERT INTO account(accountNumber, userId, balance, currencyCode) VALUES ('701729781134364700','2','5000','GBP');
INSERT INTO account(accountNumber, userId, balance, currencyCode) VALUES ('801729781134364219','3','500','EUR');
INSERT INTO transaction(transactionId,accountFrom,accountTo,amount,notes,createdAt,currencyCode) VALUES('147847834555','1845878957848','1845878257848','10000','Demo',NOW(),'INR');
INSERT INTO transaction(transactionId,accountFrom,accountTo,amount,notes,createdAt,currencyCode) VALUES('147847854555','1845878157848','1845878257848','10','Demo',NOW(),'EUR');
INSERT INTO transaction(transactionId,accountFrom,accountTo,amount,notes,createdAt,currencyCode) VALUES('147847854556','1845878257848','1845878157848','10','Demo',NOW(),'EUR');



commit;
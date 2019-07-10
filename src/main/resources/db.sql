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

--INSERT INTO users(firstName, lastName, email) VALUES ('Shubham','Mangla','shubham.mangla@email.com');
--INSERT INTO users(firstName, lastName, email) VALUES ('Mark','Boucher','mark.b@email.com');
--INSERT INTO users(firstName, lastName, email) VALUES ('Steve','Waugh','steve.w@email.com');

commit;
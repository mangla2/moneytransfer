DROP TABLE IF EXISTS users;

CREATE TABLE users(
   id INT(11) AUTO_INCREMENT PRIMARY KEY,
   firstName VARCHAR(100) NOT NULL,
   lastName VARCHAR(100) NOT NULL,
   email VARCHAR(80) NOT NULL
);

--INSERT INTO users(firstName, lastName, email) VALUES ('Shubham','Mangla','shubham.mangla@email.com');
--INSERT INTO users(firstName, lastName, email) VALUES ('Mark','Boucher','mark.b@email.com');
--INSERT INTO users(firstName, lastName, email) VALUES ('Steve','Waugh','steve.w@email.com');

commit;
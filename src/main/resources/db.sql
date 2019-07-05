DROP TABLE IF EXISTS users;

CREATE TABLE users(
   id INT AUTO_INCREMENT,
   firstName VARCHAR(100),
   lastName VARCHAR(100),
   email VARCHAR(80),
   PRIMARY KEY(id),
   UNIQUE INDEX(email)
);

INSERT INTO users(firstName, lastName, email) VALUES ('Shubham','Mangla','shubham.mangla@email.com');
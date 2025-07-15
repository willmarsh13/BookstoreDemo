DELETE
FROM book;
ALTER TABLE book AUTO_INCREMENT = 1001;

DELETE
FROM category;
ALTER TABLE category AUTO_INCREMENT = 1001;

INSERT INTO `category` (`name`)
VALUES ('Classics'),
       ('Fantasy'),
       ('Mystery'),
       ('Romance');

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('The Iliad', 'Homer', '', 6.99, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('The Brothers Karamazov', 'Fyodor Dostoyevski', '', 7.99, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Little Women ', 'Louisa May Alcott', '', 5.99, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Little Dorrit', 'Charles Dickens', '', 6.659, 0, TRUE, FALSE, 1001);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Outlander', 'Diana Gabaldon', '', 11.59, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('A Game of Thrones', 'George R. R. Martin', '', 25.64, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('American Gods', 'Neil Ga', '', 3.9, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('The Lord of the Rings', ' J. R. R. Tolkien', '', 6.00, 0, TRUE, FALSE, 1002);


INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('The Moonstone', 'Wilkie Collins', '', 12.59, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Gone Girl', 'Gillian Flynn', '', 7.99, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Devil in a Blue Dress', 'Walter Mosley', '', 5.99, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Presumed Innocent', 'Scott Turow', '', 6.99, 0, TRUE, FALSE, 1003);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Jane Eyre', 'Charlotte Bronte', '', 7.99, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('One True Loves: A Novel', 'Taylor Jenkins', '', 2.99, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('Beach Read', 'Emily Henry', '', 12.99, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id)
VALUES ('The Notebook', 'Nicholas Sparks', '', 15, 0, TRUE, FALSE, 1004);
CREATE TABLE IF NOT EXISTS category (
  category_id bigint AUTO_INCREMENT PRIMARY KEY,
  name varchar(90) NOT NULL UNIQUE
);
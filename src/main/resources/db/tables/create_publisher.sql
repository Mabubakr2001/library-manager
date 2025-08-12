CREATE TABLE IF NOT EXISTS publisher(
  publisher_id bigint AUTO_INCREMENT PRIMARY KEY,
  name varchar(100) NOT NULL UNIQUE,
  country varchar(150),
  founded_year varchar(4),
  website varchar(200)
);
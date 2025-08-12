CREATE TABLE IF NOT EXISTS author (
  author_id bigint AUTO_INCREMENT PRIMARY KEY,
  full_name varchar(80) NOT NULL unique,
  country varchar(50),
  birth_date date
);
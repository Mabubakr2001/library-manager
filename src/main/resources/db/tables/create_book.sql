CREATE TABLE IF NOT EXISTS book (
  book_id bigint AUTO_INCREMENT PRIMARY KEY,
  title varchar(100) NOT NULL UNIQUE,
  subtitle text,
  description longtext,
  isbn varchar(13) NOT NULL UNIQUE,
  pages_count int NOT NULL,
  image_link text,
  published_on date,
  status varchar(20) NOT NULL,
  author_id bigint NOT NULL,
  category_id bigint NOT NULL,
  publisher_id bigint NOT NULL
)
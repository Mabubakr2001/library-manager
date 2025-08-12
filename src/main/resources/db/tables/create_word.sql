CREATE TABLE word (
  word_id bigint primary key AUTO_INCREMENT,
  word varchar(50) NOT NULL unique,
  word_meaning varchar(100) NOT NULL,
  related_sentence` longtext NOT NULL,
  page_number int NOT NULL,
  book_id bigint NOT NULL,
)
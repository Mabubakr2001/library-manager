CREATE TABLE IF NOT EXISTS quote (
    quote_id bigint AUTO_INCREMENT PRIMARY KEY,
    text longtext NOT NULL,
    page_number int NOT NULL,
    book_id bigint not null
);
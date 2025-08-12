BEGIN;

    ALTER TABLE book ADD CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES author (author_id);

    ALTER TABLE book ADD CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES category (category_id);

    ALTER TABLE book ADD CONSTRAINT fk_book_publisher FOREIGN KEY (publisher_id) REFERENCES publisher (publisher_id);

    ALTER TABLE quote ADD CONSTRAINT fk_quote_book FOREIGN KEY (book_id) REFERENCES book (book_id);

    ALTER TABLE word ADD CONSTRAINT fk_word_book FOREIGN KEY (book_id) REFERENCES book (book_id);
COMMIT;
package dev.bakr.library_manager.repository;

import dev.bakr.library_manager.model.ReaderBook;
import dev.bakr.library_manager.model.ReaderBookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReaderBookRepository extends JpaRepository<ReaderBook, ReaderBookId> {
    @Query("SELECT COUNT(rb) FROM ReaderBook rb WHERE rb.book.id = :bookId")
    Long countReadersByBookId(@Param("bookId") Long bookId);
}

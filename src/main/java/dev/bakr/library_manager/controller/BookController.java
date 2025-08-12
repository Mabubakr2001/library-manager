package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requestDtos.BookDtoRequest;
import dev.bakr.library_manager.responses.ReaderBookDto;
import dev.bakr.library_manager.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "/readers/{readerId}/books")
    public ResponseEntity<List<ReaderBookDto>> getBooks(@PathVariable Long readerId) {
        List<ReaderBookDto> allBooks = bookService.getBooks(readerId);
        return ResponseEntity.ok(allBooks);
    }

    @GetMapping(path = "/readers/{readerId}/books/{bookId}")
    public ResponseEntity<ReaderBookDto> getBook(@PathVariable Long readerId, @PathVariable Long bookId) {
        ReaderBookDto readingCopy = bookService.getBook(readerId, bookId);  // this might throw BookNotFoundException
        return ResponseEntity.status(HttpStatus.OK).body(readingCopy);
    }

    @PostMapping(path = "/readers/{readerId}/books")
    public ResponseEntity<String> addBook(@PathVariable Long readerId,
            @Valid @RequestBody BookDtoRequest bookDtoRequest) {
        String newBookMessage = bookService.addBook(readerId, bookDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBookMessage);
    }

    @DeleteMapping(path = "/readers/{readerId}/books/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long readerId, @PathVariable Long bookId) {
        String deletedBookMessage = bookService.deleteBook(readerId, bookId);
        return ResponseEntity.ok(deletedBookMessage);
    }
}

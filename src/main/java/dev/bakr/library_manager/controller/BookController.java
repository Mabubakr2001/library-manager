package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requestDtos.BookDtoRequest;
import dev.bakr.library_manager.requestDtos.ReaderBookUpdateDto;
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
    public ResponseEntity<List<ReaderBookDto>> getReaderBooks(@PathVariable Long readerId) {
        List<ReaderBookDto> allBooks = bookService.getReaderBooks(readerId);
        return ResponseEntity.ok(allBooks);
    }

    @GetMapping(path = "/readers/{readerId}/books/{bookId}")
    public ResponseEntity<ReaderBookDto> getReaderBook(@PathVariable Long readerId, @PathVariable Long bookId) {
        ReaderBookDto readingCopy = bookService.getReaderBook(readerId,
                                                              bookId
        );  // this might throw BookNotFoundException
        return ResponseEntity.status(HttpStatus.OK).body(readingCopy);
    }

    @PostMapping(path = "/readers/{readerId}/books")
    public ResponseEntity<String> addReaderBook(@PathVariable Long readerId,
            @Valid @RequestBody BookDtoRequest bookDtoRequest) {
        String newBookMessage = bookService.addReaderBook(readerId, bookDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBookMessage);
    }

    @PutMapping(path = "/readers/{readerId}/books/{bookId}")
    public ResponseEntity<ReaderBookDto> updateReaderBook(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @Valid @RequestBody ReaderBookUpdateDto readerBookUpdateDto) {
        ReaderBookDto updatedReadingCopy = bookService.updateReaderBook(readerId, bookId, readerBookUpdateDto);
        return ResponseEntity.ok(updatedReadingCopy);
    }

    @DeleteMapping(path = "/readers/{readerId}/books/{bookId}")
    public ResponseEntity<String> deleteReaderBook(@PathVariable Long readerId, @PathVariable Long bookId) {
        String deletedBookMessage = bookService.deleteReaderBook(readerId, bookId);
        return ResponseEntity.ok(deletedBookMessage);
    }
}

package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.AccessDeniedException;
import dev.bakr.library_manager.exceptions.ExistsException;
import dev.bakr.library_manager.exceptions.NotFoundException;
import dev.bakr.library_manager.mappers.BookMapper;
import dev.bakr.library_manager.model.Book;
import dev.bakr.library_manager.model.Reader;
import dev.bakr.library_manager.model.ReaderBook;
import dev.bakr.library_manager.repository.BookRepository;
import dev.bakr.library_manager.repository.ReaderBookRepository;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.requestDtos.BookDtoRequest;
import dev.bakr.library_manager.responses.ReaderBookDto;
import dev.bakr.library_manager.utils.SecurityCheck;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class BookService {
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final ReaderBookRepository readerBookRepository;
    private final BookMapper bookMapper;

    public BookService(AuthorService authorService,
            CategoryService categoryService,
            PublisherService publisherService,
            BookRepository bookRepository,
            ReaderRepository readerRepository, ReaderBookRepository readerBookRepository,
            BookMapper bookMapper) {
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.readerBookRepository = readerBookRepository;
        this.bookMapper = bookMapper;
    }

    public List<ReaderBookDto> getBooks(Long readerId) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        Reader neededReader = readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found! With id: " + readerId));

        List<ReaderBookDto> readingCopies = new ArrayList<>();
        List<ReaderBook> allReaderBooks = neededReader.getReaderBooks();

        if (!allReaderBooks.isEmpty()) {
            for (ReaderBook readerBook : allReaderBooks) {
                var theBookItself = readerBook.getBook();
                var readingCopy = new ReaderBookDto(theBookItself.getId(),
                                                    theBookItself.getTitle(),
                                                    theBookItself.getSubtitle(),
                                                    theBookItself.getDescription(),
                                                    theBookItself.getIsbn(),
                                                    theBookItself.getPagesCount(),
                                                    theBookItself.getImageLink(),
                                                    theBookItself.getPrintingType(),
                                                    theBookItself.getPublishingYear(),
                                                    theBookItself.getAuthor().getFullName(),
                                                    theBookItself.getCategory().getName(),
                                                    theBookItself.getPublisher().getName(),
                                                    readerBook.getStatus(),
                                                    readerBook.getAddingDate(),
                                                    readerBook.getLeftOffPage()
                );
                readingCopies.add(readingCopy);
            }
        }

        return readingCopies;
    }

    public ReaderBookDto getBook(Long readerId, Long bookId) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        Reader reader = readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found! With id: " + readerId));

        // I have to use either hashmap or hashset here to improve the big o, because i want a simple lookup
        var readerBook = reader.getReaderBooks().stream()
                .filter(book -> Objects.equals(book.getBook().getId(), bookId))
                .findFirst().orElseThrow(() -> new NotFoundException(
                        "The reader with ID " + readerId + " doesn't have the book with ID " + bookId
                ));

        var theBookItself = readerBook.getBook();

        return new ReaderBookDto(theBookItself.getId(),
                                 theBookItself.getTitle(),
                                 theBookItself.getSubtitle(),
                                 theBookItself.getDescription(),
                                 theBookItself.getIsbn(),
                                 theBookItself.getPagesCount(),
                                 theBookItself.getImageLink(),
                                 theBookItself.getPrintingType(),
                                 theBookItself.getPublishingYear(),
                                 theBookItself.getAuthor().getFullName(),
                                 theBookItself.getCategory().getName(),
                                 theBookItself.getPublisher().getName(),
                                 readerBook.getStatus(),
                                 readerBook.getAddingDate(),
                                 readerBook.getLeftOffPage()
        );
    }

    public String addBook(Long readerId, BookDtoRequest bookDtoRequest) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        // Still check to avoid NullPointerException if reader was deleted after token issued (Always prefer robustness over optimism)
        Reader neededReader = readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found! With id: " + readerId));

        var bookIsbn = bookDtoRequest.isbn();
        Book existingBookInDatabase = bookRepository.findByIsbn(bookIsbn);

        if (existingBookInDatabase == null) {
            Book newBookEntity = bookMapper.toEntity(bookDtoRequest);
            newBookEntity.setAuthor(authorService.findOrCreateAuthor(bookDtoRequest.authorFullName()));
            newBookEntity.setCategory(categoryService.findOrCreateCategory(bookDtoRequest.categoryName()));
            newBookEntity.setPublisher(publisherService.findOrCreatePublisher(bookDtoRequest.publisherName()));
            Book savedBook = bookRepository.save(newBookEntity);
            buildRelationshipWithBook(neededReader, savedBook);
            readerRepository.save(neededReader);
            return "We've successfully created the book and added it to your books.";
        } else {
            boolean alreadyLinked = neededReader.getReaderBooks().stream()
                    .anyMatch(rb -> rb.getBook().getId().equals(existingBookInDatabase.getId()));
            if (alreadyLinked) {
                throw new ExistsException("You already have this book!");
            }
            buildRelationshipWithBook(neededReader, existingBookInDatabase);
            readerRepository.save(neededReader);
            return "This book already exists in the database. We've added it to your books.";
        }
    }

    public String deleteBook(Long readerId, Long bookId) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        // in case it is authenticated but was deleted accidentally from the database
        Reader neededReader = readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found! With id: " + readerId));

        Book bookToRemove = neededReader.getReaderBooks().stream()
                .filter(rb -> rb.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Book not found in your collection.")).getBook();

        removeRelationshipWithBook(neededReader, bookToRemove);
        readerRepository.save(neededReader);

        // Optional: if no other readers have the book, delete it
        if (readerBookRepository.countReadersByBookId(bookToRemove.getId()) == 0) {
            bookRepository.delete(bookToRemove);
        }

        return "Book deleted successfully.";
    }

    private void buildRelationshipWithBook(Reader reader, Book book) {
        ReaderBook readerBook = new ReaderBook(reader, book);
        reader.getReaderBooks().add(readerBook);
    }

    public void removeRelationshipWithBook(Reader reader, Book book) {
        reader.getReaderBooks().removeIf(rb -> rb.getBook().equals(book));
    }
}

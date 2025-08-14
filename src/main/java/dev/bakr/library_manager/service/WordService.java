package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.AccessDeniedException;
import dev.bakr.library_manager.exceptions.ExistsException;
import dev.bakr.library_manager.exceptions.NotFoundException;
import dev.bakr.library_manager.mappers.WordMapper;
import dev.bakr.library_manager.model.ReaderBook;
import dev.bakr.library_manager.repository.ReaderBookRepository;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.repository.WordRepository;
import dev.bakr.library_manager.requestDtos.WordDtoRequest;
import dev.bakr.library_manager.responses.WordDto;
import dev.bakr.library_manager.utils.SecurityCheck;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private final ReaderRepository readerRepository;
    private final ReaderBookRepository readerBookRepository;
    private final WordMapper wordMapper;

    public WordService(WordRepository wordRepository,
            ReaderRepository readerRepository,
            ReaderBookRepository readerBookRepository, WordMapper wordMapper) {
        this.wordRepository = wordRepository;
        this.readerRepository = readerRepository;
        this.readerBookRepository = readerBookRepository;
        this.wordMapper = wordMapper;
    }

    public WordDto addWord(Long readerId, Long bookId, WordDtoRequest wordDtoRequest) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found in the database!"));

        var readerBookId = ReaderBook.createCompositeKey(readerId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to add the word!"
        ));

        boolean isWordExistsInReaderBook = wordRepository.existsByWordContentAndReaderBookId(wordDtoRequest.wordContent(),
                                                                                             readerBookId
        );
        if (isWordExistsInReaderBook) {
            throw new ExistsException("You already have this word in this book copy!");
        }

        var newWord = wordMapper.toEntity(wordDtoRequest);
        newWord.setReaderBook(readerBook);
        var savedWord = wordRepository.save(newWord);

        readerBook.getWords().add(savedWord);
        readerBookRepository.save(readerBook);

        return wordMapper.toDto(newWord);
    }

    public WordDto updateWord(Long readerId, Long bookId, Long wordId, WordDtoRequest wordDtoRequest) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found in the database!"));

        var readerBookId = ReaderBook.createCompositeKey(readerId, bookId);

        readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to update its word!"
        ));

        var theWordToUpdate = wordRepository.findByIdAndReaderBookId(wordId, readerBookId);

        if (theWordToUpdate == null) {
            throw new ExistsException("This word isn't found in this book copy to update it!");
        }

        theWordToUpdate.setWordContent(wordDtoRequest.wordContent());
        theWordToUpdate.setTranslation(wordDtoRequest.translation());
        theWordToUpdate.setRelatedSentence(wordDtoRequest.relatedSentence());
        theWordToUpdate.setPageNumber(wordDtoRequest.pageNumber());
        wordRepository.save(theWordToUpdate);

        return wordMapper.toDto(theWordToUpdate);
    }

    public String deleteWord(Long readerId, Long bookId, Long wordId) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found in the database!"));

        var readerBookId = ReaderBook.createCompositeKey(readerId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to delete its word!"
        ));

        var theWordToDelete = wordRepository.findByIdAndReaderBookId(wordId, readerBookId);
        if (theWordToDelete == null) {
            throw new ExistsException("This word isn't found in this book copy to delete it!");
        }
        wordRepository.delete(theWordToDelete);

        readerBook.getWords().removeIf((word) -> word.equals(theWordToDelete));
        readerBookRepository.save(readerBook);

        return "You've successfully deleted the word.";
    }

    public WordDto getWord(Long readerId, Long bookId, Long wordId) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found in the database!"));

        var readerBookId = ReaderBook.createCompositeKey(readerId, bookId);

        readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "This book copy isn't found in your collection to get its word!"
        ));

        var theWordToGet = wordRepository.findByIdAndReaderBookId(wordId, readerBookId);

        if (theWordToGet == null) {
            throw new ExistsException("This word isn't found in this book copy to get from");
        }

        return wordMapper.toDto(theWordToGet);
    }

    public List<WordDto> getWords(Long readerId, Long bookId) {
        Long authenticatedReaderId = SecurityCheck.getAuthenticatedUserId();

        if (!authenticatedReaderId.equals(readerId)) {
            throw new AccessDeniedException("You cannot access this resource.");
        }

        readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException(
                "Reader not found in the database!"));

        var readerBookId = ReaderBook.createCompositeKey(readerId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "This book copy isn't found in your collection to get the words from!"
        ));

        return readerBook.getWords().stream().map(wordMapper::toDto).toList();
    }
}

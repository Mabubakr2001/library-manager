package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requestDtos.WordDtoRequest;
import dev.bakr.library_manager.responses.WordDto;
import dev.bakr.library_manager.service.WordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class WordController {
    private final WordService wordService;

    WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @PostMapping("/readers/{readerId}/books/{bookId}/words")
    public ResponseEntity<WordDto> addWord(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @Valid @RequestBody WordDtoRequest wordDtoRequest) {
        WordDto addedWord = wordService.addWord(readerId, bookId, wordDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedWord);
    }

    @PutMapping("/readers/{readerId}/books/{bookId}/words/{wordId}")
    public ResponseEntity<WordDto> updateWord(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @PathVariable Long wordId,
            @Valid @RequestBody WordDtoRequest wordDtoRequest) {
        WordDto updatedWord = wordService.updateWord(readerId, bookId, wordId, wordDtoRequest);
        return ResponseEntity.ok(updatedWord);
    }

    @DeleteMapping("/readers/{readerId}/books/{bookId}/words/{wordId}")
    public ResponseEntity<String> deleteWord(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @PathVariable Long wordId) {
        String deletedWordMessage = wordService.deleteWord(readerId, bookId, wordId);
        return ResponseEntity.ok(deletedWordMessage);
    }


    @GetMapping("/readers/{readerId}/books/{bookId}/words/{wordId}")
    public ResponseEntity<WordDto> getWord(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @PathVariable Long wordId) {
        WordDto returnedWord = wordService.getWord(readerId, bookId, wordId);
        return ResponseEntity.ok(returnedWord);
    }

    @GetMapping("/readers/{readerId}/books/{bookId}/words")
    public ResponseEntity<List<WordDto>> getWords(@PathVariable Long readerId, @PathVariable Long bookId) {
        List<WordDto> returnedWords = wordService.getWords(readerId, bookId);
        return ResponseEntity.ok(returnedWords);
    }
}

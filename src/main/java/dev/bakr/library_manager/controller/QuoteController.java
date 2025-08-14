package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requestDtos.QuoteDtoRequest;
import dev.bakr.library_manager.responses.QuoteDto;
import dev.bakr.library_manager.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class QuoteController {
    private final QuoteService quoteService;

    QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/readers/{readerId}/books/{bookId}/quotes")
    public ResponseEntity<QuoteDto> addQuote(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @Valid @RequestBody QuoteDtoRequest quoteDtoRequest) {
        QuoteDto addedQuote = quoteService.addQuote(readerId, bookId, quoteDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedQuote);
    }

    @PutMapping("/readers/{readerId}/books/{bookId}/quotes/{quoteId}")
    public ResponseEntity<QuoteDto> updateQuote(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @PathVariable Long quoteId,
            @Valid @RequestBody QuoteDtoRequest quoteDtoRequest) {
        QuoteDto updatedQuote = quoteService.updateQuote(readerId, bookId, quoteId, quoteDtoRequest);
        return ResponseEntity.ok(updatedQuote);
    }

    @DeleteMapping("/readers/{readerId}/books/{bookId}/quotes/{quoteId}")
    public ResponseEntity<String> deleteQuote(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @PathVariable Long quoteId) {
        String deletedQuoteMessage = quoteService.deleteQuote(readerId, bookId, quoteId);
        return ResponseEntity.ok(deletedQuoteMessage);
    }


    @GetMapping("/readers/{readerId}/books/{bookId}/quotes/{quoteId}")
    public ResponseEntity<QuoteDto> getQuote(@PathVariable Long readerId,
            @PathVariable Long bookId,
            @PathVariable Long quoteId) {
        QuoteDto returnedQuote = quoteService.getQuote(readerId, bookId, quoteId);
        return ResponseEntity.ok(returnedQuote);
    }

    @GetMapping("/readers/{readerId}/books/{bookId}/quotes")
    public ResponseEntity<List<QuoteDto>> getQuotes(@PathVariable Long readerId, @PathVariable Long bookId) {
        List<QuoteDto> returnedQuotes = quoteService.getQuotes(readerId, bookId);
        return ResponseEntity.ok(returnedQuotes);
    }
}

package dev.bakr.library_manager.responses;

public record WordDto(Long id, String wordContent, String translation, String relatedSentence, Integer pageNumber) {
}

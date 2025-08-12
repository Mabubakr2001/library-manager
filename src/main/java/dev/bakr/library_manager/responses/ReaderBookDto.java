package dev.bakr.library_manager.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReaderBookDto(Long id,
        String title,
        String subtitle,
        String description,
        String isbn,
        Integer pagesCount,
        String imageLink,
        String printingType,
        Integer publishingYear,
        String authorName,
        String categoryName,
        String publisherName,
        String readingStatus,
        LocalDate addingDate,
        Integer leftOffPage) {
}

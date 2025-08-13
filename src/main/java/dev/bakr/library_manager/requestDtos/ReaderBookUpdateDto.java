package dev.bakr.library_manager.requestDtos;

import jakarta.validation.constraints.NotBlank;

public record ReaderBookUpdateDto(@NotBlank(message = "The book should have a status!") String status,
        Integer leftOffPage) {
}

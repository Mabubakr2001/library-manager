package dev.bakr.library_manager.requestDtos;

import jakarta.validation.constraints.NotBlank;

public record LoginReaderDtoRequest(@NotBlank(message = "The username of the reader is required!") String username,
        @NotBlank(message = "The password of the reader is required!") String password) {
}

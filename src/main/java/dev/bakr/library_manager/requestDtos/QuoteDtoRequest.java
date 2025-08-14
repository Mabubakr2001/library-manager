package dev.bakr.library_manager.requestDtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuoteDtoRequest(@NotBlank(message = "The quote text is required!") String text,
        @NotNull(message = "The page number of the quote is required!") @Min(1) Integer pageNumber) {
}

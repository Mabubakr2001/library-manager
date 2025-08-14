package dev.bakr.library_manager.mappers;

import dev.bakr.library_manager.model.Quote;
import dev.bakr.library_manager.requestDtos.QuoteDtoRequest;
import dev.bakr.library_manager.responses.QuoteDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
    Quote toEntity(QuoteDtoRequest quoteDtoRequest);

    QuoteDto toDto(Quote quote);
}

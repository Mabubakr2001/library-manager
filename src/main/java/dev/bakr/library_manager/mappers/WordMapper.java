package dev.bakr.library_manager.mappers;

import dev.bakr.library_manager.model.Word;
import dev.bakr.library_manager.requestDtos.WordDtoRequest;
import dev.bakr.library_manager.responses.WordDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WordMapper {
    Word toEntity(WordDtoRequest wordDtoRequest);

    WordDto toDto(Word word);
}

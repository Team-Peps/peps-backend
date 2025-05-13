package fr.teampeps.mapper;

import fr.teampeps.dto.AuthorDto;
import fr.teampeps.models.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorMapper {

    public AuthorDto toDto(Author author) {
        return AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .build();
    }
}

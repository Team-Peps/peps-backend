package fr.teampeps.service;

import fr.teampeps.dto.AuthorDto;
import fr.teampeps.mapper.AuthorMapper;
import fr.teampeps.models.Author;
import fr.teampeps.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(author -> AuthorDto.builder().id(author.getId()).name(author.getName()).build())
                .toList();
    }

    public AuthorDto createAuthor(Author authorDto) {
        if(authorRepository.existsAuthorByName(authorDto.getName())) {
            throw new IllegalArgumentException("Un auteur avec ce nom existe déjà");
        }

        return authorMapper.toDto(authorRepository.save(authorDto));
    }

    public AuthorDto updateAuthor(Author author, String authorId) {
        Author existingAuthor = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Aucun auteur trouvé avec cet ID"));

        existingAuthor.setName(author.getName());
        return authorMapper.toDto(authorRepository.save(existingAuthor));
    }
}

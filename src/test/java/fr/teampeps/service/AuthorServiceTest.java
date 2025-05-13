package fr.teampeps.service;

import fr.teampeps.dto.AuthorDto;
import fr.teampeps.mapper.AuthorMapper;
import fr.teampeps.models.Author;
import fr.teampeps.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAuthors_shouldReturnListOfAuthorDtos() {
        Author author = new Author();
        author.setId("1");
        author.setName("Victor Hugo");

        when(authorRepository.findAll()).thenReturn(List.of(author));

        List<AuthorDto> result = authorService.getAllAuthors();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("1");
        assertThat(result.get(0).getName()).isEqualTo("Victor Hugo");
    }

    @Test
    void createAuthor_shouldCreateAuthorIfNameNotExists() {
        Author author = new Author();
        author.setName("Zola");

        Author savedAuthor = new Author();
        savedAuthor.setId("123");
        savedAuthor.setName("Zola");

        AuthorDto dto = AuthorDto.builder().id("123").name("Zola").build();

        when(authorRepository.existsAuthorByName("Zola")).thenReturn(false);
        when(authorRepository.save(author)).thenReturn(savedAuthor);
        when(authorMapper.toDto(savedAuthor)).thenReturn(dto);

        AuthorDto result = authorService.createAuthor(author);

        assertThat(result.getId()).isEqualTo("123");
        assertThat(result.getName()).isEqualTo("Zola");
    }

    @Test
    void createAuthor_shouldThrowIfNameExists() {
        Author author = new Author();
        author.setName("Zola");

        when(authorRepository.existsAuthorByName("Zola")).thenReturn(true);

        assertThatThrownBy(() -> authorService.createAuthor(author))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un auteur avec ce nom existe déjà");
    }

    @Test
    void updateAuthor_shouldUpdateExistingAuthor() {
        String authorId = "42";
        Author update = new Author();
        update.setName("Albert Camus");

        Author existing = new Author();
        existing.setId("42");
        existing.setName("Old Name");

        Author saved = new Author();
        saved.setId("42");
        saved.setName("Albert Camus");

        AuthorDto dto = AuthorDto.builder().id("42").name("Albert Camus").build();

        when(authorRepository.findById("42")).thenReturn(Optional.of(existing));
        when(authorRepository.save(existing)).thenReturn(saved);
        when(authorMapper.toDto(saved)).thenReturn(dto);

        AuthorDto result = authorService.updateAuthor(update, "42");

        assertThat(result.getId()).isEqualTo("42");
        assertThat(result.getName()).isEqualTo("Albert Camus");
    }

    @Test
    void updateAuthor_shouldThrowIfAuthorNotFound() {
        when(authorRepository.findById("404")).thenReturn(Optional.empty());

        Author author = new Author();
        author.setName("Nouveau");

        assertThatThrownBy(() -> authorService.updateAuthor(author, "404"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Aucun auteur trouvé avec cet ID");
    }

}

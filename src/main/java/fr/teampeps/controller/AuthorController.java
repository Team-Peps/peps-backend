package fr.teampeps.controller;

import fr.teampeps.dto.AuthorDto;
import fr.teampeps.models.Author;
import fr.teampeps.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/author")
@RequiredArgsConstructor
@Slf4j
public class AuthorController {

    private final AuthorService authorService;
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createAuthor(
            @RequestBody Author author) {
        log.info("📦 Creating author with name: {}", author.getName());
        try {
            AuthorDto authorCreated = authorService.createAuthor(author);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Auteur créé avec succès",
                    "author", authorCreated
            ));
        } catch (Exception e) {
            log.error("❌ Error creating author with name: {}", author.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la création de l'auteur",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PutMapping("/{authorId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAuthor(
            @PathVariable("authorId") String authorId,
            @RequestBody Author author
    ) {
        log.info("📦 Updating author with ID: {}", author.getId());
        try {
            AuthorDto updatedAuthor = authorService.updateAuthor(author, authorId);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Auteur mise à jour avec succès",
                    "author", updatedAuthor
            ));
        } catch (Exception e) {
            log.error("❌ Error updating author with name: {}", author.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la mise à jour de l'auteur",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

}

package fr.teampeps.controller;

import fr.teampeps.dto.UserDto;
import fr.teampeps.dto.UserTinyDto;
import fr.teampeps.model.user.User;
import fr.teampeps.model.user.UserRequest;
import fr.teampeps.repository.UserRepository;
import fr.teampeps.security.config.JwtService;
import fr.teampeps.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Set<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/disable")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> disableUser(@RequestBody UserRequest request, @AuthenticationPrincipal User authenticatedUser)
    {
        UserDto disabledUser = userService.disableUser(request.id(), authenticatedUser.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Utilisateur bloqué avec succès",
                "user", disabledUser
        ));
    }

    @PutMapping("/enable")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> enableUser(@RequestBody UserRequest request) {
        UserDto enabledUser = userService.enableUser(request.id());

        return ResponseEntity.ok(Map.of(
                "message", "Utilisateur débloqué avec succès",
                "user", enabledUser
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id, @AuthenticationPrincipal User authenticatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (user.getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez pas supprimer votre propre compte");
        }

        userRepository.delete(user);

        return ResponseEntity.ok(Map.of(
                "message", "Utilisateur supprimé avec succès",
                "user", user
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<UserTinyDto> getProfile(@AuthenticationPrincipal User userAuthenticated) {
        log.info(userAuthenticated.toString());

        User user = userRepository.findById(userAuthenticated.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        return ResponseEntity.ok(
                UserTinyDto.builder()
                    .avatarUrl(user.getAvatarUrl())
                    .discordId(user.getDiscordId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .id(user.getId())
                    .build()
        );
    }
}

package fr.teampeps.service;

import fr.teampeps.dto.UserDto;
import fr.teampeps.mapper.UserMapper;
import fr.teampeps.model.Authority;
import fr.teampeps.model.User;
import fr.teampeps.repository.TokenRepository;
import fr.teampeps.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;

    public Set<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .sorted(Comparator.comparing((UserDto user) -> user.getAuthorities().isEmpty() ? "" : user.getAuthorities().get(0))
                        .thenComparing(UserDto::getUsername))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public UserDto disableUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'ID: " + userId));

        user.setEnable(false);
        tokenRepository.deleteAll(tokenRepository.findAllValidTokensByUser(user.getId()));
        userRepository.save(user);

        return userMapper.map(user);
    }

    public UserDto enableUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'ID: " + userId));

        user.setEnable(true);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public UserDto changeRole(String userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'ID: " + userId));

        user.setAuthorities(Collections.singletonList(Authority.valueOf(role)));
        userRepository.save(user);

        return userMapper.map(user);
    }
}

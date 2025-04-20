package fr.teampeps.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import fr.teampeps.dto.UserDto;
import fr.teampeps.mapper.UserMapper;
import fr.teampeps.model.user.Authority;
import fr.teampeps.model.user.User;
import fr.teampeps.repository.TokenRepository;
import fr.teampeps.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User regularUser;
    private User disabledUser;

    private UserDto adminUserDto;
    private UserDto regularUserDto;
    private UserDto disabledUserDto;

    @BeforeEach
    void setUp() {
        // Création des utilisateurs pour les tests
        adminUser = new User();
        adminUser.setId("admin-id");
        adminUser.setUsername("admin");
        adminUser.setEnable(true);
        adminUser.setAuthorities(List.of(Authority.ADMIN));

        regularUser = new User();
        regularUser.setId("user-id");
        regularUser.setUsername("user");
        regularUser.setEnable(true);
        regularUser.setAuthorities(List.of(Authority.USER));

        disabledUser = new User();
        disabledUser.setId("disabled-id");
        disabledUser.setUsername("disabled");
        disabledUser.setEnable(false);
        disabledUser.setAuthorities(List.of(Authority.USER));

        // Création des DTOs correspondants
        adminUserDto = new UserDto();
        adminUserDto.setId("admin-id");
        adminUserDto.setUsername("admin");
        adminUserDto.setIsEnable(true);
        adminUserDto.setAuthorities(List.of("ADMIN"));

        regularUserDto = new UserDto();
        regularUserDto.setId("user-id");
        regularUserDto.setUsername("user");
        regularUserDto.setIsEnable(true);
        regularUserDto.setAuthorities(List.of("USER"));

        disabledUserDto = new UserDto();
        disabledUserDto.setId("disabled-id");
        disabledUserDto.setUsername("disabled");
        disabledUserDto.setIsEnable(false);
        disabledUserDto.setAuthorities(List.of("USER"));
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(regularUser, adminUser, disabledUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.map(adminUser)).thenReturn(adminUserDto);
        when(userMapper.map(regularUser)).thenReturn(regularUserDto);
        when(userMapper.map(disabledUser)).thenReturn(disabledUserDto);

        // Act
        Set<UserDto> result = userService.getAllUsers();

        // Assert
        assertEquals(3, result.size());

        // Vérification du tri (admin en premier, puis par nom d'utilisateur)
        List<UserDto> sortedList = result.stream().toList();
        assertEquals("admin-id", sortedList.get(0).getId()); // Admin devrait être premier

        // Vérifier que tous les utilisateurs mappés sont présents
        assertTrue(result.contains(adminUserDto));
        assertTrue(result.contains(regularUserDto));
        assertTrue(result.contains(disabledUserDto));

        verify(userRepository).findAll();
        verify(userMapper, times(3)).map(any(User.class));
    }

    @Test
    void testGetAllUsers_WithEmptyAuthorities() {
        // Arrange
        User userWithoutAuthority = new User();
        userWithoutAuthority.setId("no-auth-id");
        userWithoutAuthority.setUsername("noauth");
        userWithoutAuthority.setEnable(true);
        userWithoutAuthority.setAuthorities(Collections.emptyList());

        UserDto userWithoutAuthorityDto = new UserDto();
        userWithoutAuthorityDto.setId("no-auth-id");
        userWithoutAuthorityDto.setUsername("noauth");
        userWithoutAuthorityDto.setIsEnable(true);
        userWithoutAuthorityDto.setAuthorities(Collections.emptyList());

        List<User> users = Arrays.asList(userWithoutAuthority, adminUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.map(adminUser)).thenReturn(adminUserDto);
        when(userMapper.map(userWithoutAuthority)).thenReturn(userWithoutAuthorityDto);

        // Act
        Set<UserDto> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());

        // Les utilisateurs sans autorité devraient venir avant ceux avec autorité
        List<UserDto> sortedList = result.stream().toList();
        assertEquals("no-auth-id", sortedList.get(0).getId());

        verify(userRepository).findAll();
        verify(userMapper, times(2)).map(any(User.class));
    }

    @Test
    void testDisableUser_Success() {
        // Arrange
        String userId = "user-id";
        String authenticatedUserId = "admin-id";

        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));
        when(userRepository.save(any(User.class))).thenReturn(regularUser);
        when(userMapper.map(regularUser)).thenReturn(regularUserDto);

        // Act
        UserDto result = userService.disableUser(userId, authenticatedUserId);

        // Assert
        assertFalse(regularUser.isEnabled());
        assertEquals(regularUserDto, result);

        verify(userRepository).findById(userId);
        verify(tokenRepository).findAllValidTokensByUser(userId);
        verify(tokenRepository).deleteAll(anyList());
        verify(userRepository).save(regularUser);
        verify(userMapper).map(regularUser);
    }

    @Test
    void testDisableUser_UserNotFound() {
        // Arrange
        String userId = "nonexistent-id";
        String authenticatedUserId = "admin-id";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.disableUser(userId, authenticatedUserId));

        assertEquals(404, exception.getStatusCode().value());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Utilisateur non trouvé"));

        verify(userRepository).findById(userId);
        verifyNoInteractions(tokenRepository, userMapper);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDisableUser_SelfDisable() {
        // Arrange
        String userId = "admin-id";
        String authenticatedUserId = "admin-id";

        when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.disableUser(userId, authenticatedUserId));

        assertEquals(403, exception.getStatusCode().value());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Vous ne pouvez pas bloquer votre propre compte"));

        verify(userRepository).findById(userId);
        verifyNoInteractions(tokenRepository, userMapper);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testEnableUser_Success() {
        // Arrange
        String userId = "disabled-id";

        when(userRepository.findById(userId)).thenReturn(Optional.of(disabledUser));
        when(userRepository.save(any(User.class))).thenReturn(disabledUser);
        when(userMapper.map(disabledUser)).thenReturn(disabledUserDto);

        // Act
        UserDto result = userService.enableUser(userId);

        // Assert
        assertTrue(disabledUser.isEnabled());
        assertEquals(disabledUserDto, result);

        verify(userRepository).findById(userId);
        verify(userRepository).save(disabledUser);
        verify(userMapper).map(disabledUser);
    }

    @Test
    void testEnableUser_UserNotFound() {
        // Arrange
        String userId = "nonexistent-id";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.enableUser(userId));

        assertEquals(404, exception.getStatusCode().value());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Utilisateur non trouvé"));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userMapper);
    }
}
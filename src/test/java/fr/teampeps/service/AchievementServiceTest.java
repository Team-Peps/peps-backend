package fr.teampeps.service;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.mapper.AchievementMapper;
import fr.teampeps.models.Achievement;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Member;
import fr.teampeps.repository.AchievementRepository;
import fr.teampeps.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private AchievementMapper achievementMapper;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AchievementService achievementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAchievementsByGame_shouldReturnMappedAchievements() {
        Game game = Game.OVERWATCH;
        Achievement achievement = new Achievement();
        AchievementDto dto = AchievementDto.builder().build();

        when(achievementRepository.findAllByGame(game)).thenReturn(List.of(achievement));
        when(achievementMapper.toAchievementDto(achievement)).thenReturn(dto);

        List<AchievementDto> result = achievementService.getAllAchievementsByGame(game);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void saveGameAchievement_shouldSaveAchievementWithoutMember() {
        Achievement achievement = new Achievement();
        AchievementDto dto = AchievementDto.builder().build();

        when(achievementRepository.save(achievement)).thenReturn(achievement);
        when(achievementMapper.toAchievementDto(achievement)).thenReturn(dto);

        AchievementDto result = achievementService.saveGameAchievement(achievement);

        assertThat(result).isEqualTo(dto);
        assertThat(achievement.getMember()).isNull();
    }

    @Test
    void saveGameAchievement_shouldThrowExceptionOnError() {
        Achievement achievement = new Achievement();

        when(achievementRepository.save(achievement)).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> achievementService.saveGameAchievement(achievement))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Erreur lors de la mise à jour du palmarès");
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        String id = "id123";
        achievementService.delete(id);
        verify(achievementRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_shouldThrowExceptionIfEntityNotFound() {
        String id = "id123";
        doThrow(new EntityNotFoundException()).when(achievementRepository).deleteById(id);

        assertThatThrownBy(() -> achievementService.delete(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Palmarès non trouvé");
    }
}


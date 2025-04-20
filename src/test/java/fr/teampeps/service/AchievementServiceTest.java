package fr.teampeps.service;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.mapper.AchievementMapper;
import fr.teampeps.model.Achievement;
import fr.teampeps.model.Game;
import fr.teampeps.model.member.Member;
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
    void saveMemberAchievement_shouldThrowExceptionIfMemberNotFound() {
        String memberId = "123";
        Achievement achievement = new Achievement();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> achievementService.saveMemberAchievement(achievement, memberId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Membre non trouvé");
    }

    @Test
    void saveMemberAchievement_shouldSaveWithMember() {
        String memberId = "123";
        Member member = new Member();
        member.setGame(Game.OVERWATCH);
        Achievement achievement = new Achievement();
        AchievementDto dto = AchievementDto.builder().build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(achievementRepository.save(achievement)).thenReturn(achievement);
        when(achievementMapper.toAchievementDto(achievement)).thenReturn(dto);

        AchievementDto result = achievementService.saveMemberAchievement(achievement, memberId);

        assertThat(result).isEqualTo(dto);
        assertThat(achievement.getMember()).isEqualTo(member);
        assertThat(achievement.getGame()).isEqualTo(Game.OVERWATCH);
    }

    @Test
    void saveMemberAchievement_shouldThrowExceptionOnError() {
        String memberId = "123";
        Member member = new Member();
        Achievement achievement = new Achievement();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(achievementRepository.save(achievement)).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> achievementService.saveMemberAchievement(achievement, memberId))
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

    @Test
    void getAllAchievementsByMember_shouldReturnMappedAchievements() {
        String memberId = "member123";
        Achievement achievement = new Achievement();
        AchievementDto dto = AchievementDto.builder().build();

        when(achievementRepository.findAllByMemberId(memberId)).thenReturn(List.of(achievement));
        when(achievementMapper.toAchievementDto(achievement)).thenReturn(dto);

        List<AchievementDto> result = achievementService.getAllAchievementsByMember(memberId);

        assertThat(result).containsExactly(dto);
    }
}


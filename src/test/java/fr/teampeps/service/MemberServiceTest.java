package fr.teampeps.service;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.MemberTinyDto;
import fr.teampeps.mapper.MemberMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Heroe;
import fr.teampeps.models.Member;
import fr.teampeps.repository.HeroeRepository;
import fr.teampeps.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private MinioService minioService;

    @Mock
    private HeroeRepository heroeRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberDto memberDto;
    private MemberTinyDto memberTinyDto;
    private MultipartFile imageFile;
    private List<Heroe> favorites;
    private Game game;

    @BeforeEach
    void setUp() {
        // Setup test data
        member = new Member();
        member.setId("12345");
        member.setPseudo("TestUser");
        member.setIsActive(true);
        member.setIsSubstitute(false);

        memberDto = MemberDto.builder().build();
        memberDto.setId("12345");
        memberDto.setPseudo("TestUser");

        memberTinyDto = MemberTinyDto.builder().build();
        memberTinyDto.setId("12345");
        memberTinyDto.setPseudo("TestUser");

        // Create favorite heroes
        favorites = new ArrayList<>();
        Heroe heroe1 = new Heroe();
        heroe1.setId("hero1");
        heroe1.setName("Hero One");
        favorites.add(heroe1);

        // Create mock multipart file
        imageFile = new MockMultipartFile(
                "image.jpg",
                "image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Create game
        game = Game.OVERWATCH;
    }

    @Test
    void saveOrUpdateMember_Success() {
        // Arrange
        member.setFavoriteHeroes(favorites);
        when(minioService.uploadImageFromMultipartFile(any(MultipartFile.class), eq("testuser"), eq(Bucket.MEMBERS)))
                .thenReturn("test-image-url");
        when(heroeRepository.findById("hero1")).thenReturn(Optional.of(favorites.get(0)));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberMapper.toMemberDto(member)).thenReturn(memberDto);

        // Act
        MemberDto result = memberService.saveOrUpdateMember(member, imageFile);

        // Assert
        assertNotNull(result);
        assertEquals(memberDto, result);
        assertEquals("test-image-url", member.getImageKey());
        verify(memberRepository).save(member);
        verify(minioService).uploadImageFromMultipartFile(imageFile, "testuser", Bucket.MEMBERS);
    }

    @Test
    void saveOrUpdateMember_NullImageFile() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.saveOrUpdateMember(member, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Aucune image fournie", exception.getReason());
    }

    @Test
    void saveOrUpdateMember_TooManyFavoriteHeroes() {
        // Arrange
        List<Heroe> tooManyFavorites = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Heroe heroe = new Heroe();
            heroe.setId("hero" + i);
            tooManyFavorites.add(heroe);
        }
        member.setFavoriteHeroes(tooManyFavorites);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.saveOrUpdateMember(member, imageFile));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Un membre ne peut avoir plus de 3 héros favoris", exception.getReason());
    }

    @Test
    void saveOrUpdateMember_HeroNotFound() {
        // Arrange
        member.setFavoriteHeroes(favorites);
        when(heroeRepository.findById("hero1")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.saveOrUpdateMember(member, imageFile));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Héros introuvable"));
    }

    @Test
    void saveOrUpdateMember_MinioServiceException() {
        // Arrange
        member.setFavoriteHeroes(favorites);
        when(heroeRepository.findById("hero1")).thenReturn(Optional.of(favorites.get(0)));
        when(minioService.uploadImageFromMultipartFile(any(), any(), any()))
                .thenThrow(new RuntimeException("Error uploading image"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.saveOrUpdateMember(member, imageFile));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Erreur lors de la mise à jour du membre", exception.getReason());
    }

    @Test
    void getAllMembers_Success() {
        // Arrange
        List<Member> holders = Collections.singletonList(member);
        List<Member> substitutes = new ArrayList<>();
        List<Member> coaches = new ArrayList<>();
        List<Member> inactives = new ArrayList<>();

        when(memberRepository.findAllActiveHolderByGame(game)).thenReturn(holders);
        when(memberRepository.findAllActiveSubstituteByGame(game)).thenReturn(substitutes);
        when(memberRepository.findAllActiveCoachByGame(game)).thenReturn(coaches);
        when(memberRepository.findAllInactiveByGame(game)).thenReturn(inactives);
        when(memberMapper.toMemberDto(any(Member.class))).thenReturn(memberDto);

        // Act
        Map<String, List<MemberDto>> result = memberService.getAllMembers(game);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(1, result.get("members").size());
        assertEquals(0, result.get("substitutes").size());
        assertEquals(0, result.get("coaches").size());
        assertEquals(0, result.get("inactives").size());
    }

    @Test
    void getAllActiveMembersByGame_Success() {
        // Arrange
        List<Member> holders = Collections.singletonList(member);
        List<Member> substitutes = new ArrayList<>();
        List<Member> coaches = new ArrayList<>();

        when(memberRepository.findAllActiveHolderByGame(game)).thenReturn(holders);
        when(memberRepository.findAllActiveSubstituteByGame(game)).thenReturn(substitutes);
        when(memberRepository.findAllActiveCoachByGame(game)).thenReturn(coaches);
        when(memberMapper.toMemberTinyDto(any(Member.class))).thenReturn(memberTinyDto);

        // Act
        Map<String, List<MemberTinyDto>> result = memberService.getAllActiveMembersByGame(game);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1, result.get("members").size());
        assertEquals(0, result.get("substitutes").size());
        assertEquals(0, result.get("coaches").size());
    }

    @Test
    void deleteMember_Success() {
        // Act
        memberService.deleteMember("12345");

        // Assert
        verify(memberRepository).deleteById("12345");
    }

    @Test
    void deleteMember_NotFound() {
        // Arrange
        doThrow(new EntityNotFoundException("Member not found")).when(memberRepository).deleteById("nonexistent");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.deleteMember("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Membre non trouvé", exception.getReason());
    }

    @Test
    void toggleActive_Success() {
        // Arrange
        when(memberRepository.findById("12345")).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toMemberDto(member)).thenReturn(memberDto);

        // Act
        MemberDto result = memberService.toggleActive("12345");

        // Assert
        assertNotNull(result);
        assertFalse(member.getIsActive());
        verify(memberRepository).save(member);
    }

    @Test
    void toggleActive_MemberNotFound() {
        // Arrange
        when(memberRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.toggleActive("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Membre non trouvé", exception.getReason());
    }

    @Test
    void toggleSubstitute_Success() {
        // Arrange
        when(memberRepository.findById("12345")).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toMemberDto(member)).thenReturn(memberDto);

        // Act
        MemberDto result = memberService.toggleSubstitute("12345");

        // Assert
        assertNotNull(result);
        assertTrue(member.getIsSubstitute());
        verify(memberRepository).save(member);
    }

    @Test
    void toggleSubstitute_MemberNotFound() {
        // Arrange
        when(memberRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.toggleSubstitute("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Membre non trouvé", exception.getReason());
    }

    @Test
    void getMemberDetails_Success() {
        // Arrange
        when(memberRepository.findById("12345")).thenReturn(Optional.of(member));
        when(memberMapper.toMemberDto(member)).thenReturn(memberDto);

        // Act
        MemberDto result = memberService.getMemberDetails("12345");

        // Assert
        assertNotNull(result);
        assertEquals(memberDto, result);
    }

    @Test
    void getMemberDetails_MemberNotFound() {
        // Arrange
        when(memberRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                memberService.getMemberDetails("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Membre non trouvé", exception.getReason());
    }
}
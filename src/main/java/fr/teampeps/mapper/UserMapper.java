package fr.teampeps.mapper;

import fr.teampeps.dto.UserDto;
import fr.teampeps.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto map(User user){
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .authorities(mapAuthorities(user.getAuthorities()))
                .username(user.getUsername())
                .isEnable(user.getEnable())
                .build();
    }

    public List<String> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}

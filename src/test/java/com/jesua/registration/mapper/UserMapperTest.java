package com.jesua.registration.mapper;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.jesua.registration.builder.UserBuilder.buildUser;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private static final UUID ID = UUID.randomUUID();

    @Mock
    PasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    void mapDtoToEntityTest() {
        UserDto userDto = buildUserDto();
        User expectedUser = buildUserFromDto(userDto);

        doReturn("$2a$10$FdrdQKY43A1klR8adtBKseAJvAlkH/Y5zQ1uZS4w0gjap3V4m6yam").when(bCryptPasswordEncoder).encode(userDto.getPassword());

        User actualUser = userMapper.mapDtoToEntity(userDto);

        assertThat(actualUser).usingRecursiveComparison().ignoringFields("created","password").isEqualTo(expectedUser);
        assertThat(actualUser.getCreated()).isCloseTo(expectedUser.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualUser.getPassword()).startsWith("$2a$10$");
    }

    @Test
    void mapDtoToSavedEntityTest() {
        User user = buildUser(ID);
        UserDto userDto = buildUserDto();
        userDto.setRole("ROLE_MODERATOR");
        userDto.setActive(false);
        User expectedUser = buildUserFromDto(userDto);

        doReturn("$2a$10$FdrdQKY43A1klR8adtBKseAJvAlkH/Y5zQ1uZS4w0gjap3V4m6yam").when(bCryptPasswordEncoder).encode(userDto.getPassword());

        User actualUser = userMapper.mapDtoToEntity(userDto, user);

        assertThat(actualUser).usingRecursiveComparison().ignoringFields("id", "created","password").isEqualTo(expectedUser);
        assertThat(actualUser.getId()).isEqualTo(user.getId());
        assertThat(actualUser.getCreated()).isCloseTo(expectedUser.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualUser.getPassword()).startsWith("$2a$10$");
    }

    @Test
    void testMapEntityToDtoTest() {
        User user = buildUser(ID);
        UserResponseDto userResponseDto = buildUserResponseDto(user);

        UserResponseDto actualResponseDto = userMapper.mapEntityToDto(user);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(userResponseDto);
    }
}
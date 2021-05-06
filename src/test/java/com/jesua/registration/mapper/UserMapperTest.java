package com.jesua.registration.mapper;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDtoWithoutId;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseBaseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    PasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserMapperImpl userMapper;

    private static User user;

    @BeforeAll
    static void setUp(){
        user = buildUserWithId(USER_ID);
    }

    @Test
    void mapDtoToEntityTest() {

        UserDto userDto = buildUserDto();
        User expectedUser = buildUserFromDtoWithoutId(userDto);

        doReturn("$2a$10$FdrdQKY43A1klR8adtBKseAJvAlkH/Y5zQ1uZS4w0gjap3V4m6yam").when(bCryptPasswordEncoder).encode(userDto.getPassword());

        User actualUser = userMapper.mapDtoToEntity(userDto);

        assertThat(actualUser).usingRecursiveComparison().ignoringFields("password").isEqualTo(expectedUser);
        assertThat(actualUser.getPassword()).startsWith("$2a$10$");
    }

    @Test
    void mapDtoToSavedEntityTest() {

        UserDto userDto = buildUserDto();
        userDto.setRole("ROLE_MODERATOR");
        userDto.setActive(false);
        userDto.setPassword(null);

        User expectedUser = buildUserFromDto(userDto, user);

        User actualUser = userMapper.mapDtoToEntity(userDto, user);

        assertThat(actualUser).usingRecursiveComparison().ignoringFields("password").isEqualTo(expectedUser);
        assertThat(actualUser.getPassword()).startsWith("$2a$10$");
    }

    @Test
    void testMapEntityToResponseDtoTest() {

        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(user);

        UserResponseBaseDto actualResponseDto = userMapper.mapEntityToDto(user);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(userResponseDto);
    }
}
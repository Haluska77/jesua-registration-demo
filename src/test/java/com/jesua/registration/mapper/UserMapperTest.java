package com.jesua.registration.mapper;

import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDtoWithoutId;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    PasswordEncoder bCryptPasswordEncoder;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    private ProjectMapperImpl projectMapper;

    @InjectMocks
    private UserMapperImpl userMapper;

    private static User user;
    private static Project project;

    @BeforeAll
    static void setUp(){
        project = buildProject(1);
        user = buildUserWithId(USER_ID, project);
    }

    @Test
    void mapDtoToEntityTest() {

        UserDto userDto = buildUserDto(project.getId());
        User expectedUser = buildUserFromDtoWithoutId(userDto, project);

        doReturn("$2a$10$FdrdQKY43A1klR8adtBKseAJvAlkH/Y5zQ1uZS4w0gjap3V4m6yam").when(bCryptPasswordEncoder).encode(userDto.getPassword());
        doReturn(project).when(projectRepository).getOne(project.getId());

        User actualUser = userMapper.mapDtoToEntity(userDto);

        assertThat(actualUser).usingRecursiveComparison().ignoringFields("password").isEqualTo(expectedUser);
        assertThat(actualUser.getPassword()).startsWith("$2a$10$");
    }

    @Test
    void mapDtoToSavedEntityTest() {
        User savedUser = buildUserWithId(USER_ID, project);
        UserDto userDto = buildUserDto(project.getId());
        userDto.setRole("ROLE_MODERATOR");
        userDto.setActive(false);
        userDto.setPassword(null);
        User expectedUser = buildUserFromDto(userDto, savedUser, project);

        doReturn(project).when(projectRepository).getOne(project.getId());

        User actualUser = userMapper.mapDtoToEntity(userDto, UserMapperTest.user);

        assertThat(actualUser).usingRecursiveComparison().ignoringFields("password").isEqualTo(expectedUser);
        assertThat(actualUser.getPassword()).startsWith("$2a$10$");
    }

    @Test
    void testMapEntityToDtoTest() {

        UserResponseDto userResponseDto = buildUserResponseDtoFromEntity(user);
        ProjectResponseDto projectResponseDto = buildProjectResponseDtoFromEntity(project);
        doReturn(projectResponseDto).when(projectMapper).mapEntityToDto(project);

        UserResponseDto actualResponseDto = userMapper.mapEntityToDto(user);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(userResponseDto);
    }
}
package com.jesua.registration.service;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.security.services.UserAuthPrincipal;
import com.jesua.registration.security.services.UserAuthPrincipalService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDtoWithoutId;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseBaseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final UUID USER_ID = UUID.randomUUID();

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @InjectMocks
    UserAuthPrincipalService userAuthPrincipalService;

    private static User user;
    private static Project project;
    private static Set<Project> projects;

    @BeforeAll
    static void setUp() {
        project = buildProject(1);
        projects = Set.of(project);
        user = buildUserWithId(USER_ID);
    }

    @Test
    void loadExistingUserByUsernameTest() {

        UserDetails expectedUserDetails = new UserAuthPrincipal(user);

        doReturn(Optional.of(user)).when(userRepository).findByEmailAndActiveTrue(user.getEmail());

        UserDetails userDetails = userAuthPrincipalService.loadUserByUsername(user.getEmail());

        verify(userRepository).findByEmailAndActiveTrue(user.getEmail());

        assertThat(userDetails).usingRecursiveComparison().isEqualTo(expectedUserDetails);
    }

    @Test
    void loadNonExistingUserByUsernameTest() {

        String userName = "admin";

        assertThatThrownBy(() -> userAuthPrincipalService.loadUserByUsername(userName))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User " + userName + " not found");
    }

    @Test
    void switchActiveUserAccountToFalse() {

        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(user);
        userResponseDto.setActive(false);

        doReturn(Optional.of(user)).when(userRepository).findById(USER_ID);
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(user);

        UserResponseBaseDto actualUserResponse = userService.switchActiveUserAccount(USER_ID);

        verify(userRepository).findById(USER_ID);
        verify(userMapper).mapEntityToDto(user);

        assertThat(actualUserResponse).usingRecursiveComparison().isEqualTo(userResponseDto);
    }


    @Test
    void switchActiveUserAccountToTrue() {

        user.setActive(false);
        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(user);
        userResponseDto.setActive(true);

        doReturn(Optional.of(user)).when(userRepository).findById(USER_ID);
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(user);

        UserResponseBaseDto actualUserResponse = userService.switchActiveUserAccount(USER_ID);

        verify(userRepository).findById(USER_ID);
        verify(userMapper).mapEntityToDto(user);

        assertThat(actualUserResponse).usingRecursiveComparison().isEqualTo(userResponseDto);
    }

    @Test
    void switchActiveUserAccountNotFound() {

        doReturn(Optional.empty()).when(userRepository).findById(USER_ID);

        assertThatThrownBy(() -> userService.switchActiveUserAccount(USER_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not Found!");
    }

    @Test
    void createSuccessUserTest() {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDtoWithoutId(userDto);

        User userWithId = buildUserWithId(USER_ID);

        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(userWithId);

        doReturn(false).when(userRepository).existsByEmail(userDto.getEmail());
        doReturn(user).when(userMapper).mapDtoToEntity(userDto);
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(user);
        doReturn(userWithId).when(userRepository).save(user);

        UserResponseBaseDto actualUserResponse = userService.createUser(userDto);

        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userMapper).mapDtoToEntity(userDto);
        verify(userMapper).mapEntityToDto(user);
        verify(userRepository).save(user);

        assertThat(actualUserResponse).usingRecursiveComparison().isEqualTo(userResponseDto);
    }

    @Test
    void createFailedUserTest() {
        UserDto userDto = buildUserDto();

        doReturn(true).when(userRepository).existsByEmail(userDto.getEmail());

        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email is already registered!");
    }


    @Test
    void updateUserTest() {

        User user1 = buildUserWithId(USER_ID);
        UserDto userDto = buildUserDto();
        userDto.setRole("ROLE_MODERATOR");
        userDto.setActive(false);
        User expectedUser = buildUserFromDtoWithoutId(userDto);
        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(expectedUser);

        doReturn(user1).when(userRepository).getOne(USER_ID);
        doReturn(expectedUser).when(userMapper).mapDtoToEntity(userDto, user1);
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(expectedUser);

        UserResponseBaseDto actualUserResponse = userService.updateUser(USER_ID, userDto);

        verify(userRepository).getOne(USER_ID);
        verify(userMapper).mapDtoToEntity(userDto, user1);
        verify(userMapper).mapEntityToDto(expectedUser);

        assertThat(actualUserResponse).usingRecursiveComparison().isEqualTo(userResponseDto);
    }
}
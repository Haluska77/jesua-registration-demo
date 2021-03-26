package com.jesua.registration.service;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.security.services.UserAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.UserBuilder.buildUser;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final UUID MY_ID = UUID.randomUUID();

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @Test
    void loadExistingUserByUsernameTest() {

        User user = buildUser(MY_ID);
        UserDetails expectedUserDetails = new UserAuthPrincipal(user);

        doReturn(Optional.of(user)).when(userRepository).findByEmailAndActiveTrue(user.getEmail());

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        assertThat(userDetails).usingRecursiveComparison().isEqualTo(expectedUserDetails);
    }

    @Test
    void loadNonExistingUserByUsernameTest() {

        User user = buildUser(MY_ID);
        String userName = "admin";

        assertThatThrownBy(() -> userService.loadUserByUsername(userName))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User " + userName + " not found");
    }

    @Test
    void switchActiveUserAccountToFalse() {

        User user = buildUser(MY_ID);
        UserResponseDto userResponseDto = buildUserResponseDto(user);
        userResponseDto.setActive(false);

        doReturn(Optional.of(user)).when(userRepository).findById(MY_ID);
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(user);

        UserResponseDto actualUserResponse = userService.switchActiveUserAccount(MY_ID);

        verify(userRepository).findById(MY_ID);
        verify(userMapper).mapEntityToDto(user);

        assertThat(actualUserResponse).usingRecursiveComparison().isEqualTo(userResponseDto);
    }


    @Test
    void switchActiveUserAccountToTrue() {

        User user = buildUser(MY_ID);
        user.setActive(false);
        UserResponseDto userResponseDto = buildUserResponseDto(user);
        userResponseDto.setActive(true);

        doReturn(Optional.of(user)).when(userRepository).findById(MY_ID);
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(user);

        UserResponseDto actualUserResponse = userService.switchActiveUserAccount(MY_ID);

        verify(userRepository).findById(MY_ID);
        verify(userMapper).mapEntityToDto(user);

        assertThat(actualUserResponse).usingRecursiveComparison().isEqualTo(userResponseDto);
    }

    @Test
    void switchActiveUserAccountNotFound() {

        User user = buildUser(MY_ID);

        doReturn(Optional.of(user)).when(userRepository).findById(MY_ID);

        assertThatThrownBy(() -> userService.switchActiveUserAccount(MY_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not Found!");
    }

    @Test
    void createSuccessUserTest() {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDto(userDto);
        UserResponseDto userResponseDto = buildUserResponseDto(buildUser(MY_ID));

        doReturn(false).when(userRepository).existsByEmail(userDto.getEmail());
        doReturn(user).when(userMapper).mapDtoToEntity(userDto);
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(user);

        UserResponseDto actualUserResponse = userService.createUser(userDto);

        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userMapper).mapDtoToEntity(userDto);
        verify(userMapper).mapEntityToDto(user);

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
}
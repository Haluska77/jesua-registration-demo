package com.jesua.registration.service;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.mapper.UserProjectMapper;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.security.dto.LoginResponseDto;
import com.jesua.registration.security.jwt.JwtProvider;
import com.jesua.registration.security.services.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProjectService userProjectService;
    private final UserProjectMapper userProjectMapper;
    private final JwtProvider jwtProvider;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));

        return new UserAuthPrincipal(user);
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserResponseBaseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::mapEntityToDto).collect(Collectors.toList());
    }

    public UserResponseBaseDto switchActiveUserAccount(UUID userId) {
        return userRepository.findById(userId)
                .map(this::getUserResponseDto)
                .orElseThrow(() -> new NoSuchElementException("User not Found!"));
    }

    private UserResponseBaseDto getUserResponseDto(User user) {
        user.setActive(!user.getActive());
        userRepository.save(user);
        return userMapper.mapEntityToDto(user);
    }

    public UserResponseBaseDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalStateException("Email is already registered!");
        }

        User user = userMapper.mapDtoToEntity(userDto);
        userRepository.save(user);

        return userMapper.mapEntityToDto(user);
    }

    public UserResponseBaseDto updateUser(UUID id, UserDto userDto) {

        User origUser = getUser(id);
        User user = userMapper.mapDtoToEntity(userDto, origUser);

        userRepository.save(user);

        return userMapper.mapEntityToDto(user);

    }

    public LoginResponseDto signIn(Authentication authentication) {
        UserAuthPrincipal userDetails = (UserAuthPrincipal) authentication.getPrincipal();
        String jwtToken = jwtProvider.generateToken(authentication, userDetails.getUsername());
        Set<UserProjectResponseDto> userProjects = getUserProjects(userDetails);

        return createLoginResponseDto(userDetails, userProjects, jwtToken);
    }

    private Set<UserProjectResponseDto> getUserProjects(UserAuthPrincipal userDetails) {

        Set<UserProject> userProjects = userDetails.getProjects().stream().map(
                        p -> {
                            UserProjectId userProjectId = new UserProjectId();
                            userProjectId.setUserId(userDetails.getId());
                            userProjectId.setProjectId(p.getId());
                            return userProjectId;
                        }
                ).collect(Collectors.toSet())
                .stream().map(userProjectService::findById
                ).collect(Collectors.toSet());

        return userProjectMapper.mapEntitySetToDtoSet(userProjects);
    }

    private LoginResponseDto createLoginResponseDto(UserAuthPrincipal userDetails,
                                                    Set<UserProjectResponseDto> userProjectResponseDtoSet,
                                                    String jwtToken) {

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setId(userDetails.getId());
        loginResponseDto.setName(userDetails.getName());
        loginResponseDto.setEmail(userDetails.getUsername());
        loginResponseDto.setRole(userDetails.getRole());
        loginResponseDto.setAvatar(userDetails.getAvatar());
        loginResponseDto.setActive(userDetails.isEnabled());
        loginResponseDto.setCreated(userDetails.getCreated());
        loginResponseDto.setAuthProvider(userDetails.getAuthProvider());
        loginResponseDto.setProjects(userProjectResponseDtoSet);
        loginResponseDto.setToken(jwtToken);

        return loginResponseDto;
    }
}

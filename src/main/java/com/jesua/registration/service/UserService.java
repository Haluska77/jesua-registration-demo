package com.jesua.registration.service;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.entity.User;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.security.services.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        User user = userRepository.findByEmailAndActiveTrue(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User " + userName + " not found"));

        return new UserAuthPrincipal(user);
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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

        User origUser = userRepository.getOne(id);
        User user = userMapper.mapDtoToEntity(userDto, origUser);

        userRepository.save(user);

        return userMapper.mapEntityToDto(user);

    }
}

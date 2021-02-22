package com.jesua.registration.service;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.security.services.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        User user = userRepository.findByEmailAndActiveTrue(userName).orElseThrow(() -> new UsernameNotFoundException("User " + userName + " not found"));

        return new UserAuthPrincipal(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User switchActiveUserAccount(UUID userId) {

        return userRepository.findById(userId)
                .map(
                        u -> {
                            u.setActive(u.getActive() == Boolean.TRUE ? Boolean.FALSE : Boolean.TRUE);
                            return userRepository.save(u);
                        }).orElse(null);

    }

    public UserResponseDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalStateException("Email is already registered!");
        }

        User user = userMapper.mapDtoToEntity(userDto);
        userRepository.save(user);

        return userMapper.mapEntityToDto(user);
    }

}

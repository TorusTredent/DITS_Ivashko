package com.example.dits.service.impl;

import com.example.dits.DAO.UserRepository;
import com.example.dits.dto.UserInfoDTO;
import com.example.dits.entity.User;
import com.example.dits.service.RoleService;
import com.example.dits.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserInfoDTO getUserInfoByLogin(String login) {
        return modelMapper.map(repository.getUserByLogin(login), UserInfoDTO.class);
    }

    @Override
    public User getUserByLogin(String login) {
        return repository.getUserByLogin(login);
    }

    @Override
    public List<UserInfoDTO> getAllUsers() {
        return repository.findAll().stream().map(f -> modelMapper.map(f, UserInfoDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UserInfoDTO save(UserInfoDTO userInfoDTO) {
        return modelMapper.map(repository.save(User.builder()
                .firstName(userInfoDTO.getFirstName())
                .lastName(userInfoDTO.getLastName())
                .login(userInfoDTO.getLogin())
                .password(passwordEncoder.encode(userInfoDTO.getPassword()))
                .role(roleService.getRoleByRoleName(userInfoDTO.getRole()))
                .build()), UserInfoDTO.class);
    }

    @Override
    @Transactional
    public UserInfoDTO update(UserInfoDTO userInfoDTO) {
        User user = repository.getById(userInfoDTO.getUserId());
        user.setFirstName(userInfoDTO.getFirstName());
        user.setLastName(userInfoDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));
        user.setLogin(userInfoDTO.getLogin());
        user.setRole(roleService.getRoleByRoleName(userInfoDTO.getRole()));
        return modelMapper.map(user, UserInfoDTO.class);
    }
}

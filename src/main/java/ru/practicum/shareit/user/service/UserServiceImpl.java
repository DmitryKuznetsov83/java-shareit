package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.utils.DtoManager;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserJpaRepository userJpaRepository;

	@Transactional
	@Override
	public UserDto addUser(UserDto userDto) {
		User newUser = UserMapper.mapToNewUser(userDto);
		User savedUser = userJpaRepository.save(newUser);
		log.info("Create user with name {} id {}", savedUser.getName(), savedUser.getId());
		return UserMapper.mapToUserDto(savedUser);
	}

	@Transactional
	@Override
	public UserDto patchUser(Integer userId, Map<String, String> patch) {
		UserDto userDto = getUserById(userId);
		UserDto patchedUserDto = DtoManager.patch(userDto, patch);
		DtoManager.validate(patchedUserDto);
		User pachedUser = UserMapper.mapToUser(userDto);
		User savedUser = userJpaRepository.save(pachedUser);
		log.info("Patch user by id {}", userId);
		return UserMapper.mapToUserDto(savedUser);
	}

	@Transactional
	@Override
	public void deleteUserById(int userId) {
		User user = getUserEntityById(userId);
		log.info("Delete user by id {}", userId);
		userJpaRepository.deleteById(userId);
	}

	@Transactional(readOnly = true)
	@Override
	public UserDto getUserById(int userId) {
		return UserMapper.mapToUserDto(getUserEntityById(userId));
	}

	@Transactional(readOnly = true)
	@Override
	public User getUserEntityById(int userId) {
		return userJpaRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserDto> getUsers() {
		return UserMapper.mapToUserDtoList(userJpaRepository.findAll());
	}

}
package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailLoginAlreadyUsed;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public User addUser(User user) {
		checkEmailLoginAlreadyUsed(user, false);
		return userRepository.addUser(user);
	}

	@Override
	public User updateUser(User user) {
		checkEmailLoginAlreadyUsed(user, true);
		return userRepository.updateUser(user);
	}

	@Override
	public void deleteUserById(int userId) {
		// checking
		User user = userRepository.getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException("User", userId);
		}

		// delete
		userRepository.deleteUserById(userId);
	}

	@Override
	public User getUserById(int userId) {
		// checking
		User user = userRepository.getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException("User", userId);
		}

		// get
		return user;
	}

	@Override
	public List<User> getUsers() {
		return userRepository.getUsers();
	}


	// PRIVATE
	private void checkEmailLoginAlreadyUsed(User user, boolean excludUserId) {
		String email = user.getEmail();
		String login = user.getName();

		boolean emailAlreadyUsed;
		boolean loginAlreadyUsed;
		if (excludUserId) {
			emailAlreadyUsed = userRepository.emailAlreadyUsed(email, user.getId());
			loginAlreadyUsed = userRepository.loginAlreadyUsed(login, user.getId());
		} else {
			emailAlreadyUsed = userRepository.emailAlreadyUsed(email);
			loginAlreadyUsed = userRepository.loginAlreadyUsed(login);
		}

		if (emailAlreadyUsed || loginAlreadyUsed) {
			List<String> warningList = new ArrayList<>();
			if (emailAlreadyUsed) {
				warningList.add("E-mail: " + email + " is already used");
			}
			if (loginAlreadyUsed) {
				warningList.add("Login: " + login + " is already used");
			}
			String warning = String.join(", ", warningList);
			throw new EmailLoginAlreadyUsed(warning);
		}
	}

}

package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserInMemoryRepository implements UserRepository {
	private Integer userId = 0;
	private final Map<Integer, User> idUser = new HashMap<>();
	private final Map<String, User> emailUser = new HashMap<>();
	private final Map<String, User> nameUser = new HashMap<>();


	// USERS - CRUD
	@Override
	public User addUser(User user) {
		Integer currentId = getUserId();
		user.setId(currentId);

		idUser.put(currentId, user);
		emailUser.put(user.getEmail(), user);
		nameUser.put(user.getName(), user);

		return user;
	}

	@Override
	public User updateUser(User user) {
		Integer userId = user.getId();
		User oldUser = idUser.get(userId);
		if (oldUser == null) {
			return null;
		}
		String oldEmail = oldUser.getEmail();
		String oldName = oldUser.getName();

		idUser.put(user.getId(), user);

		if (!oldEmail.equals(user.getEmail())) {
			emailUser.remove(oldEmail);
			emailUser.put(user.getEmail(), user);
		}

		if (!oldName.equals(user.getName())) {
			nameUser.remove(oldName);
			nameUser.put(user.getName(), user);
		}

		return user;
	}

	@Override
	public User getUser(Integer userid) {
		return idUser.get(userid);
	}

	@Override
	public List<User> getUsers() {
		return new ArrayList<>(idUser.values());
	}

	@Override
	public void deleteUserById(Integer userId) {
		User user = idUser.get(userId);
		if (user == null) {
			return;
		}

		idUser.remove(userId);
		emailUser.remove(user.getEmail());
		nameUser.remove(user.getName());
	}


	// USERS - Checking
	@Override
	public boolean emailAlreadyUsed(String email) {
		return emailUser.containsKey(email);
	}

	@Override
	public boolean emailAlreadyUsed(String email, Integer excludedUserId) {
		User user = emailUser.get(email);
		return user != null && !excludedUserId.equals(user.getId());
	}

	@Override
	public boolean loginAlreadyUsed(String login) {
		return nameUser.containsKey(login);
	}

	@Override
	public boolean loginAlreadyUsed(String login, Integer excludedUserId) {
		User user = nameUser.get(login);
		return user != null && !excludedUserId.equals(user.getId());
	}


	// PRIVATE
	private Integer getUserId() {
		return ++userId;
	}
}

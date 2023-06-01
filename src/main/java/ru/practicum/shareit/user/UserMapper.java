package ru.practicum.shareit.user;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

	private static final ModelMapper modelMapper = new ModelMapper();

	public static User mapToNewUser(UserDto userDto) {
		return modelMapper.map(userDto, User.class);
	}

	public static User mapToUser(UserDto userDto) {
		return modelMapper.map(userDto, User.class);
	}

	public static UserDto mapToUserDto(User user) {
		return modelMapper.map(user, UserDto.class);
	}

	public static List<UserDto> mapToUserDtoList(List<User> userList) {
		return userList.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
	}

}
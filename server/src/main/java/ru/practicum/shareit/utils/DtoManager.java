package ru.practicum.shareit.utils;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.List;


public class DtoManager {

	@SneakyThrows
	public static <T> T patch(T dto, T patch) {
		List<Field> patchFields = List.of(patch.getClass().getDeclaredFields());
		patchFields.forEach(field -> {
			try {
				field.setAccessible(true);
				if ("id".equals(field.getName())) {
					return;
				}
				Object patchFieldValue = field.get(patch);
				if (patchFieldValue == null) {
					return;
				}
				field.setAccessible(true);
				field.set(dto, patchFieldValue);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
		return dto;
	}

}

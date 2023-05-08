package ru.practicum.shareit.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.validation.Validation.buildDefaultValidatorFactory;

public class DtoManager {

	private static final Validator validator = buildDefaultValidatorFactory().getValidator();

	public static <T> T patch(T dto, Map<String, String> patchMap) {

		List<Field> fieldsAsList = List.of(dto.getClass().getDeclaredFields());
		Map<String, Field> fieldsAsMap = fieldsAsList
				.stream()
				.collect(Collectors.toMap(Field::getName, Function.identity()));
		patchMap.forEach((k, v) -> {
			if ("id".equals(k)) {
				return;
			}
			Field field = fieldsAsMap.get(k);
			if (field != null) {
				try {
					field.setAccessible(true);
					if (field.getType() == String.class) {
						field.set(dto, v);
					} else if (field.getType() == Boolean.class) {
						field.set(dto, Boolean.valueOf(v));
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PATCH has unexpected field " + k);
			}
		});
		return dto;
	}

	public static <T> void validate(T dto) {

		Set<ConstraintViolation<T>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

}

package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.exception.ResourceNotAvailableException;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTestIT {

	private final BookingService bookingService;
	private final UserService userService;
	private final ItemService itemService;

	@Test
	void addBooking_whenItemIsNotAvailable_thenThrowResourceNotAvailableException() {
		int ownerId = userService.addUser(UserDto.builder().name("Owner").email("owner@mail.org").build()).getId();

		ItemRequestDto itemRequestDto = ItemRequestDto.builder()
				.name("Item A")
				.description("Item A")
				.available(false)
				.build();
		int itemId = itemService.addItem(itemRequestDto, ownerId).getId();
		BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
				.itemId(itemId)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build();

		assertThrows(ResourceNotAvailableException.class, () -> bookingService.addBooking(bookingRequestDto, ownerId));

	}

}

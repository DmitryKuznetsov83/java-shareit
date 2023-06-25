package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class BookingRequestDtoTest {

	@Autowired
	private JacksonTester<BookingRequestDto> jacksonTester;

	@Test
	@SneakyThrows
	void bookingRequestDto_JsonToDto() {
		// given
		String json = "{\"itemId\": 1, \"start\": \"2000-01-01T00:00:00\", \"end\": \"2000-01-02T00:00:00\"}";
		LocalDateTime expectedStart = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		// when
		BookingRequestDto bookingRequestDto = jacksonTester.parseObject(json);
		// then
		assertThat(bookingRequestDto.getStart()).isEqualTo(expectedStart);
	}

	@Test
	@SneakyThrows
	void bookingRequestDto_DtoToJson() {
		// given
		LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2000, 1, 2, 0, 0, 0);
		BookingRequestDto bookingRequestDto = BookingRequestDto.builder().itemId(1).start(start).end(end).build();
		// when
		JsonContent<BookingRequestDto> jsonContent = jacksonTester.write(bookingRequestDto);
		// then
		assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2000-01-01T00:00:00");
	}

}
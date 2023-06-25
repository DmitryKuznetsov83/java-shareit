package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.annotation.StartEndFields;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@StartEndFields
public class BookingRequestDto {
	@NotNull
	public Integer itemId;
	@NotNull
	@FutureOrPresent
	public LocalDateTime start;
	@NotNull
	public LocalDateTime end;
}

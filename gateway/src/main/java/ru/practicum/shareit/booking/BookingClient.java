package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.enums.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postBooking(BookingRequestDto bookingRequestDto, Integer bookerId) {
        return post("", bookerId, bookingRequestDto);
    }

    public ResponseEntity<Object> approveBooking(Integer bookingId, Integer ownerId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId, ownerId);
    }

    public ResponseEntity<Object> getBookingById(Integer bookingId, Integer userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookersBookings(Integer bookerId, BookingState state, Integer from, Integer size) {
        String path = "?state=" + state + ((from != null) ? "&from=" + from + "&size=" + size : "");
        return get(path, bookerId);
    }

    public ResponseEntity<Object> getOwnersBookings(Integer ownerId, BookingState state, Integer from, Integer size) {
        String path = "/owner?state=" + state + ((from != null) ? "&from=" + from + "&size=" + size : "");
        return get(path, ownerId.longValue());
    }

}

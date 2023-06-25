package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestJpaRepository;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

	@InjectMocks
	RequestServiceImpl requestService;

	@Mock
	private RequestJpaRepository requestJpaRepository;

	private UserService userService;
	private ItemService itemService;

	User requester;
	Request request;
	RequestRequestDto requestRequestDto;
	User user;
	Item item;

	@BeforeEach
	void setUp() {
		userService = Mockito.mock(UserService.class);
		itemService = Mockito.mock(ItemService.class);

		ReflectionTestUtils.setField(requestService, "userService", userService);
		ReflectionTestUtils.setField(requestService, "itemService", itemService);

		request = Request.builder()
				.description("Request for item A")
				.requester(requester)
				.created(LocalDateTime.now())
				.build();

		requestRequestDto = RequestRequestDto.builder()
				.description("Request for item A")
				.build();

		user = User.builder()
				.name("User")
				.email("user@mail.org")
				.build();

		item = Item.builder()
				.name("Item A")
				.description("Item A description")
				.available(true)
				.owner(user)
				.request(request)
				.build();
	}

	@Test
	void addRequest_whenRequesterFound_thenRequestAdded() {
		// when
		when(userService.getUserEntityById(1)).thenReturn(requester);
		when(requestJpaRepository.save(any())).then(returnsFirstArg());
		RequestResponseDto requestResponseDto = requestService.addRequest(requestRequestDto, 1);
		// then
		assertThat(requestResponseDto.getDescription(), equalTo(requestRequestDto.getDescription()));
		verify(requestJpaRepository).save(any());
	}

	@Test
	void getRequestById_whenRequestFound_thenRequestReturned() {
		// when
		when(requestJpaRepository.findById(anyInt())).thenReturn(Optional.of(request));
		when(userService.getUserEntityById(anyInt())).thenReturn(user);
		when(itemService.getItemsByRequestId(anyInt())).thenReturn(Collections.emptyList());
		RequestResponseDto requestResponseDto = requestService.getRequestById(1, 1);
		// then
		assertThat(requestResponseDto.getDescription(), equalTo(request.getDescription()));
	}

	@Test
	void getOwnRequests_whenRequesterFound_thenRequestListReturned() {
		// when
		when(userService.getUserEntityById(anyInt())).thenReturn(user);
		when(requestJpaRepository.findAllByRequesterIdOrderByCreatedAsc(anyInt())).thenReturn(Collections.singletonList(request));
		when(itemService.getOwnRequestsItems(anyInt())).thenReturn(Collections.emptyList());
		List<RequestResponseDto> requestDtoList = requestService.getOwnRequests(1);
		// then
		assertThat(requestDtoList.size(), equalTo(1));
	}

	@Test
	void getOthersRequests_whenUserFoundAndNoPagination_thenRequestListReturned() {
		// when
		when(userService.getUserEntityById(anyInt())).thenReturn(user);
		when(requestJpaRepository.findAllByRequesterIdNot(anyInt())).thenReturn(Collections.singletonList(request));
		when(itemService.getOtherRequestsItems(anyInt())).thenReturn(Collections.singletonList(item));
		List<RequestResponseDto> requestDtoList = requestService.getOthersRequests(1, null, null);
		// then
		assertThat(requestDtoList.size(), equalTo(1));
	}

	@Test
	void getOthersRequests_whenUserFoundAndPagination_thenEmptyListReturned() {
		// given
		Page<Request> requestPage = new PageImpl<>(Collections.singletonList(request));
		// when
		when(userService.getUserEntityById(anyInt())).thenReturn(user);
		when(requestJpaRepository.findAllByRequesterIdNot(anyInt(), any())).thenReturn(requestPage);
		when(itemService.getItemsByRequestList(any())).thenReturn(Collections.singletonList(item));
		List<RequestResponseDto> requestDtoList = requestService.getOthersRequests(1, 0, 10);
		// then
		assertThat(requestDtoList.size(), equalTo(1));
	}

}
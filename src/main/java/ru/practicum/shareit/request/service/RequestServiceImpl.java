package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestJpaRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

	private final RequestJpaRepository requestJpaRepository;

	private UserService userService;
	private ItemService itemService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	public RequestResponseDto addRequest(RequestRequestDto requestRequestDto, Integer requesterId) {
		User requester = userService.getUserEntityById(requesterId);
		Request newRequest = RequestMapper.mapToNewRequest(requestRequestDto, requester);
		Request savedRequest = requestJpaRepository.save(newRequest);
		log.info("Create item request with id {}", savedRequest.getId());
		return RequestMapper.mapToRequestDto(savedRequest, Collections.EMPTY_LIST);
	}

	@Override
	public RequestResponseDto getRequestById(Integer requestId, Integer userId) {
		Request request = getRequestEntityById(requestId);
		User user = userService.getUserEntityById(userId);
		List<ItemRequestDto> itemRequestDtoList = itemService.getItemsByRequestId(requestId);
		RequestResponseDto requestResponseDto = RequestMapper.mapToRequestDto(request, itemRequestDtoList);
		return requestResponseDto;
	}

	@Override
	public Request getRequestEntityById(Integer requestId) {
		return requestJpaRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Request",
				requestId));
	}

	@Override
	public List<RequestResponseDto> getOwnRequests(Integer requesterId) {
		User requester = userService.getUserEntityById(requesterId);
		List<Request> requestList = requestJpaRepository.findAllByRequesterIdOrderByCreatedAsc(requesterId);
		List<Item> itemList = itemService.getOwnRequestsItems(requesterId);
		Map<Request, List<Item>> requestItemsMap = itemList.stream().collect(Collectors.groupingBy(Item::getRequest));
		return RequestMapper.mapToRequestDtoList(requestList, requestItemsMap);
	}

	@Override
	public List<RequestResponseDto> getOthersRequests(Integer userId, Integer from, Integer size) {
		User user = userService.getUserEntityById(userId);
		List<Item> itemList;
		List<Request> requestList;
		if (from == null) {
			requestList = requestJpaRepository.findAllByRequesterIdNot(userId);
			itemList = itemService.getOtherRequestsItems(userId);
		} else {
			PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("Created").ascending());
			requestList = requestJpaRepository.findAllByRequesterIdNot(userId, pageRequest).getContent();
			itemList = itemService.getItemsByRequestList(requestList);
		}
		Map<Request, List<Item>> requestItemsMap = itemList.stream().collect(Collectors.groupingBy(Item::getRequest));
		return RequestMapper.mapToRequestDtoList(requestList, requestItemsMap);
	}

}
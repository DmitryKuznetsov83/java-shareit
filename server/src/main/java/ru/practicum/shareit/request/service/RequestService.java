package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;

import java.util.List;

public interface RequestService {

	RequestResponseDto addRequest(RequestRequestDto requestRequestDto, Integer requesterId);

	RequestResponseDto getRequestById(Integer requestId, Integer userId);

	Request getRequestEntityById(Integer requestId);

	List<RequestResponseDto> getOwnRequests(Integer requesterId);

	List<RequestResponseDto> getOthersRequests(Integer userId, Integer from, Integer size);

}
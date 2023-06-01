package ru.practicum.shareit.item.repository;

public interface ItemWithBookingProjection {

	Integer getId();

	String getName();

	String getDescription();

	Boolean getAvailable();

	Integer getLastBookingId();

	Integer getNextBookingId();

	Integer getLastBookingBookerId();

	Integer getNextBookingBookerId();

}
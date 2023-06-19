package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ShareItAppTest {

	@Test
	void main() {
		Assertions.assertDoesNotThrow(ShareItApp::new);
		Assertions.assertDoesNotThrow(() -> ShareItApp.main(new String[]{}));
	}
}
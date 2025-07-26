package com.codebloom.cineman;

import com.codebloom.cineman.controller.MovieController;
import com.codebloom.cineman.controller.admin.MovieAController;
import com.codebloom.cineman.controller.auth.AuthenticationController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CinemanApplicationTests {
	@InjectMocks
	private AuthenticationController authenticationController;
	@InjectMocks
	private MovieController movieController;
	@InjectMocks
	private MovieAController movieAController;
	@Test
	void contextLoads() {
		Assertions.assertNotNull(authenticationController);
		Assertions.assertNotNull(movieController);
		Assertions.assertNotNull(movieAController);
	}

}

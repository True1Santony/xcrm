package com.xcrm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class XcrmApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testSingleAccess() {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/api/organizacion", String.class);
		assertThat(response.getStatusCodeValue())
				.as("Check that the API is accessible")
				.isEqualTo(200);
	}

	@Test
	public void testConcurrentAccess() throws InterruptedException {
		int threadCount = 50;
		CountDownLatch latch = new CountDownLatch(threadCount);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/api/organizacion", String.class);
					assertThat(response.getStatusCodeValue())
							.as("Ensure the API responds with 200 status code")
							.isEqualTo(200);
				} finally {
					latch.countDown(); // Decrement the latch count
				}
			});
		}

		latch.await(); // Wait for all threads to finish
		executorService.shutdown(); // Properly shut down the executor
	}
}

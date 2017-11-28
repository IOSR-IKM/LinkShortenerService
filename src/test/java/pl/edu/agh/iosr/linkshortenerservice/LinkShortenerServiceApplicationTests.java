package pl.edu.agh.iosr.linkshortenerservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.agh.iosr.linkshortenerservice.controller.LinkController;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;
import pl.edu.agh.iosr.linkshortenerservice.repository.LinkRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LinkShortenerServiceApplicationTests {

	private static final String ORIGINAL_URL = "original";
	private static final String HASH = "hash";

	@Mock
	private LinkRepository repository;

	private LinkController controller;

	@Before
	public void setUp() {
		initMocks(this);
		controller = new LinkController(repository);
	}

	@Test
	public void controllerShouldAddNewPairing() {
		// GIVEN
		ArgumentCaptor<Link> captor = ArgumentCaptor.forClass(Link.class);

		// WHEN
		ResponseEntity<String> response = controller.addNewPairing(HASH, ORIGINAL_URL);

		// THEN
		verify(repository).save(captor.capture());
		assertEquals(HASH, captor.getValue().getHash());
		assertEquals(ORIGINAL_URL, captor.getValue().getOriginalUrl());
		assertEquals(HASH, response.getBody());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	public void controllerShouldRedirectToOriginalUrlByHash() {
		// GIVEN
		Link link = new Link(null, ORIGINAL_URL, HASH);
		when(repository.findOneByHash(HASH)).thenReturn(Optional.of(link));

		// WHEN
		ResponseEntity<String> response = controller.redirectByHash(HASH);

		// THEN
		assertEquals(ORIGINAL_URL, response.getBody());
		assertEquals(HttpStatus.TEMPORARY_REDIRECT, response.getStatusCode());
	}
}

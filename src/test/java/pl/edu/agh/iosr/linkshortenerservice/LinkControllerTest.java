package pl.edu.agh.iosr.linkshortenerservice;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.agh.iosr.linkshortenerservice.controller.LinkController;
import pl.edu.agh.iosr.linkshortenerservice.messaging.MessageSender;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;
import pl.edu.agh.iosr.linkshortenerservice.repository.LinkRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LinkControllerTest {
    private static final String ORIGINAL_URL = "original";
    private static final String SHORTCUT = "short";
    private static final int SHORTCUT_LENGTH = 7;

    @Mock
    private LinkRepository repository;
    @Mock
    private MessageSender sender;

    private LinkController controller;

    @Before
    public void setUp() {
        initMocks(this);
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        controller = new LinkController(repository, generator, sender);
    }

    @Test
    public void shouldAddNewShortcut() {
        // GIVEN
        ArgumentCaptor<Link> captor = ArgumentCaptor.forClass(Link.class);

        // WHEN
        ResponseEntity<Link> response = controller.addNewShortcut(ORIGINAL_URL, true);

        // THEN
        verify(repository).save(captor.capture());
        Link link = captor.getValue();
        assertEquals(SHORTCUT_LENGTH, link.getShortcut().length());
        assertEquals(ORIGINAL_URL, link.getOriginalUrl());
        assertEquals(link, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(sender).sendCacheNotification(captor.capture());
        assertEquals(link, captor.getValue());
    }

    @Test
    public void shouldRedirectToOriginalUrlByHash() throws URISyntaxException {
        // GIVEN
        Link link = new Link(null, ORIGINAL_URL, SHORTCUT);
        when(repository.findOneByShortcut(SHORTCUT)).thenReturn(Optional.of(link));

        // WHEN
        ResponseEntity<Object> response = controller.redirectByShortcut(SHORTCUT);

        // THEN
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals(ORIGINAL_URL, response.getHeaders().getLocation().toString());
    }
}

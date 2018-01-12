package pl.edu.agh.iosr.linkshortenerservice.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.iosr.linkshortenerservice.messaging.MessageSender;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;
import pl.edu.agh.iosr.linkshortenerservice.repository.LinkRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

@RestController("/lss")
@RequiredArgsConstructor
public class LinkController {

    private final LinkRepository repository;
    private final RandomStringGenerator randomStringGenerator;
    private final MessageSender sender;

    @GetMapping("/{shortcut}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Object> redirectByShortcut(@PathVariable String shortcut) throws URISyntaxException {
        Link link = repository.findOneByShortcut(shortcut).orElseThrow(() -> new RuntimeException("Could not find link"));

        URI uri = new URI(link.getOriginalUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @PostMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<Link> addNewShortcut(@RequestBody String originalUrl, @RequestParam boolean isPersistent) {
        String shortcut = randomStringGenerator.generate(7);

        Link link = new Link(null, originalUrl, shortcut);

        repository.save(link);

        sender.sendCacheNotification(link);

        if (!isPersistent) {
            scheduleRemovalTask(link);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(link);
    }

    private void scheduleRemovalTask(Link link) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                repository.delete(link);
            }
        }, 10L * 60 * 1000);
    }
}

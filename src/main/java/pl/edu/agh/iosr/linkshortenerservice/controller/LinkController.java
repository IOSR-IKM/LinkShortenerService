package pl.edu.agh.iosr.linkshortenerservice.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.iosr.linkshortenerservice.messaging.MessageSender;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;
import pl.edu.agh.iosr.linkshortenerservice.repository.LinkRepository;

@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkRepository repository;
    private final RandomStringGenerator randomStringGenerator;
    private final MessageSender sender;

    @GetMapping("/{shortcut}")
    public ResponseEntity<String> redirectByShortcut(@PathVariable String shortcut) {
        Link link = repository.findOneByShortcut(shortcut).orElseThrow(() -> new RuntimeException("Could not find link"));

        return new ResponseEntity<>(link.getOriginalUrl(), HttpStatus.TEMPORARY_REDIRECT);
    }

    @PostMapping
    public ResponseEntity<String> addNewShortcut(@RequestBody String originalUrl) {
        String shortcut = randomStringGenerator.generate(7);

        Link link = new Link(null, originalUrl, shortcut);

        repository.save(link);

        sender.sendCacheNotification(link);

        return new ResponseEntity<>(shortcut, HttpStatus.CREATED);
    }
}

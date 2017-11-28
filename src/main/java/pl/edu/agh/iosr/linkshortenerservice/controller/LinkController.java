package pl.edu.agh.iosr.linkshortenerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;
import pl.edu.agh.iosr.linkshortenerservice.repository.LinkRepository;

@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkRepository repository;

    @GetMapping("/{hash}")
    public ResponseEntity<String> redirectByHash(@PathVariable String hash) {
        Link link = repository.findOneByHash(hash).orElseThrow(() -> new RuntimeException("Could not find link"));

        return new ResponseEntity<>(link.getOriginalUrl(), HttpStatus.TEMPORARY_REDIRECT);
    }

    @PostMapping("/{hash}")
    public ResponseEntity<String> addNewPairing(@PathVariable String hash, @RequestBody String originalUrl) {
        Link link = new Link(null, originalUrl, hash);

        repository.save(link);

        return new ResponseEntity<>(link.getHash(), HttpStatus.CREATED);
    }
}

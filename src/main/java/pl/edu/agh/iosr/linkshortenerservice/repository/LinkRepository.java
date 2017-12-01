package pl.edu.agh.iosr.linkshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findOneByShortcut(String shortcut);
}

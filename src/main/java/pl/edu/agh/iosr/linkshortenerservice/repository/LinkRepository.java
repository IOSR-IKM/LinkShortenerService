package pl.edu.agh.iosr.linkshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.CannotCreateTransactionException;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class LinkRepository {
    private static final Logger logger = LoggerFactory.getLogger(LinkRepository.class);
    private final List<Link> cache = new ArrayList<>();
    private final LinkCrudRepository crudRepository;

    public Optional<Link> findLinkByShortcut(String shortcut) {
        Optional<Link> link = getFromCache(shortcut);
        if (link.isPresent()) {
            return link;
        }
        return crudRepository.findOneByShortcut(shortcut);
    }

    private Optional<Link> getFromCache(String shortcut) {
        return cache.stream().filter(link -> link.getShortcut().equals(shortcut)).findFirst();
    }

    public void saveLink(Link link) {
        try {
            crudRepository.save(link);
            scheduleCachePrune();
        } catch (CannotCreateTransactionException | DataAccessException e) {
            logger.error("Unable to save in database, saving in cache", e);
            cache.add(link);
        }
    }

    private void scheduleCachePrune() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cache.removeIf(link -> saveFromCache(link));
            }
        }, 10L * 1000);
    }

    private boolean saveFromCache(Link link) {
        try {
            crudRepository.save(link);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void scheduleRemovalTask(Link link) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                crudRepository.delete(link);
            }
        }, 10L * 60 * 1000);
    }
}

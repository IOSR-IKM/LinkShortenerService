package pl.edu.agh.iosr.linkshortenerservice.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);
    private final ConnectionFactory connectionFactory;
    private final List<Link> cache = new ArrayList<>();
    @Value("${queue.name}")
    private String queueName = "cache_notify";

    public void sendCacheNotification(Link link) {
        if (sendLinkToQueue(link)) {
            scheduleCachePrune();
        } else {
            logger.error("Unable to send to queue, caching locally");
            cache.add(link);
        }
    }

    private boolean sendLinkToQueue(Link link) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            String message = link.getShortcut() + "," + link.getOriginalUrl();
            channel.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException | TimeoutException e) {
            return false;
        } finally {
            if (channel != null) {
                closeChannel(channel);
            }
            if (connection != null) {
                closeConnection(connection);
            }
        }
        return true;
    }

    private void scheduleCachePrune() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cache.removeIf(link -> sendLinkToQueue(link));
            }
        }, 10L * 1000);
    }

    private void closeChannel(Channel channel) {
        try {
            channel.close();
        } catch (IOException | TimeoutException e) {
            logger.error("Unable to close channel", e);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (IOException e) {
            logger.error("Unable to close connection", e);
        }
    }
}

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
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);
    private final ConnectionFactory connectionFactory;
    @Value("${queue.name}")
    private String queueName = "cache_notify";

    public void sendCacheNotification(Link link) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, link.getOriginalUrl().getBytes());
        } catch (IOException | TimeoutException e) {
            logger.error("Unable to save in cache", e);
        } finally {
            if (channel != null) {
                closeChannel(channel);
            }
            if (connection != null) {
                closeConnection(connection);
            }
        }
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

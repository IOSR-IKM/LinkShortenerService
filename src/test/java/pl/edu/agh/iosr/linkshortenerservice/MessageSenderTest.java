package pl.edu.agh.iosr.linkshortenerservice;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import pl.edu.agh.iosr.linkshortenerservice.messaging.MessageSender;
import pl.edu.agh.iosr.linkshortenerservice.model.Link;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageSenderTest {
    private static final String QUEUE_NAME = "cache_notify";

    @Mock
    private ConnectionFactory connectionFactory;
    @Mock
    private Connection connection;
    @Mock
    private Channel channel;

    private MessageSender sender;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
        sender = new MessageSender(connectionFactory);
    }

    @Test
    public void shouldSendCacheNotification() throws Exception {
        // GIVEN
        Link link = new Link(null, "original", "short");

        // WHEN
        sender.sendCacheNotification(link);

        // THEN
        verify(channel).queueDeclare(QUEUE_NAME, false, false, false, null);
        verify(channel).basicPublish("", QUEUE_NAME, null, "original".getBytes());
        verify(channel).close();
        verify(connection).close();
    }
}

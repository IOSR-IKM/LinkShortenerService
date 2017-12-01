package pl.edu.agh.iosr.linkshortenerservice.configuration;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    private static final String BROKER_ADDRESS = "localhost";

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(BROKER_ADDRESS);
        return factory;
    }

    @Bean
    public RandomStringGenerator randomStringGenerator() {
        return new RandomStringGenerator.Builder()
                        .withinRange('0', 'z')
                        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                        .build();
    }
}

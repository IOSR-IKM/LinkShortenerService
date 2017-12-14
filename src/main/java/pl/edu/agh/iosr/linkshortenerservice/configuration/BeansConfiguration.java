package pl.edu.agh.iosr.linkshortenerservice.configuration;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Value("${rabbit.address}")
    private String rabbitAddress;
    @Value("${rabbit.username}")
    private String rabbitUsername;
    @Value("${rabbit.password}")
    private String rabbitPassword;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitAddress);
        factory.setUsername(rabbitUsername);
        factory.setPassword(rabbitPassword);
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

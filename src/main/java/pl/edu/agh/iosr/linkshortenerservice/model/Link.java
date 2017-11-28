package pl.edu.agh.iosr.linkshortenerservice.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(exclude = "id")
public class Link {

    @Id
    @GeneratedValue
    private final Long id;
    private final String originalUrl;
    private final String hash;

}

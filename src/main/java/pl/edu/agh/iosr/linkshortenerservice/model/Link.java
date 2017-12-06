package pl.edu.agh.iosr.linkshortenerservice.model;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = "id")
public class Link {

    @Id
    @GeneratedValue
    private Long id;
    private String originalUrl;
    private String shortcut;

}

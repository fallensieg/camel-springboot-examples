package uk.co.joshjordan.camel_springboot_examples.beans;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Data
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "club")
    private String club;

    @Column(name = "position")
    private String position;

    @Column(name = "handed")
    private String handed;

}

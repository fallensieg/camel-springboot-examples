package uk.co.joshjordan.camel_springboot_examples.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Entity
@Data
@Table(name = "player")
@NamedQuery(name = "getAllRows", query= "Select x from Player x") // Hibernate syntax, not tsql syntax
public class Player implements Serializable {

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

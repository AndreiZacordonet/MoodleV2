package dev.zaco.MariaDB_testing.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game {
    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "year_of_publishing")
    private Integer yearOfPublishing;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @OneToMany(mappedBy = "game", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)    // ERROR: Hibernate transaction: Unable to commit against JDBC Connection; bad SQL grammar []
    private Set<Achievement> achievements = new HashSet<>();
}

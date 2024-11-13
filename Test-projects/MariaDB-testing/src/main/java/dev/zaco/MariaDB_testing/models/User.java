package dev.zaco.MariaDB_testing.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User {
    @Basic
    @Column(name = "username")
    private String username;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToMany(cascade = {
            CascadeType.MERGE,      // MERGE will try to reattach the entity
    },
            fetch = FetchType.LAZY)
    @JoinTable(name = "games_users",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Game> games = new HashSet<>();
}

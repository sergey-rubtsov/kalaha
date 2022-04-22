package com.game.kalaha.model;

import com.game.kalaha.repository.converter.BoardConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Lob
    @Convert(converter = BoardConverter.class)
    private int[][] board;

    @Builder.Default
    @OrderColumn
    @Column(unique = true)
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    @Column(nullable = false)
    private UUID nextPlayer;

    @Builder.Default
    @Column
    private GameStatus status = GameStatus.NEW;

}

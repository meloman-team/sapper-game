package ru.catr.game.sapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class GameInfo {
    private UUID gameId;
    private int width;
    private int height;
    private int minesCount;
    private List<List<CellState>> field;

    public GameInfo(GameInfo other) {
        if (other == null) {
            throw new IllegalArgumentException("Игра не найдена.");
        }

        this.gameId = other.gameId;
        this.width = other.width;
        this.height = other.height;
        this.minesCount = other.minesCount;

        if (other.field == null) {
            this.field = null;
        } else {
            this.field = other.field.stream()
                    .map(row -> {
                        if (row == null) return null;
                        return row.stream()
                                .map(CellState::new)
                                .collect(Collectors.toList());
                    })
                    .collect(Collectors.toList());
        }
    }
}

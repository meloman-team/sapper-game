package ru.catr.game.sapper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Состояние ячейки игрового поля
 */
@Data
@NoArgsConstructor
public class CellState {
    /**
     * заминированный
     */
    private boolean mined = false;
    /**
     * открытый
     */
    private boolean opened = false;
    /**
     * Количество мин в соседних ячейках
     */
    private int adjacentMines = 0;

    public CellState(CellState other) {
        this.mined = other.mined;
        this.opened = other.opened;
        this.adjacentMines = other.adjacentMines;
    }
}

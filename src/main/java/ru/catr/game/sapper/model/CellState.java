package ru.catr.game.sapper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Состояние ячейки игрового поля
 */
@Data
@NoArgsConstructor
public class CellState {
    private boolean hasMine = false;
    private boolean opened = false;
    private int adjacentMines = 0;
}

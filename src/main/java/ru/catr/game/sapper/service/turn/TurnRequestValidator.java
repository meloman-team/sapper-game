package ru.catr.game.sapper.service.turn;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.GameInfo;
import ru.catr.game.sapper.model.GameTurnRequest;

/**
 * Валидатор запроса на открытие ячейки игрового поля
 */
@Component
@AllArgsConstructor
public class TurnRequestValidator {

    public void validateRequest(@Nullable GameTurnRequest request) {
        if (request == null) throw new IllegalArgumentException("Запрос не должен быть null");
        if (request.getGameId() == null) throw new IllegalArgumentException("Идентификатор игры не должен быть null");
        if (request.getCol() == null) throw new IllegalArgumentException("Номер колонки не должен быть null");
        if (request.getRow() == null) throw new IllegalArgumentException("Номер ряда не должен быть null");
        if (request.getCol() < 0) throw new IllegalArgumentException("Номер колонки должен быть больше 0");
        if (request.getRow() < 0) throw new IllegalArgumentException("Номер ряда должен быть больше 0");
    }

    public void validateRequest(@NotNull GameTurnRequest request, @Nullable GameInfo gameInfo) {
        if (gameInfo == null || gameInfo.getField() == null) throw new IllegalArgumentException("Игра не найдена.");
        int maxRow = gameInfo.getField().size() - 1;
        int maxCol = gameInfo.getField().getFirst().size() - 1;
        if (request.getRow() > maxRow) throw new IllegalArgumentException("Номер ряда вышел за рамки игрового поля");
        if (request.getCol() > maxCol) throw new IllegalArgumentException("Номер колонки вышел за рамки игрового поля");
    }

}

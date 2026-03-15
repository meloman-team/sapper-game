package ru.catr.game.sapper.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.GameInfo;
import ru.catr.game.sapper.util.CommonValidator;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class GameInfoValidator {

    private final CommonValidator validator;

    public void validateGameId(UUID gameId) {
        Objects.requireNonNull(gameId, "Идентификатор игры не должен быть null");
    }

    public void validateGameInfo(GameInfo gameInfo) {
        Objects.requireNonNull(gameInfo, "Информация об игре не должна быть null");
        Objects.requireNonNull(gameInfo.getField(), "Игровое поле не должно быть null");
        validateGameId(gameInfo.getGameId());
        int height = gameInfo.getHeight();
        int width = gameInfo.getWidth();
        validator.validateHeight(height);
        validator.validateWidth(width);
        validator.validateMinesCount(height, width, gameInfo.getMinesCount());
        // TODO добавить проверку на field и CellState
    }

}

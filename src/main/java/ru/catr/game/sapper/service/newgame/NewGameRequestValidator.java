package ru.catr.game.sapper.service.newgame;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.NewGameRequest;
import ru.catr.game.sapper.util.CommonValidator;

/**
 * Валидатор запроса на создание новой игры
 */
@Component
@AllArgsConstructor
public class NewGameRequestValidator {

    private final CommonValidator validator;

    /**
     * Валидируем количество мин и размеры игрового поля
     */
    public void validateRequest(@Nullable NewGameRequest newGameRequest) {
        if (newGameRequest == null) throw new IllegalArgumentException("Запрос не должен быть null");
        Integer height = newGameRequest.getHeight();
        Integer width = newGameRequest.getWidth();
        validator.validateHeight(height);
        validator.validateWidth(width);
        validator.validateMinesCount(height, width, newGameRequest.getMinesCount());
    }

}

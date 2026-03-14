package ru.catr.game.sapper.service.newgame;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.config.prop.ValidateFieldConfig;
import ru.catr.game.sapper.model.NewGameRequest;

/**
 * Валидатор запроса на создание новой игры
 */
@Component
@AllArgsConstructor
public class NewGameRequestValidator {

    private final ValidateFieldConfig config;

    /**
     * Валидируем количество мин и размеры игрового поля
     */
    public void validateRequest(@Nullable NewGameRequest newGameRequest) {
        if (newGameRequest == null) throw new IllegalArgumentException("Запрос не должен быть null");
        validateHeight(newGameRequest);
        validateWidth(newGameRequest);
        validateMinesCount(newGameRequest);
    }

    private void validateMinesCount(@NotNull NewGameRequest newGameRequest) {
        var validMinMinesCount = config.minMinesCount();

        var height = newGameRequest.getHeight();
        var width = newGameRequest.getWidth();
        int validMinesCount = width * height - 1;

        var minesCount = newGameRequest.getMinesCount();

        if(minesCount < validMinMinesCount || minesCount > validMinesCount) {
            throw new IllegalArgumentException(String.format("Количество мин должно быть в диапазоне от %d, до %d. Текущее количество: %d", validMinMinesCount, validMinesCount, minesCount));
        }
    }

    private void validateHeight(@NotNull NewGameRequest newGameRequest) {
        var validMinHeight = config.minHeight();
        var validMaxHeight = config.maxHeight();

        var height = newGameRequest.getHeight();
        var heightErrorMassage = String.format("Высота должна быть в диапазоне от %d, до %d. Текущая высота: %d", validMinHeight, validMaxHeight, height);

        validateSize(height, validMinHeight, validMaxHeight, heightErrorMassage);
    }

    private void validateWidth(@NotNull NewGameRequest newGameRequest) {
        var validMinWidth = config.minWidth();
        var validMaxWidth = config.maxWidth();

        var width = newGameRequest.getWidth();
        var heightErrorMassage = String.format("Ширина должна быть в диапазоне от %d, до %d. Текущая ширина: %d", validMinWidth, validMaxWidth, width);

        validateSize(width, validMinWidth, validMaxWidth, heightErrorMassage);
    }

    /**
     * Валидация размеров
     *
     * @param current текущий размер
     * @param min минимальный допустимый размер
     * @param max максимальный допустимый размер
     * @param errorMassage Сообщение об ошибке для пользователя
     */
    private void validateSize(int current, int min, int max, String errorMassage) {
        if(current < min || current > max) {
            throw new IllegalArgumentException(errorMassage);
        }
    }

}

package ru.catr.game.sapper.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.config.prop.ValidateFieldConfig;

/**
 * Общий валидатор для основной логики игры, без привязки к типам данных (внутренние/внешние объекты)
 */
@Component
@AllArgsConstructor
public class CommonValidator {

    private final ValidateFieldConfig config;

    public void validateMinesCount(int height, int width, int minesCount) {
        var validMinMinesCount = config.minMinesCount();
        int validMaxMinesCount = width * height - 1;

        if (minesCount < validMinMinesCount || minesCount > validMaxMinesCount) {
            throw new IllegalArgumentException(String.format("Количество мин должно быть в диапазоне от %d, до %d. Текущее количество: %d", validMinMinesCount, validMaxMinesCount, minesCount));
        }
    }

    public void validateHeight(int height) {
        var validMinHeight = config.minHeight();
        var validMaxHeight = config.maxHeight();

        var heightErrorMassage = String.format("Высота должна быть в диапазоне от %d, до %d. Текущая высота: %d", validMinHeight, validMaxHeight, height);

        validateSize(height, validMinHeight, validMaxHeight, heightErrorMassage);
    }

    public void validateWidth(int width) {
        var validMinWidth = config.minWidth();
        var validMaxWidth = config.maxWidth();

        var heightErrorMassage = String.format("Ширина должна быть в диапазоне от %d, до %d. Текущая ширина: %d", validMinWidth, validMaxWidth, width);

        validateSize(width, validMinWidth, validMaxWidth, heightErrorMassage);
    }

    /**
     * Валидация размеров
     *
     * @param current      текущий размер
     * @param min          минимальный допустимый размер
     * @param max          максимальный допустимый размер
     * @param errorMassage Сообщение об ошибке для пользователя
     */
    private void validateSize(int current, int min, int max, String errorMassage) {
        if (current < min || current > max) {
            throw new IllegalArgumentException(errorMassage);
        }
    }

}

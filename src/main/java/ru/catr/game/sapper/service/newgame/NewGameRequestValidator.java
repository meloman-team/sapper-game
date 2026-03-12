package ru.catr.game.sapper.service.newgame;

import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.NewGameRequest;

@Component
public class NewGameRequestValidator {

    /**
     * Каждая игра начинается с вызова метода POST /new с указанием размера игрового поля width и height, а также количества мин mines_count на нём.
     * Для тестовой реализации ограничиваемся разумными размерами игрового поля:
     * ширина и высота не менее 2 и не более 50, количество мин не более width * height - 1 (всегда должна быть хотя бы одна свободная ячейка).
     */
    public static void validateRequest(NewGameRequest newGameRequest) {
        validateHeight(newGameRequest);
        validateWidth(newGameRequest);
        validateMinesCount(newGameRequest);
    }

    private static void validateMinesCount(NewGameRequest newGameRequest) {
        int validMinMinesCount = 0; // TODO вынести константы валидаций в конфигурацию или енам

        var height = newGameRequest.getHeight();
        var width = newGameRequest.getWidth();
        int validMinesCount = width * height - 1;

        var minesCount = newGameRequest.getMinesCount();

        // TODO Уточнить требования по минимальному количеству мин.
        if(minesCount < validMinMinesCount && minesCount > validMinesCount) {
            throw new IllegalArgumentException(String.format("Количество мин должно быть в диапазоне от %d, до %d. Текущее количество: %d", validMinMinesCount, validMinesCount, minesCount));
        }
    }

    private static void validateHeight(NewGameRequest newGameRequest) {
        int validMinHeight = 2; // TODO вынести константы валидаций в конфигурацию или енам
        int validMaxHeight = 50;

        var height = newGameRequest.getHeight();
        var heightErrorMassage = String.format("Высота должна быть в диапазоне от %d, до %d. Текущая высота: %d", validMinHeight, validMaxHeight, height);

        validateSize(height, validMinHeight, validMaxHeight, heightErrorMassage);
    }

    private static void validateWidth(NewGameRequest newGameRequest) {
        int validMinWidth = 2; // TODO вынести константы валидаций в конфигурацию или енам
        int validMaxWidth = 50;

        var width = newGameRequest.getWidth();
        var heightErrorMassage = String.format("Ширина должна быть в диапазоне от %d, до %d. Текущая ширина: %d", validMinWidth, validMaxWidth, width);

        validateSize(width, validMinWidth, validMaxWidth, heightErrorMassage);
    }

    /**
     * @param current текущий размер
     * @param min минимальный допустимый размер
     * @param max максимальный допустимый размер
     * @param errorMassage Сообщение об ошибке для пользователя
     */
    private static void validateSize(int current, int min, int max, String errorMassage) {
        if(current < min || current > max) {
            throw new IllegalArgumentException(errorMassage);
        }
    }

}

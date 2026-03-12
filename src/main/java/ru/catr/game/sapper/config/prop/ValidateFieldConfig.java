package ru.catr.game.sapper.config.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфиг валидации игрового поля
 *
 * @param minMinesCount минимальное количество мин
 * @param minHeight минимальная высота поля
 * @param maxHeight максимальная высота поля
 * @param minWidth минимальная ширина поля
 * @param maxWidth максимальная ширина поля
 */
@ConfigurationProperties(prefix = "game.validate")
public record ValidateFieldConfig(
        int minMinesCount,
        int minHeight,
        int maxHeight,
        int minWidth,
        int maxWidth
) {}

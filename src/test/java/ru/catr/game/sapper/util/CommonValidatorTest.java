package ru.catr.game.sapper.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.catr.game.sapper.config.prop.ValidateFieldConfig;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

/**
 * Тест на {@link CommonValidator}
 */
@ExtendWith(MockitoExtension.class)
class CommonValidatorTest {

    private final static int VALID_HEIGHT = 5;
    private final static int VALID_WIDTH = 5;
    private final static int VALID_MINES_COUNT = 5;

    @Mock
    private ValidateFieldConfig config;

    private CommonValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CommonValidator(config);

        lenient().when(config.minHeight()).thenReturn(2);
        lenient().when(config.maxHeight()).thenReturn(50);
        lenient().when(config.minWidth()).thenReturn(2);
        lenient().when(config.maxWidth()).thenReturn(50);
        lenient().when(config.minMinesCount()).thenReturn(1);
    }

    @Test
    void validHeight() {
        validator.validateHeight(VALID_HEIGHT);

        verify(config, atLeastOnce()).minHeight();
        verify(config, atLeastOnce()).maxHeight();
    }

    @Test
    void validWidth() {
        validator.validateWidth(VALID_WIDTH);

        verify(config, atLeastOnce()).minWidth();
        verify(config, atLeastOnce()).maxWidth();
    }

    @Test
    void validMinesCount() {
        validator.validateMinesCount(VALID_HEIGHT, VALID_WIDTH, VALID_MINES_COUNT);

        verify(config, atLeastOnce()).minMinesCount();
    }

    @Test
    void invalidMinHeight() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateHeight(-1);
        });

        assertTrue(ex.getMessage().contains("высота"));
    }

    @Test
    void invalidMaxHeight() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateHeight(100);
        });

        assertTrue(ex.getMessage().contains("высота"));
    }

    @Test
    void invalidMinWidth() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateWidth(-1);
        });

        assertTrue(ex.getMessage().contains("ширина"));
    }

    @Test
    void invalidMaxWidth() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateWidth(100);
        });

        assertTrue(ex.getMessage().contains("ширина"));
    }

    @Test
    void invalidMinMinesCount() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateMinesCount(VALID_HEIGHT, VALID_WIDTH, 0);
        });

        assertTrue(ex.getMessage().contains("Количество мин"));
    }

    @Test
    void invalidMaxMinesCount() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateMinesCount(VALID_HEIGHT, VALID_WIDTH, 25);
        });

        assertTrue(ex.getMessage().contains("Количество мин"));
    }

}
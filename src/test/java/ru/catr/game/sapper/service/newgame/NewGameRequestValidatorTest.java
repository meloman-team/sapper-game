package ru.catr.game.sapper.service.newgame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.catr.game.sapper.config.prop.ValidateFieldConfig;
import ru.catr.game.sapper.model.NewGameRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тест на {@link NewGameRequestValidator}
 */
@ExtendWith(MockitoExtension.class)
class NewGameRequestValidatorTest {

    @Mock
    private ValidateFieldConfig config;
    @Mock
    private NewGameRequest request;

    private NewGameRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NewGameRequestValidator(config);

        lenient().when(config.minHeight()).thenReturn(2);
        lenient().when(config.maxHeight()).thenReturn(50);
        lenient().when(config.minWidth()).thenReturn(2);
        lenient().when(config.maxWidth()).thenReturn(50);
        lenient().when(config.minMinesCount()).thenReturn(0);

        lenient().when(request.getHeight()).thenReturn(5);
        lenient().when(request.getWidth()).thenReturn(5);
        lenient().when(request.getMinesCount()).thenReturn(5);
    }

    @Test
    void valid() {

        validator.validateRequest(request);

        verify(config, atLeastOnce()).minHeight();
        verify(config, atLeastOnce()).maxHeight();
        verify(config, atLeastOnce()).minWidth();
        verify(config, atLeastOnce()).maxWidth();
        verify(config, atLeastOnce()).minMinesCount();

        verify(request, atLeastOnce()).getHeight();
        verify(request, atLeastOnce()).getWidth();
        verify(request, atLeastOnce()).getMinesCount();
    }

    @Test
    void invalidMinHeight() {
        when(request.getHeight()).thenReturn(-1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateRequest(request);
        });

        assertTrue(ex.getMessage().contains("высота"));
    }

    @Test
    void invalidMaxHeight() {
        when(request.getHeight()).thenReturn(100);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateRequest(request);
        });

        assertTrue(ex.getMessage().contains("высота"));
    }

    @Test
    void invalidMinWidth() {
        when(request.getWidth()).thenReturn(-1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateRequest(request);
        });

        assertTrue(ex.getMessage().contains("ширина"));
    }

    @Test
    void invalidMaxWidth() {
        when(request.getWidth()).thenReturn(100);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateRequest(request);
        });

        assertTrue(ex.getMessage().contains("ширина"));
    }

    @Test
    void invalidMinMinesCount() {
        when(request.getMinesCount()).thenReturn(-1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateRequest(request);
        });

        assertTrue(ex.getMessage().contains("Количество мин"));
    }

    @Test
    void invalidMaxMinesCount() {
        when(request.getMinesCount()).thenReturn(25);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateRequest(request);
        });

        assertTrue(ex.getMessage().contains("Количество мин"));
    }

}
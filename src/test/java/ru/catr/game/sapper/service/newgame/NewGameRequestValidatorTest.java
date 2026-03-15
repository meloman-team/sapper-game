package ru.catr.game.sapper.service.newgame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.catr.game.sapper.model.NewGameRequest;
import ru.catr.game.sapper.util.CommonValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Тест на {@link NewGameRequestValidator}
 */
@ExtendWith(MockitoExtension.class)
class NewGameRequestValidatorTest {

    @Mock
    private CommonValidator validator;
    @Mock
    private NewGameRequest request;

    @InjectMocks
    private NewGameRequestValidator newGameRequestValidator;

    @Test
    void requestIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> newGameRequestValidator.validateRequest(null)
        );

        assertEquals("Запрос не должен быть null", exception.getMessage());
        verifyNoInteractions(validator);
    }

    @Test
    void requestIsValid() {
        int height = 10;
        int width = 15;
        int mines = 20;

        when(request.getHeight()).thenReturn(height);
        when(request.getWidth()).thenReturn(width);
        when(request.getMinesCount()).thenReturn(mines);

        newGameRequestValidator.validateRequest(request);

        verify(validator).validateHeight(height);
        verify(validator).validateWidth(width);
        verify(validator).validateMinesCount(height, width, mines);
        verifyNoMoreInteractions(validator);
    }

}
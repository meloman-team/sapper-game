package ru.catr.game.sapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.catr.game.sapper.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        log.info(ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse();
        return ResponseEntity.badRequest()
                .body(response.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse();
        return ResponseEntity.internalServerError()
                .body(response.error("Неизвестная ошибка сервера"));
    }

}

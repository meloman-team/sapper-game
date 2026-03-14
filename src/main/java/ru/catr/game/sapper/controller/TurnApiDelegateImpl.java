package ru.catr.game.sapper.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.api.TurnApiDelegate;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.GameTurnRequest;
import ru.catr.game.sapper.service.turn.TurnGameService;
import ru.catr.game.sapper.service.turn.TurnRequestValidator;

@Component
@AllArgsConstructor
public class TurnApiDelegateImpl implements TurnApiDelegate {

    private final TurnRequestValidator validator;
    private final TurnGameService service;

    @Override
    public ResponseEntity<GameInfoResponse> turnPost(GameTurnRequest gameTurnRequest) {
        validator.validateRequest(gameTurnRequest); // TODO можно переделать на автоматическую валидацию через аннотации в dto
        GameInfoResponse response = service.turn(gameTurnRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

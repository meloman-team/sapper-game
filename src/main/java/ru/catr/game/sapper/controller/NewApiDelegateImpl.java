package ru.catr.game.sapper.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import ru.catr.game.sapper.api.NewApiDelegate;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.NewGameRequest;
import ru.catr.game.sapper.service.newgame.NewGameRequestValidator;

import java.util.Optional;

@Component
public class NewApiDelegateImpl implements NewApiDelegate {

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return NewApiDelegate.super.getRequest();
    }

    @Override
    public ResponseEntity<GameInfoResponse> newPost(NewGameRequest newGameRequest) {
        NewGameRequestValidator.validateRequest(newGameRequest); // TODO можно переделать на автоматическую валидацию через аннотации в dto
        GameInfoResponse response = createResponse(newGameRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GameInfoResponse createResponse(NewGameRequest newGameRequest) {
        // FIXME
        return null;
    }

}

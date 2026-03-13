package ru.catr.game.sapper.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import ru.catr.game.sapper.api.NewApiDelegate;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.NewGameRequest;
import ru.catr.game.sapper.service.newgame.NewGameRequestValidator;
import ru.catr.game.sapper.service.newgame.NewGameService;

import java.util.Optional;

@Component
@AllArgsConstructor
public class NewApiDelegateImpl implements NewApiDelegate {

    private NewGameRequestValidator validator;
    private NewGameService service;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return NewApiDelegate.super.getRequest();
    }

    @Override
    public ResponseEntity<GameInfoResponse> newPost(NewGameRequest newGameRequest) {
        validator.validateRequest(newGameRequest); // TODO можно переделать на автоматическую валидацию через аннотации в dto
        GameInfoResponse response = service.createFieldAndResponse(newGameRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

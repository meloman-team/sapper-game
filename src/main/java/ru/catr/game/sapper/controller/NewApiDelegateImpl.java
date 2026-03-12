package ru.catr.game.sapper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import ru.catr.game.sapper.api.NewApiDelegate;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.NewGameRequest;

import java.util.Optional;

@Component
public class NewApiDelegateImpl implements NewApiDelegate {

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return NewApiDelegate.super.getRequest();
    }

    @Override
    public ResponseEntity<GameInfoResponse> newPost(NewGameRequest newGameRequest) {
        return NewApiDelegate.super.newPost(newGameRequest);
    }

}

package ru.catr.game.sapper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import ru.catr.game.sapper.api.TurnApiDelegate;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.GameTurnRequest;

import java.util.Optional;

@Component
public class TurnApiDelegateImpl implements TurnApiDelegate {

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return TurnApiDelegate.super.getRequest();
    }

    @Override
    public ResponseEntity<GameInfoResponse> turnPost(GameTurnRequest gameTurnRequest) {
        return TurnApiDelegate.super.turnPost(gameTurnRequest);
    }
}

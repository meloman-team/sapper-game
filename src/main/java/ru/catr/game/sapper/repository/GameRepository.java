package ru.catr.game.sapper.repository;

import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.CellState;

import java.util.List;
import java.util.UUID;

@Component
public class GameRepository {

    public void save(UUID gameId, List<List<CellState>> internalStateField) {
        //FIXME
    }
}

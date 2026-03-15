package ru.catr.game.sapper.repository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.GameInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class GameRepository {

    // TODO подумать над ограничением количества игр
    private final Map<UUID, GameInfo> gameStorage = new ConcurrentHashMap<>();
    private final GameInfoValidator validator;

    public void save(@NotNull GameInfo gameInfo) {
        validator.validateGameInfo(gameInfo);
        gameStorage.put(gameInfo.getGameId(), new GameInfo(gameInfo));
    }

    @Nullable
    public GameInfo findById(@NotNull UUID gameId) {
        validator.validateGameId(gameId);
        return new GameInfo(gameStorage.get(gameId));
    }

    public void deleteById(@NotNull UUID gameId) {
        validator.validateGameId(gameId);
        gameStorage.remove(gameId);
    }

}

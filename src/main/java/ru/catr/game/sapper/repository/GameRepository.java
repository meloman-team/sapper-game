package ru.catr.game.sapper.repository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.GameInfo;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameRepository {

    // TODO подумать над ограничением количества игр
    private final Map<UUID, GameInfo> gameStorage = new ConcurrentHashMap<>();

    public void save(@NotNull GameInfo gameInfo) {
        validateGameInfo(gameInfo);
        gameStorage.put(gameInfo.getGameId(), new GameInfo(gameInfo));
    }

    @Nullable
    public GameInfo findById(@NotNull UUID gameId) {
        validateGameId(gameId);
        return new GameInfo(gameStorage.get(gameId));
    }

    public void deleteById(@NotNull UUID gameId) {
        validateGameId(gameId);
        gameStorage.remove(gameId);
    }

    private void validateGameId(UUID gameId) {
        Objects.requireNonNull(gameId, "Идентификатор игры не должен быть null");
    }

    private void validateGameInfo(GameInfo gameInfo) {
        Objects.requireNonNull(gameInfo, "Идентификатор игры не должен быть null");
        Objects.requireNonNull(gameInfo.getGameId(), "Идентификатор игры не должен быть null");
        Objects.requireNonNull(gameInfo.getField(), "Игровое поле не должно быть null");
    }

}

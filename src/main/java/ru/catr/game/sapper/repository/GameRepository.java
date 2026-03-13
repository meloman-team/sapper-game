package ru.catr.game.sapper.repository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import ru.catr.game.sapper.model.CellState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameRepository {

    // TODO подумать над ограничением количества игр
    private final Map<UUID, List<List<CellState>>> gameStorage = new ConcurrentHashMap<>();

    public void save(@NotNull UUID gameId, @NotNull List<List<CellState>> internalStateField) {
        validateGameId(gameId);
        validateStateField(internalStateField);

        gameStorage.put(gameId, createDeepCopy(internalStateField));
    }

    @Nullable
    public List<List<CellState>> findById(@NotNull UUID gameId) {
        validateGameId(gameId);
        return createDeepCopy(gameStorage.get(gameId));
    }

    public void deleteById(@NotNull UUID gameId) {
        validateGameId(gameId);
        gameStorage.remove(gameId);
    }

    private void validateGameId(UUID gameId) {
        Objects.requireNonNull(gameId, "Идентификатор игры не должен быть null");
    }

    private void validateStateField(List<List<CellState>> internalStateField) {
        Objects.requireNonNull(internalStateField, "Игровое поле не должно быть null");
    }

    private List<List<CellState>> createDeepCopy(List<List<CellState>> original) {
        if (original == null) {
            return null;
        }

        List<List<CellState>> copy = new ArrayList<>(original.size());
        for (List<CellState> row : original) {
            List<CellState> rowCopy = new ArrayList<>(row.size());
            for (CellState cell : row) {
                rowCopy.add(new CellState(cell));
            }
            copy.add(rowCopy);
        }
        return copy;
    }

}

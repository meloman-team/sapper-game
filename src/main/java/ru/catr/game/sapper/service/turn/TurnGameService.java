package ru.catr.game.sapper.service.turn;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.catr.game.sapper.model.CellState;
import ru.catr.game.sapper.model.GameInfo;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.GameTurnRequest;
import ru.catr.game.sapper.repository.GameRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.catr.game.sapper.model.GameInfoResponse.FieldEnum;

@Slf4j
@Service
@AllArgsConstructor
public class TurnGameService {

    private final GameRepository gameRepository;
    private final TurnRequestValidator validator;

    public GameInfoResponse turn(@NotNull GameTurnRequest request) {
        boolean completed = false;
        boolean loss = false;

        GameInfo gameInfo = gameRepository.findById(request.getGameId());
        validator.validateRequest(request, gameInfo);
        List<List<CellState>> internalStateField = gameInfo.getField();

        boolean hitMine = openCell(internalStateField, request.getRow(), request.getCol());

        if (hitMine) {
            completed = true;
            loss = true;
        } else if (checkWin(internalStateField)) {
            completed = true;
        }

        if (completed) {
            revealAllField(internalStateField);
            gameRepository.deleteById(gameInfo.getGameId());
        } else {
            gameRepository.save(gameInfo);
        }

        return toResponse(gameInfo, completed, loss);
    }

    /**
     * Открывает ячейку, возвращает true, если попала в мину
     */
    private boolean openCell(@NotNull List<List<CellState>> internalStateField, int row, int col) {
        CellState cell = internalStateField.get(row).get(col);

        if(cell.isOpened()) {
            throw new IllegalArgumentException("Нельзя открыть уже открытую ячейку");
        }

        if (cell.isMined()) {
            cell.setOpened(true);
            return true;
        }

        openRecursive(internalStateField, row, col);
        return false;
    }

    /**
     * Рекурсивное открытие для нулевых ячеек
     */
    private void openRecursive(@NotNull List<List<CellState>> field, int row, int col) {
        int height = field.size();
        int width = field.getFirst().size();
        if (row < 0 || row >= height || col < 0 || col >= width) return;

        CellState cell = field.get(row).get(col);
        if (cell.isOpened() || cell.isMined()) return;

        cell.setOpened(true);

        if (cell.getAdjacentMines() == 0) {
            int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
            for (int d = 0; d < 8; d++) {
                openRecursive(field, row + dx[d], col + dy[d]);
            }
        }
    }

    /**
     * Проверка победы: все не заминированные ячейки открыты
     */
    private boolean checkWin(@NotNull List<List<CellState>> field) {
        for (var row : field) {
            for (var cell : row) {
                if (!cell.isMined() && !cell.isOpened()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Показать все поле при завершении игры
     */
    private void revealAllField(@NotNull List<List<CellState>> field) {
        for (var row : field) {
            for (var cell : row) {
                cell.setOpened(true);
            }
        }
    }

    private GameInfoResponse toResponse(@NotNull GameInfo gameInfo, boolean gameCompleted, boolean isLoss) {
        List<List<CellState>> field = gameInfo.getField();
        int height = gameInfo.getHeight();
        int width = gameInfo.getWidth();

        List<List<FieldEnum>> displayField = IntStream.range(0, height)
                .mapToObj(i -> IntStream.range(0, width)
                        .mapToObj(j -> {
                            var cell = field.get(i).get(j);
                            return toFieldEnum(cell, gameCompleted, isLoss);
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        logInternalGameField(field);

        return new GameInfoResponse()
                .gameId(gameInfo.getGameId())
                .width(width)
                .height(height)
                .minesCount(gameInfo.getMinesCount())
                .completed(gameCompleted)
                .field(displayField);
    }

    private FieldEnum toFieldEnum(CellState cell, boolean gameCompleted, boolean isLoss) {
        if (!cell.isOpened()) {
            if (gameCompleted && cell.isMined()) {
                return isLoss ? FieldEnum.X : FieldEnum.M;
            }
            return FieldEnum.SPACE;
        }
        if (cell.isMined()) {
            return isLoss ? FieldEnum.X : FieldEnum.M;
        }
        return switch (cell.getAdjacentMines()) {
            case 0 -> FieldEnum._0;
            case 1 -> FieldEnum._1;
            case 2 -> FieldEnum._2;
            case 3 -> FieldEnum._3;
            case 4 -> FieldEnum._4;
            case 5 -> FieldEnum._5;
            case 6 -> FieldEnum._6;
            case 7 -> FieldEnum._7;
            case 8 -> FieldEnum._8;
            default -> FieldEnum.SPACE;
        };
    }

    private void logInternalGameField(List<List<CellState>> field) {
        for (int i = 0; i < field.size(); i++) {
            String rowString = field.get(i).stream()
                    .map(CellState::toString)
                    .collect(Collectors.joining(", "));

            log.debug("Row {}: [{}]", i, rowString);
        }
    }
}

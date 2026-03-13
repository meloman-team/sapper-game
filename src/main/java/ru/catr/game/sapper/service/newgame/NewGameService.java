package ru.catr.game.sapper.service.newgame;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.catr.game.sapper.model.CellState;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.NewGameRequest;
import ru.catr.game.sapper.repository.GameRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Логика сервиса
 */
@Service
@AllArgsConstructor
public class NewGameService {

    private GameRepository gameRepository;

    /**
     * Создает и сохраняет новое игровое поле, затем формирует ответ для пользователя.
     */
    public GameInfoResponse createFieldAndResponse(NewGameRequest newGameRequest) {
        UUID gameId = UUID.randomUUID();
        List<List<CellState>> internalStateField = generateInternalField(newGameRequest);
        gameRepository.save(gameId, internalStateField);
        return buildResponse(newGameRequest, gameId);
    }

    private GameInfoResponse buildResponse(NewGameRequest request, UUID gameId) {
        var response = new GameInfoResponse();
        response.setGameId(gameId);
        response.setWidth(request.getWidth());
        response.setHeight(request.getHeight());
        response.setMinesCount(request.getMinesCount());
        response.setCompleted(false);
        response.setField(generateEmptyField(request.getWidth(), request.getHeight()));
        return response;
    }

    /**
     * Генерация пустого поля для отображения пользователю
     */
    private List<List<GameInfoResponse.FieldEnum>> generateEmptyField(int width, int height) {
        return IntStream.range(0, height)
                .mapToObj(i -> IntStream.range(0, width)
                        .mapToObj(j -> GameInfoResponse.FieldEnum.SPACE)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Генерация внутреннего состояния поля
     */
    private List<List<CellState>> generateInternalField(NewGameRequest newGameRequest) {
        var height = newGameRequest.getHeight();
        var width = newGameRequest.getWidth();
        var minesCount = newGameRequest.getMinesCount();

        List<List<CellState>> internalState = IntStream.range(0, height)
                .mapToObj(i -> IntStream.range(0, width)
                        .mapToObj(j -> new CellState())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        placeMines(internalState, width, height, minesCount);
        calculateAdjacentMines(internalState, width, height);

        return internalState;
    }

    /**
     * Случайное размещение мин на поле
     */
    private void placeMines(List<List<CellState>> field, int width, int height, int minesCount) {
        int totalCells = width * height;

        // Если мин больше половины — выгоднее выбрать безопасные ячейки
        if (minesCount > totalCells / 2) {
            Set<Integer> safeIdx = new HashSet<>();
            int safeCount = totalCells - minesCount;
            ThreadLocalRandom random = ThreadLocalRandom.current();

            while (safeIdx.size() < safeCount) {
                safeIdx.add(random.nextInt(totalCells));
            }

            // Все ячейки — мины, кроме safeIdx
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int index = i * width + j;
                    if (!safeIdx.contains(index)) {
                        field.get(i).get(j).setHasMine(true);
                    }
                }
            }
        } else {
            Set<Integer> mineIdx = new HashSet<>();
            ThreadLocalRandom random = ThreadLocalRandom.current();

            while (mineIdx.size() < minesCount) {
                mineIdx.add(random.nextInt(totalCells));
            }

            for (int index : mineIdx) {
                int row = index / width;
                int col = index % width;
                field.get(row).get(col).setHasMine(true);
            }
        }
    }

    /**
     * Подсчитывает количество мин в соседних ячейках (8 направлений).
     */
    private void calculateAdjacentMines(List<List<CellState>> field, int width, int height) {
        // Смещения для 8 соседей: ↖ ↑ ↗ ← → ↙ ↓ ↘
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                CellState cell = field.get(i).get(j);
                if (cell.isHasMine()) continue;

                int count = 0;
                for (int d = 0; d < 8; d++) {
                    int ni = i + dx[d];
                    int nj = j + dy[d];
                    if (ni >= 0 && ni < height && nj >= 0 && nj < width) {
                        if (field.get(ni).get(nj).isHasMine()) {
                            count++;
                        }
                    }
                }
                cell.setAdjacentMines(count);
            }
        }
    }

}
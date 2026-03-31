package ru.catr.game.sapper.service.newgame;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;
import ru.catr.game.sapper.model.CellState;
import ru.catr.game.sapper.model.GameInfo;
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
 * Сервис отвечает за создание новой игры и генерацию игрового поля
 */
@Service
@AllArgsConstructor
public class NewGameService {

    private final GameRepository gameRepository;

    /**
     * Создает и сохраняет новое игровое поле, затем формирует ответ для пользователя.
     */
    public GameInfoResponse createFieldAndResponse(@NotNull NewGameRequest request) {
        UUID gameId = UUID.randomUUID();
        List<List<CellState>> internalStateField = generateInternalField(request);
        GameInfo gameInfo = new GameInfo(gameId, request.getWidth(), request.getHeight(), request.getMinesCount(), internalStateField);
        gameRepository.save(gameInfo);
        return buildResponse(request, gameId);
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
        FillContext fillContext = mapToFillContext(newGameRequest);
        List<List<CellState>> internalState = placeMines(fillContext);
        calculateAdjacentMines(internalState, fillContext.width, fillContext.height);

        return internalState;
    }

    /**
     * Заполняет поле минами, либо пустыми значениями в зависимости от стратегии
     */
    private List<List<CellState>> initField(FillContext fillContext) {
        return IntStream.range(0, fillContext.height)
                .mapToObj(i -> IntStream.range(0, fillContext.width)
                        .mapToObj(j -> {
                            boolean mined = switch (fillContext.strategy) {
                                case Strategy.MINING -> false;
                                case Strategy.DEMINING -> true;
                            };
                            return CellState.builder().mined(mined).build();
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private FillContext mapToFillContext(NewGameRequest request) {
        var height = request.getHeight();
        var width = request.getWidth();
        var minesCount = request.getMinesCount();
        var totalCells = width * height;
        return FillContext.builder()
                .height(height)
                .width(width)
                .minesCount(minesCount)
                .totalCells(totalCells)
                .strategy(getStrategy(request.getMinesCount(), totalCells))
                .build();
    }

    /**
     * Выбираем стратегию заполнения поля в зависимости от количества мин и размера поля.
     * Если мин больше половины — выгоднее выбрать заполнение всего поля минами и генерировать пустые ячейки
     */
    private Strategy getStrategy(int minesCount, int totalCells) {
        return minesCount > totalCells / 2 ? Strategy.DEMINING : Strategy.MINING;
    }

    /**
     * Случайное размещение мин на поле
     */
    private List<List<CellState>> placeMines(FillContext fillContext) {
        List<List<CellState>> field = initField(fillContext);
        switch (fillContext.strategy) {
            case DEMINING -> {
                int safeCount = fillContext.totalCells - fillContext.minesCount;
                fillingField(field, fillContext.width, safeCount, fillContext.totalCells, false);
            }
            case MINING -> fillingField(field, fillContext.width, fillContext.minesCount, fillContext.totalCells, true);
        }
        return field;
    }

    /**
     * Случайное размещение мин на поле
     *
     * @param field      - игровое поле которое нужно заполнить
     * @param width      - ширина поля
     * @param cellCount  - количество ячеек для заполнения
     * @param totalCells - общее количество ячеек на поле
     * @param mined      true - заполняем минами, false - заполняем пустыми ячейками
     */
    private static void fillingField(List<List<CellState>> field, int width, int cellCount, int totalCells, boolean mined) {
        Set<Integer> fillIdx = new HashSet<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        while (fillIdx.size() < cellCount) {
            fillIdx.add(random.nextInt(totalCells));
        }

        for (int index : fillIdx) {
            int row = index / width;
            int col = index % width;
            field.get(row).get(col).setMined(mined);
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
                if (cell.isMined()) continue;

                int count = 0;
                for (int d = 0; d < 8; d++) {
                    int ni = i + dx[d];
                    int nj = j + dy[d];
                    if (ni >= 0 && ni < height && nj >= 0 && nj < width) {
                        if (field.get(ni).get(nj).isMined()) {
                            count++;
                        }
                    }
                }
                cell.setAdjacentMines(count);
            }
        }
    }

    /**
     * Контекст для заполнения поля, пред расчётные значения и выбранная стратегия.
     */
    @Builder
    static class FillContext {
        int height;
        int width;
        int minesCount;
        int totalCells;
        Strategy strategy;
    }

    enum Strategy {
        MINING, // минируем (генерируем мины)
        DEMINING // разминируем (генерируем пустые ячейки)
    }

}
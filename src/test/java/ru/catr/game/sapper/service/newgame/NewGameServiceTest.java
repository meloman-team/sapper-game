package ru.catr.game.sapper.service.newgame;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.catr.game.sapper.model.CellState;
import ru.catr.game.sapper.model.GameInfo;
import ru.catr.game.sapper.model.GameInfoResponse;
import ru.catr.game.sapper.model.NewGameRequest;
import ru.catr.game.sapper.repository.GameRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Тест на {@link NewGameService}
 */
@ExtendWith(MockitoExtension.class)
class NewGameServiceTest {
    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private NewGameService newGameService;

    @Nested
    @DisplayName("createFieldAndResponse() — успешное создание игры")
    class CreateGameSuccess {

        @Test
        @DisplayName("Создание игры 10×10 с 15 минами")
        void shouldCreateGame_10x10_15mines() {
            // Given
            var request = new NewGameRequest();
            request.setWidth(10);
            request.setHeight(10);
            request.setMinesCount(15);

            // When
            var response = newGameService.createFieldAndResponse(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getGameId()).isNotNull();
            assertThat(response.getWidth()).isEqualTo(10);
            assertThat(response.getHeight()).isEqualTo(10);
            assertThat(response.getMinesCount()).isEqualTo(15);
            assertThat(response.getCompleted()).isFalse();
            assertThat(response.getField()).hasSize(10);
            assertThat(response.getField().get(0)).hasSize(10);

            // Проверка взаимодействия с репозиторием
            verify(gameRepository).save(any());
        }

        @Test
        @DisplayName("Каждая новая игра получает уникальный gameId")
        void shouldGenerateUniqueGameId() {
            var request = new NewGameRequest();
            request.setWidth(5);
            request.setHeight(5);
            request.setMinesCount(5);

            var response1 = newGameService.createFieldAndResponse(request);
            var response2 = newGameService.createFieldAndResponse(request);

            assertThat(response1.getGameId()).isNotEqualTo(response2.getGameId());
        }

        @Test
        @DisplayName("Сохранение внутреннего состояния в репозиторий")
        void shouldSaveInternalStateToRepository() {
            var request = new NewGameRequest();
            request.setWidth(3);
            request.setHeight(3);
            request.setMinesCount(2);

            // Captor для перехвата аргумента save()
            var stateCaptor = ArgumentCaptor.forClass(GameInfo.class);

            newGameService.createFieldAndResponse(request);

            verify(gameRepository).save(stateCaptor.capture());

            var savedState = stateCaptor.getValue();
            assertThat(savedState).isInstanceOf(GameInfo.class);
            assertThat((List<?>) savedState.getField()).hasSize(3); // height
            assertThat((List<?>) savedState.getField().get(0)).hasSize(3); // width
        }
    }

    @Nested
    @DisplayName("Граничные значения параметров")
    class BoundaryValues {

        @ParameterizedTest
        @CsvSource({
                "2, 2, 1",      // минимальное поле, 1 мина
                "2, 2, 3",      // минимальное поле, макс. мин (2*2-1)
                "50, 50, 1",    // максимальное поле, 1 мина
                "50, 50, 2499", // максимальное поле, макс. мин (50*50-1)
                "10, 20, 50",   // прямоугольное поле
                "20, 10, 100"   // прямоугольное поле (обратное)
        })
        @DisplayName("Создание игры с допустимыми параметрами: {0}×{1}, мин: {2}")
        void shouldCreateGame_ValidParams(int width, int height, int mines) {
            var request = new NewGameRequest();
            request.setWidth(width);
            request.setHeight(height);
            request.setMinesCount(mines);

            var response = newGameService.createFieldAndResponse(request);

            assertThat(response.getGameId()).isNotNull();
            assertThat(response.getWidth()).isEqualTo(width);
            assertThat(response.getHeight()).isEqualTo(height);
            assertThat(response.getMinesCount()).isEqualTo(mines);
            assertThat(response.getField()).hasSize(height);
            assertThat(response.getField().get(0)).hasSize(width);
        }

        @Test
        @DisplayName("Поле заполнено только символом SPACE при создании")
        void shouldReturnOnlySpaceSymbols() {
            var request = new NewGameRequest();
            request.setWidth(5);
            request.setHeight(5);
            request.setMinesCount(10);

            var response = newGameService.createFieldAndResponse(request);

            var allCells = response.getField().stream()
                    .flatMap(List::stream)
                    .toList();

            assertThat(allCells).allMatch(cell -> cell == GameInfoResponse.FieldEnum.SPACE);
        }
    }

    @Nested
    @DisplayName("Логика размещения мин (косвенная проверка)")
    class MinesPlacement {

        @Test
        @DisplayName("Мины размещаются внутри границ поля")
        void shouldPlaceMines_WithinBounds() {
            var request = new NewGameRequest();
            request.setWidth(10);
            request.setHeight(10);
            request.setMinesCount(20);

            // Перехватываем сохранённое состояние
            var stateCaptor = ArgumentCaptor.forClass(GameInfo.class);
            newGameService.createFieldAndResponse(request);
            verify(gameRepository).save(stateCaptor.capture());

            var internalState = stateCaptor.getValue().getField();

            // Проверяем, что мины только в пределах поля
            for (int i = 0; i < internalState.size(); i++) {
                for (int j = 0; j < internalState.get(i).size(); j++) {
                    var cell = internalState.get(i).get(j);
                    // Если ячейка — мина, проверяем, что она валидна
                    if (cell.isHasMine()) {
                        assertThat(i).isBetween(0, 9);
                        assertThat(j).isBetween(0, 9);
                    }
                }
            }
        }

        @Test
        @DisplayName("Количество размещённых мин соответствует запрошенному")
        void shouldPlaceExactMinesCount() {
            var request = new NewGameRequest();
            request.setWidth(8);
            request.setHeight(8);
            request.setMinesCount(13);

            var stateCaptor = ArgumentCaptor.forClass(GameInfo.class);
            newGameService.createFieldAndResponse(request);
            verify(gameRepository).save(stateCaptor.capture());

            var internalState = stateCaptor.getValue().getField();

            long actualMines = internalState.stream()
                    .flatMap(List::stream)
                    .filter(CellState::isHasMine)
                    .count();

            assertThat(actualMines).isEqualTo(13);
        }

        @Test
        @DisplayName("Подсчёт мин вокруг ячеек работает корректно")
        void shouldCalculateAdjacentMines_Correctly() {
            var request = new NewGameRequest();
            request.setWidth(3);
            request.setHeight(3);
            request.setMinesCount(1);

            var stateCaptor = ArgumentCaptor.forClass(GameInfo.class);
            newGameService.createFieldAndResponse(request);
            verify(gameRepository).save(stateCaptor.capture());

            var internalState = stateCaptor.getValue().getField();

            // Находим мину и проверяем соседей
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    var cell = internalState.get(i).get(j);
                    if (cell.isHasMine()) {
                        // Мина должна иметь adjacentMines = 0 (не считается для себя)
                        assertThat(cell.getAdjacentMines()).isEqualTo(0);
                    } else {
                        // У соседей мины adjacentMines >= 1
                        if (isAdjacentToMine(internalState, i, j)) {
                            assertThat(cell.getAdjacentMines()).isGreaterThan(0);
                        }
                    }
                }
            }
        }

        // Вспомогательный метод для теста
        private boolean isAdjacentToMine(List<List<CellState>> field, int row, int col) {
            int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
            for (int d = 0; d < 8; d++) {
                int ni = row + dx[d], nj = col + dy[d];
                if (ni >= 0 && ni < field.size() && nj >= 0 && nj < field.get(0).size()) {
                    if (field.get(ni).get(nj).isHasMine()) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Nested
    @DisplayName("Оптимизация placeMines для большого количества мин")
    class DenseMinesOptimization {

        @Test
        @DisplayName("Корректное размещение при 90% заполнении")
        void shouldPlaceMines_Dense_90percent() {
            var request = new NewGameRequest();
            request.setWidth(10);
            request.setHeight(10);
            request.setMinesCount(90); // 90% поля

            var stateCaptor = ArgumentCaptor.forClass(GameInfo.class);
            newGameService.createFieldAndResponse(request);
            verify(gameRepository).save(stateCaptor.capture());

            var internalState = stateCaptor.getValue().getField();

            long mines = internalState.stream()
                    .flatMap(List::stream)
                    .filter(CellState::isHasMine)
                    .count();

            assertThat(mines).isEqualTo(90);
        }

        @Test
        @DisplayName("Корректное размещение при макс. количестве мин")
        void shouldPlaceMines_MaxAllowed() {
            var request = new NewGameRequest();
            request.setWidth(5);
            request.setHeight(5);
            request.setMinesCount(24); // 5*5 - 1

            var stateCaptor = ArgumentCaptor.forClass(GameInfo.class);
            newGameService.createFieldAndResponse(request);
            verify(gameRepository).save(stateCaptor.capture());

            var internalState = stateCaptor.getValue().getField();

            long mines = internalState.stream()
                    .flatMap(List::stream)
                    .filter(CellState::isHasMine)
                    .count();
            long safe = internalState.stream()
                    .flatMap(List::stream)
                    .filter(cell -> !cell.isHasMine())
                    .count();

            assertThat(mines).isEqualTo(24);
            assertThat(safe).isEqualTo(1); // хотя бы одна безопасная ячейка
        }
    }
}
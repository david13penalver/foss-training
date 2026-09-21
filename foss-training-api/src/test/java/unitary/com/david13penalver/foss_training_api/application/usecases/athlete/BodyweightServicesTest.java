package unitary.com.david13penalver.foss_training_api.application.usecases.athlete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.athlete.impl.DeleteBodyweightService;
import com.david13penalver.foss_training_api.application.usecases.athlete.impl.GetBodyweightHistoryService;
import com.david13penalver.foss_training_api.application.usecases.athlete.impl.GetLatestBodyweightService;
import com.david13penalver.foss_training_api.application.usecases.athlete.impl.LogBodyweightService;
import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.ports.out.athlete.BodyweightRepository;

class BodyweightServicesTest {

    private FakeBodyweightRepository repository;
    private LogBodyweightService logService;
    private GetBodyweightHistoryService historyService;
    private GetLatestBodyweightService latestService;
    private DeleteBodyweightService deleteService;

    @BeforeEach
    void setUp() {
        repository = new FakeBodyweightRepository();
        logService = new LogBodyweightService(repository);
        historyService = new GetBodyweightHistoryService(repository);
        latestService = new GetLatestBodyweightService(repository);
        deleteService = new DeleteBodyweightService(repository);
    }

    @Test
    void logBodyweight_savesAndReturnsEntry() {
        BodyweightEntry entry = BodyweightEntry.builder()
                .entryDate(LocalDate.of(2026, 9, 21))
                .weightKg(80.5)
                .bodyFatPercentage(14.0)
                .notes("Morning")
                .build();

        BodyweightEntry saved = logService.execute(entry);

        assertNotNull(saved.getId());
        assertEquals(80.5, saved.getWeightKg());
        assertEquals(1, historyService.execute().size());
    }

    @Test
    void logBodyweight_nullDateOrInvalidWeight_throwsException() {
        BodyweightEntry noDate = BodyweightEntry.builder().weightKg(80.0).build();
        assertThrows(IllegalArgumentException.class, () -> logService.execute(noDate));

        BodyweightEntry negWeight = BodyweightEntry.builder().entryDate(LocalDate.now()).weightKg(-5.0).build();
        assertThrows(IllegalArgumentException.class, () -> logService.execute(negWeight));
    }

    @Test
    void getLatestBodyweight_returnsMostRecentDate() {
        logService.execute(BodyweightEntry.builder().entryDate(LocalDate.of(2026, 9, 10)).weightKg(82.0).build());
        logService.execute(BodyweightEntry.builder().entryDate(LocalDate.of(2026, 9, 20)).weightKg(81.0).build());
        logService.execute(BodyweightEntry.builder().entryDate(LocalDate.of(2026, 9, 15)).weightKg(81.5).build());

        Optional<BodyweightEntry> latest = latestService.execute();
        assertTrue(latest.isPresent());
        assertEquals(LocalDate.of(2026, 9, 20), latest.get().getEntryDate());
        assertEquals(81.0, latest.get().getWeightKg());
    }

    @Test
    void deleteBodyweight_removesEntry() {
        BodyweightEntry saved = logService.execute(BodyweightEntry.builder().entryDate(LocalDate.now()).weightKg(80.0).build());
        assertTrue(repository.existsById(saved.getId()));

        deleteService.execute(saved.getId());
        assertFalse(repository.existsById(saved.getId()));
        assertEquals(0, historyService.execute().size());
    }

    private static class FakeBodyweightRepository implements BodyweightRepository {
        private final List<BodyweightEntry> entries = new ArrayList<>();
        private int nextId = 1;

        @Override
        public BodyweightEntry save(BodyweightEntry entry) {
            if (entry.getId() == null) {
                entry.setId(nextId++);
            } else {
                entries.removeIf(e -> e.getId().equals(entry.getId()));
            }
            entries.add(entry);
            return entry;
        }

        @Override
        public List<BodyweightEntry> findAll() {
            return new ArrayList<>(entries);
        }

        @Override
        public Optional<BodyweightEntry> findById(Integer id) {
            return entries.stream().filter(e -> e.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<BodyweightEntry> findLatest() {
            return entries.stream()
                    .max(Comparator.comparing(BodyweightEntry::getEntryDate));
        }

        @Override
        public void deleteById(Integer id) {
            entries.removeIf(e -> e.getId().equals(id));
        }

        @Override
        public boolean existsById(Integer id) {
            return entries.stream().anyMatch(e -> e.getId().equals(id));
        }
    }
}

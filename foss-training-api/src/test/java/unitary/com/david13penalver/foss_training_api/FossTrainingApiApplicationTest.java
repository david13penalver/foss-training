package unitary.com.david13penalver.foss_training_api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import org.junit.jupiter.api.Test;

class FossTrainingApiApplicationTest {

    @Test
    void main_bootsSpringApplicationAndReturns() throws Exception {
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Thread mainThread = new Thread(() -> {
            try {
                FossTrainingApiApplication.main(new String[]{
                        "--spring.main.web-application-type=none",
                        "--spring.main.banner-mode=off",
                        "--logging.level.root=WARN"
                });
            } catch (Throwable t) {
                failure.set(t);
            }
        });
        mainThread.setDaemon(true);
        mainThread.start();
        mainThread.join(60_000);

        assertFalse(mainThread.isAlive(), "main() should return after the context starts");
        assertNull(failure.get(), () -> "main() failed: " + failure.get());
    }
}
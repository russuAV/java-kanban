package app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ManagersTest {

    @Test
    public void checkThatGetDefaultNotNull() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    public void checkThatGetDefaultHistoryNotNull() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}
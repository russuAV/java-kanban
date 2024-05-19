package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    public void checkThatEpicsHaveDifferentId() {
        Epic epic1 = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");

        Assertions.assertNotEquals(epic1.getId(), epic2.getId());
    }

    @Test
    public void checkThatEpicsNotEquals() {
        Epic epic1 = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");

        Assertions.assertNotEquals(epic1, epic2);
    }
}
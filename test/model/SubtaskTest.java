package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    @Test
    public void checkThatSubtasksHaveDifferentId() {
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", 1);
        Subtask subtask2 = new Subtask("Epic1Subtask", "Subtask2", 2);

        Assertions.assertNotEquals(subtask1.getId(), subtask2.getId());
    }

    @Test
    public void checkThatSubtasksNotEquals() {
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", 1);
        Subtask subtask2 = new Subtask("Epic1Subtask", "Subtask2", 2);

        Assertions.assertNotEquals(subtask1, subtask2);
    }
}
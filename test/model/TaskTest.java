package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    public void checkThatTasksHaveDifferentId() {
        Task task1 = new Task("Op1", "Op1");
        Task task2 = new Task("Op2", "Op2");

        Assertions.assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    public void checkThatTasksNotEquals() {
        Task task1 = new Task("Op1", "Op1");
        Task task2 = new Task("Op2", "Op2");

        Assertions.assertNotEquals(task1, task2);
    }
}
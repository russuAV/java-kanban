package handlers;

import managers.TaskManager;
import model.Subtask;

import java.util.List;

public class SubtasksHandler extends AbstractTaskHandler<Subtask> {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected List<Subtask> getAllTasks() {
        return taskManager.getAllSubtasks();
    }

    @Override
    protected Subtask getTaskById(int id) {
        return taskManager.getSubtaskById(id);
    }

    @Override
    protected void addTask(Subtask task) {
        taskManager.addSubtask(task);
    }

    @Override
    protected void updateTask(Subtask task) {
        taskManager.updateSubtask(task);
    }

    @Override
    protected void deleteTaskById(int id) {
        taskManager.deleteSubtaskById(id);
    }

    @Override
    protected void deleteAllTasks() {
        taskManager.deleteAllSubtasks();
    }

    @Override
    protected Class<Subtask> getTaskClass() {
        return Subtask.class;
    }

    @Override
    protected boolean checkThatCollectionHasSameId(int id) {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
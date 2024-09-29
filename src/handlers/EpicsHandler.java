package handlers;

import managers.TaskManager;
import model.Epic;

import java.util.List;

public class EpicsHandler extends AbstractTaskHandler<Epic> {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected List<Epic> getAllTasks() {
        return taskManager.getAllEpics();
    }

    @Override
    protected Epic getTaskById(int id) {
        return taskManager.getEpicById(id);
    }

    @Override
    protected void addTask(Epic task) {
        taskManager.addEpic(task);
    }

    @Override
    protected void updateTask(Epic task) {
        taskManager.updateEpic(task);
    }

    @Override
    protected void deleteTaskById(int id) {
        taskManager.deleteEpicById(id);
    }

    @Override
    protected void deleteAllTasks() {
        taskManager.deleteAllEpics();
    }

    @Override
    protected Class<Epic> getTaskClass() {
        return Epic.class;
    }

    @Override
    protected boolean checkThatCollectionHasSameId(int id) {
        List<Epic> epics = taskManager.getAllEpics();
        for (Epic epic : epics) {
            if (epic.getId() == id) {
                return true;
            }
        }
        return false;
    }
}

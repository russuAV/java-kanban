package Handlers;

import managers.TaskManager;
import model.Task;

import java.util.List;

public class TasksHandler extends AbstractTaskHandler<Task> {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected List<Task> getAllTasks() {
        return taskManager.getAllTasks();
    }

    @Override
    protected Task getTaskById(int id) {
        return taskManager.getTaskById(id);
    }

    @Override
    protected void addTask(Task task) {
        taskManager.addTask(task);
    }

    @Override
    protected void updateTask(Task task) {
        taskManager.updateTask(task);
    }

    @Override
    protected void deleteTaskById(int id) {
        taskManager.deleteTaskById(id);
    }

    @Override
    protected void deleteAllTasks() {
        taskManager.deleteAllTasks();
    }

    @Override
    protected Class<Task> getTaskClass() {
        return Task.class;
    }

    @Override
    protected boolean checkThatCollectionHasSameId(int id) {
       List<Task> tasks = taskManager.getAllTasks();
       for (Task task : tasks) {
           if (task.getId() == id) {
               return true;
           }
       }
       return false;
    }
}
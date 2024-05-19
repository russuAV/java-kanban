package managers;

import model.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;

    private final ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() > MAX_HISTORY_SIZE) {
            historyList.removeFirst();
        }
        if (task != null) {
            historyList.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
}
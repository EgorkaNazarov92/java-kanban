package history;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
	static final int SIZE_HISTORY = 10;
	ArrayList<Task> history;

	public InMemoryHistoryManager() {
		this.history = new ArrayList<>();
	}

	@Override
	public void add(Task task) {
		if (history.size() >= SIZE_HISTORY) {
			history.removeFirst();
		}
		history.add(task);
	}

	@Override
	public ArrayList<Task> getHistory() {
		return history;
	}
}

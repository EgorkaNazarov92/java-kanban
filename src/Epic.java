import java.util.ArrayList;

public class Epic extends Task {
	private final ArrayList<Subtask> subtasks;

	public Epic(String name, String description, int id) {
		super(name, description, id);
		this.subtasks = new ArrayList<>();
	}

	public void addSubTask(Subtask subtask) {
		subtasks.add(subtask);
	}

	public ArrayList<Subtask> getSubtasks() {
		return subtasks;
	}

	public void deleteSubtask(Subtask subtask) {
		subtasks.remove(subtask);
	}

}

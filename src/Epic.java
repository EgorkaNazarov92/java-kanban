import java.util.ArrayList;

public class Epic extends Task {
	private ArrayList<Integer> subTaskIds;

	public Epic(String name, String description, int id) {
		super(name, description, Status.NEW, id);
		this.subTaskIds = new ArrayList<>();
	}

	public Epic(String name, String description) {
		super(name, description, Status.NEW);
		this.subTaskIds = new ArrayList<>();
	}

	public void addSubTaskId(int subtask) {
		subTaskIds.add(subtask);
	}

	public ArrayList<Integer> getSubTaskIds() {
		return subTaskIds;
	}

	public void deleteSubtaskId(Integer subtask) {
		subTaskIds.remove(subtask);
	}

	public void deleteSubTasks() {
		subTaskIds = new ArrayList<>();
	}

}

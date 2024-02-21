package tasks;

import status.Status;

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
		if (subtask != super.getId()) {
			subTaskIds.add(subtask);
		}
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

	@Override
	public boolean equals(Object o) {
		if (o.getClass().equals(Epic.class)) {
			Epic epic = (Epic) o;
			return super.equals(o) && subTaskIds.equals(epic.subTaskIds);
		}
		return false;
	}
}

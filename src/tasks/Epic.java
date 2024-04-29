package tasks;

import status.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
	private ArrayList<Integer> subTaskIds;
	private LocalDateTime endTime;

	public Epic(String name, String description, int id) {
		super(name, description, Status.NEW, id);
		this.subTaskIds = new ArrayList<>();
		this.setType(TaskTypes.EPIC);
	}

	public Epic(String name, String description) {
		super(name, description, Status.NEW);
		this.subTaskIds = new ArrayList<>();
		this.setType(TaskTypes.EPIC);
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

	@Override
	public String toString() {
		return String.format("%d,%s,%s,%s,%s,%s,%s", super.getId(), super.getType(), super.getName(), super.getStatus(),
				super.getDescription(), getStartTime(), getEndTime());
	}

	@Override
	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
}

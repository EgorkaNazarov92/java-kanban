package tasks;

import status.Status;

public class Subtask extends Task {
	private final int epicId;

	public Subtask(String name, String description, Status status, int id, int epicId) {
		super(name, description, status, id);
		this.epicId = epicId;
		this.setType(TaskTypes.SUBTASK);
	}

	public Subtask(String name, String description, Status status, int epicId) {
		super(name, description, status);
		this.epicId = epicId;
		this.setType(TaskTypes.SUBTASK);
	}

	public int getEpicId() {
		return epicId;
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass().equals(Subtask.class)) {
			Subtask subtask = (Subtask) o;
			return super.equals(o) && epicId == subtask.epicId;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%d,%s,%s,%s,%s,%d", super.getId(), super.getType(), super.getName(), super.getStatus(),
				super.getDescription(), epicId);
	}
}

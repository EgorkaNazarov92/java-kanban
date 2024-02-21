package tasks;

import status.Status;

public class Subtask extends Task {
	private final int epicId;

	public Subtask(String name, String description, Status status, int id, int epicId) {
		super(name, description, status, id);
		this.epicId = epicId;
	}

	public Subtask(String name, String description, Status status, int epicId) {
		super(name, description, status);
		this.epicId = epicId;
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
}

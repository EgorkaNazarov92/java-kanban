package tasks;
import status.Status;

import java.util.Objects;

public class Task {
	private String name;
	private String description;
	private int id;
	private Status status;

	public Task(String name, String description, Status status, int id) {
		this.name = name;
		this.description = description;
		this.id = id;
		this.status = status;
	}

	public Task(String name, String description, Status status) {
		this.name = name;
		this.description = description;
		this.id = 0;
		this.status = status;
	}

	@Override
	public String toString() {
		return "tasks.Task{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				", taskId=" + id +
				", status=" + status +
				'}';
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Task task = (Task) o;
		return Objects.equals(name, task.name) && Objects.equals(description, task.description)
				&& id == task.id && Objects.equals(status, task.status);
	}
}

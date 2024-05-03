package tasks;
import com.google.gson.reflect.TypeToken;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Task {
	private String name;
	private String description;
	private int id;
	private Status status;
	private TaskTypes type;
	private int duration;
	private LocalDateTime startTime;

	public Task(String name, String description, Status status, int id) {
		this.name = name;
		this.description = description;
		this.id = id;
		this.status = status;
		this.type = TaskTypes.TASK;
	}

	public Task(String name, String description, Status status) {
		this.name = name;
		this.description = description;
		this.id = 0;
		this.status = status;
		this.type = TaskTypes.TASK;
	}

	public Task(String name, String description, Status status, int id, LocalDateTime startTime, int duration) {
		this.name = name;
		this.description = description;
		this.id = id;
		this.status = status;
		this.type = TaskTypes.TASK;
		this.startTime = startTime;
		this.duration = duration;
	}

	public Task(String name, String description, Status status, LocalDateTime startTime, int duration) {
		this.name = name;
		this.description = description;
		this.id = 0;
		this.status = status;
		this.type = TaskTypes.TASK;
		this.startTime = startTime;
		this.duration = duration;
	}


	@Override
	public String toString() {
		return String.format("%d,%s,%s,%s,%s,%s,%s,%d", id, type, name, status, description,
				startTime, getEndTime(), duration);
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

	public TaskTypes getType() {
		return type;
	}

	public void setType(TaskTypes type) {
		this.type = type;
	}

	public LocalDateTime getEndTime() {
		if (startTime == null) return null;
		return startTime.plus(Duration.ofMinutes(duration));
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Task task = (Task) o;
		return Objects.equals(name, task.name) && Objects.equals(description, task.description)
				&& id == task.id && Objects.equals(status, task.status);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public int getDuration() {
		return duration;
	}


	public static class TaskListTypeToken extends TypeToken<List<Task>> {

	}
}

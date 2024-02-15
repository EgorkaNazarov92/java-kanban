public class Task {
	private final String name;
	private final String description;
	private final int taskId;
	private Status status;

	public Task(String name, String description, int taskId) {
		this.name = name;
		this.description = description;
		this.taskId = taskId;
		this.status = Status.NEW;
	}

	@Override
	public String toString() {
		return "Task{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				", taskId=" + taskId +
				", status=" + status +
				'}';
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public int getTaskId() {
		return taskId;
	}
}

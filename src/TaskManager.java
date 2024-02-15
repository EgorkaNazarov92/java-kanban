import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
	private final HashMap<Integer, Task> tasks;
	private final HashMap<Integer, Epic> epics;
	private final HashMap<Integer, Subtask> subtasks;

	public TaskManager() {
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subtasks = new HashMap<>();
	}

	public void createTask(String name, String description) {
		int taskId = Objects.hash(name, description);
		if (tasks.containsKey(taskId)) {
			System.out.println("Данный task уже существует: " + getTaskById(taskId));
			return;
		}
		Task task = new Task(name, description, taskId);
		tasks.put(taskId, task);
	}

	public void createEpic(String name, String description) {
		int epicId = Objects.hash(name, description);
		if (epics.containsKey(epicId)) {
			System.out.println("Данный epic уже существует: " + getEpicById(epicId));
			return;
		}
		Epic epic = new Epic(name, description, epicId);
		epics.put(epicId, epic);
	}

	public void createSubTask(String name, String description, int epicId) {
		int subtaskId = Objects.hash(name, description);
		if (subtasks.containsKey(subtaskId)) {
			System.out.println("Данный subtask уже существует: " + getSubTaskById(subtaskId));
			return;
		}
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого Epic");
			return;
		}
		Subtask subtask = new Subtask(name, description, subtaskId, epicId);
		subtasks.put(subtaskId, subtask);
		epic.addSubTask(subtask);
	}

	public ArrayList<Task> getsTasks() {
		return new ArrayList<Task>(tasks.values());
	}

	public ArrayList<Epic> getsEpics() {
		return new ArrayList<Epic>(epics.values());
	}

	public ArrayList<Subtask> getsSubTasks() {
		return new ArrayList<Subtask>(subtasks.values());
	}

	public void deleteAllTasks() {
		tasks.clear();
	}

	public void deleteAllEpics() {
		epics.clear();
		subtasks.clear();
	}

	public void deleteAllSubTasks() {
		subtasks.clear();
	}

	public Task getTaskById(int taskId) {
		return tasks.get(taskId);
	}

	public Epic getEpicById(int epicId) {
		return epics.get(epicId);
	}

	public Subtask getSubTaskById(int subTaskId) {
		return subtasks.get(subTaskId);
	}

	public void deleteTaskById(int taskId) {
		tasks.remove(taskId);
	}

	public void deleteEpicById(int epicId) {
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого epic");
			return;
		}
		ArrayList<Subtask> tmpSubtasks = epic.getSubtasks();
		epics.remove(epicId);
		for (Subtask tmpSubtask : tmpSubtasks) {
			subtasks.remove(tmpSubtask.getTaskId());
		}
	}

	public void deleteSubTaskById(int subTaskId) {
		Subtask subtask = subtasks.get(subTaskId);
		if (subtask == null) {
			System.out.println("Нет такого subtask");
			return;
		}
		int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого эпика");
			return;
		}
		epic.deleteSubtask(subtask);
		subtasks.remove(subTaskId);
	}

	public ArrayList<Subtask> getSubtasks(int epicId) {
		Epic epic = epics.get(epicId);
		return epic.getSubtasks();
	}

	public void updateTask(int taskId, Task task) {
		tasks.put(taskId, task);
	}

	public void updateEpic(int epicId, Epic epic) {
		Epic tmpEpic = epics.get(epicId);
		if (tmpEpic == null) {
			System.out.println("Нет такого эпика");
		}
		ArrayList<Subtask> tmpSubtasks = tmpEpic.getSubtasks();
		epics.put(epicId, epic);
		for (Subtask subtask : tmpSubtasks) {
			epic.addSubTask(subtask);
		}
	}

	public void updateSubTask(int subTaskId, Subtask subtask) {
		int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого epic");
			return;
		}
		Subtask tmpSubTask = subtasks.get(subTaskId);
		epic.deleteSubtask(tmpSubTask);
		subtasks.put(subTaskId, subtask);
		epic.addSubTask(subtask);
	}

	public void setTaskStatus(int taskId, Status status) {
		Task task = tasks.get(taskId);
		if (task == null) {
			System.out.println("Нет такоого task");
			return;
		}
		task.setStatus(status);
	}

	public void setSubTaskStatus(int subTaskId, Status status) {
		Subtask subtask = subtasks.get(subTaskId);
		if (subtask == null) {
			System.out.println("Нет такого subtask");
			return;
		}
		subtask.setStatus(status);

		int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		boolean updateEpic = true;
		for (Subtask subtaskTmp : epic.getSubtasks()) {
			if (!subtaskTmp.getStatus().equals(status)) {
				updateEpic = false;
				break;
			}
		}
		if (updateEpic) epic.setStatus(status);
	}
}

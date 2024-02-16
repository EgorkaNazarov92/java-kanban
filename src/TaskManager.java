import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
	private final HashMap<Integer, Task> tasks;
	private final HashMap<Integer, Epic> epics;
	private final HashMap<Integer, Subtask> subtasks;
	private int taskId = 1;

	public TaskManager() {
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subtasks = new HashMap<>();
	}

	public Task createTask(String name, String description, Status status) {
		int taskId = getTaskId();
		Task task = new Task(name, description, status, taskId);
		tasks.put(taskId, task);
		return  task;
	}

	public Epic createEpic(String name, String description) {
		int epicId = getTaskId();
		Epic epic = new Epic(name, description, epicId);
		epics.put(epicId, epic);
		return epic;
	}

	public Subtask createSubTask(String name, String description, Status status, int epicId) {
		int subtaskId = getTaskId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого Epic");
			return null;
		}
		Subtask subtask = new Subtask(name, description, status, subtaskId, epicId);
		subtasks.put(subtaskId, subtask);
		epic.addSubTaskId(subtaskId);
		updateEpicStatus(epic);
		return subtask;
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
		for (Epic epic : epics.values()) {
			epic.deleteSubTasks();
			updateEpicStatus(epic);
		}
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
		ArrayList<Integer> tmpSubtasks = epic.getSubTaskIds();
		epics.remove(epicId);
		for (Integer tmpSubtask : tmpSubtasks) {
			subtasks.remove(tmpSubtask);
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
		epic.deleteSubtaskId(subTaskId);
		subtasks.remove(subTaskId);
		updateEpicStatus(epic);
	}

	public ArrayList<Integer> getSubtasks(int epicId) {
		Epic epic = epics.get(epicId);
		return epic.getSubTaskIds();
	}

	public void updateTask(Task task) {
		int taskId = task.getId();
		if (tasks.containsKey(taskId)) tasks.put(taskId, task);
		else System.out.println("Нет такого task");
	}

	public void updateEpic(Epic epic) {
		int epicId = epic.getId();
		if (epics.containsKey(epicId)) {
			Epic tmpEpic = epics.get(epicId);
			tmpEpic.setName(epic.getName());
			tmpEpic.setDescription(epic.getDescription());
		} else System.out.println("Нет такого эпика");
	}

	public void updateSubTask(Subtask subtask) {
		int subTaskId = subtask.getId();
		if (subtasks.containsKey(subTaskId)) {
			if (epics.containsKey(subtask.getEpicId())) {
				Subtask tmpSubTask = subtasks.get(subTaskId);
				Epic epic = epics.get(tmpSubTask.getEpicId());
				epic.deleteSubtaskId(subTaskId);
				updateEpicStatus(epic);
				subtasks.put(subTaskId, subtask);
				epic = epics.get(subtask.getEpicId());
				epic.addSubTaskId(subTaskId);
				updateEpicStatus(epic);
			} else System.out.println("Нет такого Epic");
		} else System.out.println("Нет такого subtask");
	}

	public void updateEpicStatus(Epic epic) {
		ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
		Status status = Status.NEW;
		if (!subTaskIds.isEmpty()) {
			status = Status.DONE;
			for (Integer subTaskId : subTaskIds) {
				Status subStatus = subtasks.get(subTaskId).getStatus();
				if (subStatus.equals(Status.NEW)) {
					status = subStatus;
					break;
				} else if (subStatus.equals(Status.IN_PROGRESS)) {
					status = subStatus;
				}
			}
		}
		if (!status.equals(epic.getStatus())) epic.setStatus(status);
	}


	public int getTaskId() {
		return taskId++;
	}
}

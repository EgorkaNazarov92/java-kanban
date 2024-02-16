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

	public Task createTask(Task task) {
		int taskId = getTaskId();
		task.setId(taskId);
		tasks.put(taskId, task);
		return  task;
	}

	public Epic createEpic(Epic epic) {
		int epicId = getTaskId();
		epic.setId(epicId);
		epics.put(epicId, epic);
		return epic;
	}

	public Subtask createSubTask(Subtask subtask) {
		int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого Epic");
			return null;
		}
		int subtaskId = getTaskId();
		subtask.setId(subtaskId);
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

	public ArrayList<Subtask> getSubtasks(int epicId) {
		ArrayList<Subtask> epicSubTasks = new ArrayList<>();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого эпика");
			return null;
		}
		ArrayList<Integer> epicSubTaskIds = epic.getSubTaskIds();
		for (int subId : epicSubTaskIds) {
			epicSubTasks.add(subtasks.get(subId));
		}
		return epicSubTasks;
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
			Subtask tmpSubTask = subtasks.get(subTaskId);
			if (epics.containsKey(subtask.getEpicId()) && (subtask.getEpicId() == tmpSubTask.getEpicId())) {
				Epic epic = epics.get(tmpSubTask.getEpicId());
				subtasks.put(subTaskId, subtask);
				updateEpicStatus(epic);
			} else System.out.println("Неверно указан Epic");
		} else System.out.println("Нет такого subtask");
	}

	private void updateEpicStatus(Epic epic) {
		ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
		Status status = Status.NEW;
		if (!subTaskIds.isEmpty()) {
			Status tmpStatus = null;
			for (Integer subTaskId : subTaskIds) {
				Status subStatus = subtasks.get(subTaskId).getStatus();
				if (tmpStatus == null) {
					tmpStatus = subStatus;
				} else if (!tmpStatus.equals(subStatus)) {
					tmpStatus = Status.IN_PROGRESS;
					break;
				}
			}
			status = tmpStatus;
		}
		if (!status.equals(epic.getStatus())) epic.setStatus(status);
	}


	private int getTaskId() {
		return taskId++;
	}
}

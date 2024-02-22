package manager;

import history.HistoryManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static manager.Managers.getDefaultHistory;

public class InMemoryTaskManager implements TaskManager {
	private final Map<Integer, Task> tasks;
	private final Map<Integer, Epic> epics;
	private final Map<Integer, Subtask> subtasks;
	private final HistoryManager historyManager;
	private int taskId = 1;

	public InMemoryTaskManager() {
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subtasks = new HashMap<>();
		this.historyManager = getDefaultHistory();
	}

	@Override
	public Task createTask(Task task) {
		int taskId = getTaskId();
		task.setId(taskId);
		tasks.put(taskId, task);
		return  task;
	}

	@Override
	public Epic createEpic(Epic epic) {
		int epicId = getTaskId();
		epic.setId(epicId);
		epics.put(epicId, epic);
		return epic;
	}

	@Override
	public Subtask createSubTask(Subtask subtask) {
		int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			System.out.println("Нет такого tasks.Epic");
			return null;
		}
		int subtaskId = getTaskId();
		subtask.setId(subtaskId);
		subtasks.put(subtaskId, subtask);
		epic.addSubTaskId(subtaskId);
		updateEpicStatus(epic);
		return subtask;
	}

	@Override
	public ArrayList<Task> getsTasks() {
		return new ArrayList<Task>(tasks.values());
	}

	@Override
	public ArrayList<Epic> getsEpics() {
		return new ArrayList<Epic>(epics.values());
	}

	@Override
	public ArrayList<Subtask> getSubTasks() {
		return new ArrayList<Subtask>(subtasks.values());
	}

	@Override
	public void deleteAllTasks() {
		tasks.clear();
	}

	@Override
	public void deleteAllEpics() {
		epics.clear();
		subtasks.clear();
	}

	@Override
	public void deleteAllSubTasks() {
		subtasks.clear();
		for (Epic epic : epics.values()) {
			epic.deleteSubTasks();
			updateEpicStatus(epic);
		}
	}

	@Override
	public Task getTaskById(int taskId) {
		Task task = tasks.get(taskId);
		historyManager.add(task);
		return task;
	}

	@Override
	public Epic getEpicById(int epicId) {
		Epic epic = epics.get(epicId);
		historyManager.add(epic);
		return epic;
	}

	@Override
	public Subtask getSubTaskById(int subTaskId) {
		Subtask subtask = subtasks.get(subTaskId);
		historyManager.add(subtask);
		return subtask;
	}

	@Override
	public void deleteTaskById(int taskId) {
		tasks.remove(taskId);
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
	public void updateTask(Task task) {
		int taskId = task.getId();
		if (tasks.containsKey(taskId)) tasks.put(taskId, task);
		else System.out.println("Нет такого task");
	}

	@Override
	public void updateEpic(Epic epic) {
		int epicId = epic.getId();
		if (epics.containsKey(epicId)) {
			Epic tmpEpic = epics.get(epicId);
			tmpEpic.setName(epic.getName());
			tmpEpic.setDescription(epic.getDescription());
		} else System.out.println("Нет такого эпика");
	}

	@Override
	public void updateSubTask(Subtask subtask) {
		int subTaskId = subtask.getId();
		if (subtasks.containsKey(subTaskId)) {
			Subtask tmpSubTask = subtasks.get(subTaskId);
			if (epics.containsKey(subtask.getEpicId()) && (subtask.getEpicId() == tmpSubTask.getEpicId())) {
				Epic epic = epics.get(tmpSubTask.getEpicId());
				subtasks.put(subTaskId, subtask);
				updateEpicStatus(epic);
			} else System.out.println("Неверно указан tasks.Epic");
		} else System.out.println("Нет такого subtask");
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
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

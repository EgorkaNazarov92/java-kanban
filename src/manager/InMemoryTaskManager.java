package manager;

import exception.DurationException;
import history.HistoryManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static manager.Managers.getDefaultHistory;

public class InMemoryTaskManager implements TaskManager {
	protected final Map<Integer, Task> tasks;
	protected final Map<Integer, Epic> epics;
	protected final Map<Integer, Subtask> subtasks;
	protected final HistoryManager historyManager;
	protected int taskId = 1;

	public InMemoryTaskManager() {
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subtasks = new HashMap<>();
		this.historyManager = getDefaultHistory();
	}

	@Override
	public Task createTask(Task task) {
		if (!checkTimeTask(task)) throw new DurationException("Есть пересечение по времени выполения задач!");
		int taskId = getTaskId();
		task.setId(taskId);
		tasks.put(taskId, task);
		return  task;
	}

	@Override
	public Epic createEpic(Epic epic) {
		if (!checkTimeTask(epic)) throw new DurationException("Есть пересечение по времени выполения задач!");
		int epicId = getTaskId();
		epic.setId(epicId);
		epics.put(epicId, epic);
		return epic;
	}

	@Override
	public Subtask createSubTask(Subtask subtask) {
		if (!checkTimeTask(subtask)) throw new DurationException("Есть пересечение по времени выполения задач!");
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
		updateEpicFields(epic);
		return subtask;
	}

	@Override
	public ArrayList<Task> getsTasks() {
		return new ArrayList<>(tasks.values());
	}

	@Override
	public ArrayList<Epic> getsEpics() {
		return new ArrayList<>(epics.values());
	}

	@Override
	public ArrayList<Subtask> getSubTasks() {
		return new ArrayList<>(subtasks.values());
	}

	@Override
	public void deleteAllTasks() {
		for (Integer taskId : tasks.keySet()) historyManager.remove(taskId);
		tasks.clear();
	}

	@Override
	public void deleteAllEpics() {
		for (Epic epic : epics.values()) {
			List<Integer> tmpSubtasks = epic.getSubTaskIds();
			for (Integer subTaskId : tmpSubtasks) {
				historyManager.remove(subTaskId);
			}
			historyManager.remove(epic.getId());
		}
		epics.clear();
		subtasks.clear();
	}

	@Override
	public void deleteAllSubTasks() {
		for (Integer subTaskId : subtasks.keySet()) historyManager.remove(subTaskId);
		subtasks.clear();
		for (Epic epic : epics.values()) {
			epic.deleteSubTasks();
			updateEpicFields(epic);
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
		historyManager.remove(taskId);
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
		historyManager.remove(epicId);
		for (Integer tmpSubtask : tmpSubtasks) {
			subtasks.remove(tmpSubtask);
			historyManager.remove(tmpSubtask);
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
		historyManager.remove(subTaskId);
		updateEpicFields(epic);
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
		if (!checkTimeTask(task)) throw new DurationException("Есть пересечение по времени выполения задач!");
		int taskId = task.getId();
		if (tasks.containsKey(taskId)) tasks.put(taskId, task);
		else System.out.println("Нет такого task");
	}

	@Override
	public void updateEpic(Epic epic) {
		if (!checkTimeTask(epic)) throw new DurationException("Есть пересечение по времени выполения задач!");
		int epicId = epic.getId();
		if (epics.containsKey(epicId)) {
			Epic tmpEpic = epics.get(epicId);
			tmpEpic.setName(epic.getName());
			tmpEpic.setDescription(epic.getDescription());
		} else System.out.println("Нет такого эпика");
	}

	@Override
	public void updateSubTask(Subtask subtask) {
		if (!checkTimeTask(subtask)) throw new DurationException("Есть пересечение по времени выполения задач!");
		int subTaskId = subtask.getId();
		if (subtasks.containsKey(subTaskId)) {
			Subtask tmpSubTask = subtasks.get(subTaskId);
			if (epics.containsKey(subtask.getEpicId()) && (subtask.getEpicId() == tmpSubTask.getEpicId())) {
				Epic epic = epics.get(tmpSubTask.getEpicId());
				subtasks.put(subTaskId, subtask);
				updateEpicFields(epic);
			} else System.out.println("Неверно указан tasks.Epic");
		} else System.out.println("Нет такого subtask");
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}

	protected void updateEpicFields(Epic epic) {
		ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
		Status status = Status.NEW;
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		if (!subTaskIds.isEmpty()) {
			Status tmpStatus = null;
			for (Integer subTaskId : subTaskIds) {
				Subtask subtask = subtasks.get(subTaskId);
				Status subStatus = subtask.getStatus();
				if (tmpStatus == null) {
					tmpStatus = subStatus;
				} else if (!tmpStatus.equals(subStatus)) {
					tmpStatus = Status.IN_PROGRESS;
				}
				LocalDateTime subStartTime = subtask.getStartTime();
				LocalDateTime subEndTime = subtask.getEndTime();
				if (startTime == null || subStartTime.isBefore(startTime)) startTime = subStartTime;
				if (endTime == null || subEndTime.isAfter(endTime)) endTime = subEndTime;
			}
			status = tmpStatus;
		}
		if (!status.equals(epic.getStatus())) epic.setStatus(status);
		if (startTime != null && !startTime.equals(epic.getStartTime())) epic.setStartTime(startTime);
		if (endTime != null && !endTime.equals(epic.getEndTime())) epic.setEndTime(endTime);
		if (startTime != null && endTime != null) epic.setDuration((int) Duration.between(startTime, endTime).toMinutes());
	}


	private int getTaskId() {
		return taskId++;
	}

	public Set<Task> getPrioritizedTasks() {
		Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getType));
		prioritizedTasks.addAll(tasks.values().stream().filter(task -> task.getStartTime() != null).toList());
		prioritizedTasks.addAll(epics.values().stream().filter(epic -> epic.getStartTime() != null).toList());
		prioritizedTasks.addAll(subtasks.values().stream().filter(subtask -> subtask.getStartTime() != null).toList());
		return prioritizedTasks;
	}

	private boolean checkTimeTask(Task task) {
		if (task.getStartTime() != null) {
			LocalDateTime taskStartTime = task.getStartTime();
			LocalDateTime taskEndTime = task.getEndTime();
			Set<Task> prioritizedTasks = getPrioritizedTasks();
			return prioritizedTasks.stream().filter(priorityTask -> !priorityTask.getType().equals(TaskTypes.EPIC)
					&& task.getId() != priorityTask.getId()).allMatch(priorityTask ->
					((taskStartTime.isBefore(priorityTask.getStartTime())
					&& taskEndTime.isBefore(priorityTask.getStartTime()))
					|| (taskStartTime.isAfter(priorityTask.getEndTime()))));
		} else return true;
	}
}

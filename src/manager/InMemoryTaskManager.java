package manager;

import exception.DurationException;
import history.HistoryManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTypes;

import java.time.LocalDateTime;
import java.util.*;

import static manager.Managers.getDefaultHistory;

public class InMemoryTaskManager implements TaskManager {
	protected final Map<Integer, Task> tasks;
	protected final Map<Integer, Epic> epics;
	protected final Map<Integer, Subtask> subtasks;
	protected final HistoryManager historyManager;
	protected int taskId = 1;

	protected Set<Task> prioritizedTasks;

	public InMemoryTaskManager() {
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subtasks = new HashMap<>();
		this.historyManager = getDefaultHistory();
		this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getType));
	}

	@Override
	public Task createTask(Task task) {
		if (!checkTimeTask(task)) throw new DurationException("Есть пересечение по времени выполения задач!");
		int taskId = getTaskId();
		task.setId(taskId);
		tasks.put(taskId, task);
		addTaskToPrioritizedTasks(task);
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
		addTaskToPrioritizedTasks(subtask);
		return subtask;
	}

	@Override
	public ArrayList<Task> getTasks() {
		return new ArrayList<>(tasks.values());
	}

	@Override
	public ArrayList<Epic> getEpics() {
		return new ArrayList<>(epics.values());
	}

	@Override
	public ArrayList<Subtask> getSubTasks() {
		return new ArrayList<>(subtasks.values());
	}

	@Override
	public void deleteAllTasks() {
		for (Integer taskId : tasks.keySet()) historyManager.remove(taskId);
		prioritizedTasks.removeIf(task -> task.getType().equals(TaskTypes.TASK));
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
		prioritizedTasks.removeIf(task -> task.getType().equals(TaskTypes.SUBTASK));
		epics.clear();
		subtasks.clear();
	}

	@Override
	public void deleteAllSubTasks() {
		for (Integer subTaskId : subtasks.keySet()) historyManager.remove(subTaskId);
		prioritizedTasks.removeIf(task -> task.getType().equals(TaskTypes.SUBTASK));
		subtasks.clear();
		for (Epic epic : epics.values()) {
			epic.deleteSubTasks();
			updateEpicFields(epic);
		}
	}

	@Override
	public Task getTaskById(int taskId) {
		Task task = tasks.get(taskId);
		if (task == null) {
			throw new NoSuchElementException();
		}
		historyManager.add(task);
		return task;
	}

	@Override
	public Epic getEpicById(int epicId) {
		Epic epic = epics.get(epicId);
		if (epic == null) {
			throw new NoSuchElementException();
		}
		historyManager.add(epic);
		return epic;
	}

	@Override
	public Subtask getSubTaskById(int subTaskId) {
		Subtask subtask = subtasks.get(subTaskId);
		if (subtask == null) {
			throw new NoSuchElementException();
		}
		historyManager.add(subtask);
		return subtask;
	}

	@Override
	public void deleteTaskById(int taskId) {
		Task task = tasks.get(taskId);
		if (task == null) {
			throw new NoSuchElementException();
		}
		prioritizedTasks.remove(task);
		tasks.remove(taskId);
		historyManager.remove(taskId);
	}

	@Override
	public void deleteEpicById(int epicId) {
		Epic epic = epics.get(epicId);
		if (epic == null) {
			throw new NoSuchElementException();
		}
		ArrayList<Integer> tmpSubtasks = epic.getSubTaskIds();
		epics.remove(epicId);
		historyManager.remove(epicId);
		for (Integer tmpSubtask : tmpSubtasks) {
			prioritizedTasks.remove(subtasks.get(tmpSubtask));
			subtasks.remove(tmpSubtask);
			historyManager.remove(tmpSubtask);
		}
	}

	@Override
	public void deleteSubTaskById(int subTaskId) {
		Subtask subtask = subtasks.get(subTaskId);
		if (subtask == null) {
			throw new NoSuchElementException();
		}
		int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			throw new NoSuchElementException();
		}
		epic.deleteSubtaskId(subTaskId);
		prioritizedTasks.remove(subtask);
		subtasks.remove(subTaskId);
		historyManager.remove(subTaskId);
		updateEpicFields(epic);
	}

	@Override
	public ArrayList<Subtask> getSubtasks(int epicId) {
		ArrayList<Subtask> epicSubTasks = new ArrayList<>();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			throw new NoSuchElementException();
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
		if (tasks.containsKey(taskId)) {
			tasks.put(taskId, task);
			updateTaskToPrioritizedTasks(task);
		} else throw new NoSuchElementException();
	}

	@Override
	public void updateEpic(Epic epic) {
		int epicId = epic.getId();
		if (epics.containsKey(epicId)) {
			Epic tmpEpic = epics.get(epicId);
			tmpEpic.setName(epic.getName());
			tmpEpic.setDescription(epic.getDescription());
		} else throw new NoSuchElementException();
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
				updateTaskToPrioritizedTasks(subtask);
				updateEpicFields(epic);
			} else throw new NoSuchElementException();
		} else throw new NoSuchElementException();
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
		int subDuration = 0;
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
				if (subStartTime != null) {
					LocalDateTime subEndTime = subtask.getEndTime();
					if (startTime == null || subStartTime.isBefore(startTime)) startTime = subStartTime;
					if (endTime == null || subEndTime.isAfter(endTime)) endTime = subEndTime;
					subDuration = subDuration + subtask.getDuration();
				}
			}
			status = tmpStatus;
		}
		if (!status.equals(epic.getStatus())) epic.setStatus(status);
		epic.setStartTime(startTime);
		epic.setEndTime(endTime);
		epic.setDuration(subDuration);
	}


	private int getTaskId() {
		return taskId++;
	}

	public Set<Task> getPrioritizedTasks() {
		return prioritizedTasks;
	}

	protected void addTaskToPrioritizedTasks(Task task) {
		if (task.getStartTime() != null) prioritizedTasks.add(task);
	}

	protected void updateTaskToPrioritizedTasks(Task task) {
		prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId() == task.getId());
		if (task.getStartTime() != null) prioritizedTasks.add(task);
	}

	private boolean checkTimeTask(Task task) {
		if (task.getStartTime() != null) {
			LocalDateTime taskStartTime = task.getStartTime();
			LocalDateTime taskEndTime = task.getEndTime();
			Set<Task> prioritizedTasks = getPrioritizedTasks();
			return prioritizedTasks.stream().filter(priorityTask -> task.getId() != priorityTask.getId())
					.allMatch(priorityTask -> (taskEndTime.isBefore(priorityTask.getStartTime())
					|| (taskStartTime.isAfter(priorityTask.getEndTime()))));
		} else return true;
	}
}

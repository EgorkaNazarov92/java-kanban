package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
	Task createTask(Task task);

	Epic createEpic(Epic epic);

	Subtask createSubTask(Subtask subtask);

	List<Task> getsTasks();

	List<Epic> getsEpics();

	List<Subtask> getSubTasks();

	void deleteAllTasks();

	void deleteAllEpics();

	void deleteAllSubTasks();

	Task getTaskById(int taskId);

	Epic getEpicById(int epicId);

	Subtask getSubTaskById(int subTaskId);

	void deleteTaskById(int taskId);

	void deleteEpicById(int epicId);

	void deleteSubTaskById(int subTaskId);

	List<Subtask> getSubtasks(int epicId);

	void updateTask(Task task);

	void updateEpic(Epic epic);

	void updateSubTask(Subtask subtask);

	List<Task> getHistory();

	Set<Task> getPrioritizedTasks();
}

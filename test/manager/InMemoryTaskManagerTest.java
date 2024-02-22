package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
	private TaskManager taskManager;

	@BeforeEach
	void beforeEach() {
		taskManager = new InMemoryTaskManager();
	}

	@Test
	public void testAddSubtaskIdToSubtask() {
		Epic epic = new Epic("Test", "desc");
		epic = taskManager.createEpic(epic);
		Subtask subtask = new Subtask("Test", "desc", Status.NEW, epic.getId());
		subtask = taskManager.createSubTask(subtask);
		Subtask tmpSubtask = new Subtask("Test", "desc", Status.NEW, subtask.getId());
		taskManager.createSubTask(tmpSubtask);
		Assertions.assertEquals(1, taskManager.getSubTasks().size());
	}

	@Test
	public void testAddAnyTypeOfTasks() {
		Task tmpTask = new Task("Test", "desc", Status.NEW);
		Task task = taskManager.createTask(tmpTask);
		Epic tmpEpic = new Epic("Test", "desc");
		Epic epic = taskManager.createEpic(tmpEpic);
		Subtask tmpSubtask = new Subtask("Test", "desc", Status.NEW, epic.getId());
		Subtask subtask = taskManager.createSubTask(tmpSubtask);
		assertEquals(task, taskManager.getTaskById(task.getId()));
		assertEquals(epic, taskManager.getEpicById(epic.getId()));
		assertEquals(subtask, taskManager.getSubTaskById(subtask.getId()));
	}

	@Test
	public void testConflictTasksWithSameId() {
		Task tmpTask = new Task("Test", "desc", Status.NEW);
		Task tmpTask2 = new Task("Test", "desc", Status.NEW, 1);
		Task task = taskManager.createTask(tmpTask);
		Task task2 = taskManager.createTask(tmpTask2);
		assertEquals(2, taskManager.getsTasks().size());
		assertNotEquals(task.getId(), task2.getId());
	}

	@Test
	public void testEqualTask() {
		Task tmpTask = new Task("Test", "desc", Status.NEW, 2);
		Task task = taskManager.createTask(tmpTask);
		assertEquals(tmpTask, task);
	}

	@Test
	public void testHistory() {
		Task tmpTask = new Task("Test", "desc", Status.NEW, 2);
		Task task = taskManager.createTask(tmpTask);
		taskManager.getTaskById(task.getId());

		Task tmpTask2 = new Task("Test", "desc", Status.DONE, task.getId());
		taskManager.updateTask(tmpTask2);
		taskManager.getTaskById(task.getId());

		List<Task> tasks = taskManager.getHistory();
		assertNotEquals(tasks.get(0), tasks.get(1));
	}
}
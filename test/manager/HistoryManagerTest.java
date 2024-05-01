package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import static manager.Managers.getDefault;

public class HistoryManagerTest {
	private TaskManager taskManager;

	@BeforeEach
	void beforeEach() {
		taskManager = getDefault();
	}

	@Test
	public void testEmptyHistory() {
		Assertions.assertEquals(0, taskManager.getHistory().size());
	}

	@Test
	public void testDuplicateHistory() {
		Task task = taskManager.createTask(new Task("Task", "desc", Status.NEW));
		Assertions.assertEquals(0, taskManager.getHistory().size());
		taskManager.getTaskById(task.getId());
		Assertions.assertEquals(1, taskManager.getHistory().size());
		taskManager.getTaskById(task.getId());
		Assertions.assertEquals(1, taskManager.getHistory().size());
	}

	@Test
	public void testDeleteHistory() {
		Task task1 = taskManager.createTask(new Task("Task1", "desc", Status.NEW));
		Task task2 = taskManager.createTask(new Task("Task2", "desc", Status.NEW));
		Task task3 = taskManager.createTask(new Task("Task3", "desc", Status.NEW));
		Task task4 = taskManager.createTask(new Task("Task3", "desc", Status.NEW));
		taskManager.getTaskById(task3.getId());
		taskManager.getTaskById(task2.getId());
		taskManager.getTaskById(task4.getId());
		taskManager.getTaskById(task1.getId());
		Assertions.assertEquals(taskManager.getHistory().get(1), task2);
		taskManager.deleteTaskById(task2.getId());
		Assertions.assertNotEquals(taskManager.getHistory().get(1), task2);
		Assertions.assertEquals(taskManager.getHistory().getFirst(), task3);
		taskManager.deleteTaskById(task3.getId());
		Assertions.assertNotEquals(taskManager.getHistory().getFirst(), task3);
		Assertions.assertEquals(taskManager.getHistory().getLast(), task1);
		taskManager.deleteTaskById(task1.getId());
		Assertions.assertNotEquals(taskManager.getHistory().getLast(), task1);
	}
}

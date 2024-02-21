package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
	@Test
	public void testTwoTasksWithSameId() {
		Task task = new Task("Test", "desc", Status.NEW, 1);
		Task task2 = new Task("Test", "desc", Status.NEW, 1);
		Assertions.assertEquals(task, task2);
	}

	@Test
	public void testTwoEpicsWithSameId() {
		Epic epic = new Epic("Test", "desc", 1);
		epic.addSubTaskId(2);
		Epic epic2 = new Epic("Test", "desc", 1);
		epic2.addSubTaskId(2);
		Assertions.assertEquals(epic, epic2);
	}

	@Test
	public void testTwoSubTasksWithSameId() {
		Subtask subtask = new Subtask("Test", "desc", Status.NEW, 2, 1);
		Subtask subtask2 = new Subtask("Test", "desc", Status.NEW, 2, 1);
		Assertions.assertEquals(subtask, subtask2);
	}
}
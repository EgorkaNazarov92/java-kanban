package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
	@Test
	public void testTwoSubTasksWithSameId() {
		Subtask subtask = new Subtask("Test", "desc", Status.NEW, 2, 1);
		Subtask subtask2 = new Subtask("Test", "desc", Status.NEW, 2, 1);
		Assertions.assertEquals(subtask, subtask2);
	}
}
package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
	@Test
	public void testTwoEpicsWithSameId() {
		Epic epic = new Epic("Test", "desc", 1);
		epic.addSubTaskId(2);
		Epic epic2 = new Epic("Test", "desc", 1);
		epic2.addSubTaskId(2);
		Assertions.assertEquals(epic, epic2);
	}
	@Test
	public void testAddSubtaskToEpicWithSameId() {
		Epic epic = new Epic("Test", "desc", 1);
		epic.addSubTaskId(epic.getId());
		Assertions.assertEquals(0, epic.getSubTaskIds().size());
	}
}
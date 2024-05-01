package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;

import java.time.LocalDateTime;


public abstract class TaskManagerTest<T extends TaskManager> {
	protected TaskManager taskManager;

	@Test
	public void testStatusAndTimeEpic() {
		Epic epic = taskManager.createEpic(new Epic("epic", "epicdesc"));
		Subtask sub1 = taskManager.createSubTask(new Subtask("sub1", "subdesc1",
				Status.NEW, LocalDateTime.now(), 30, 100));
		Assertions.assertEquals(0, taskManager.getSubTasks().size());
		sub1 = taskManager.createSubTask(new Subtask("sub1", "subdesc1",
				Status.NEW, LocalDateTime.now(), 30, epic.getId()));
		Assertions.assertEquals(1, taskManager.getSubTasks().size());
		Assertions.assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus());
		Subtask sub2 = taskManager.createSubTask(new Subtask("sub2", "subdesc2",
				Status.DONE, LocalDateTime.now().plusDays(1), 30, epic.getId()));
		Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
		sub1.setStatus(Status.DONE);
		taskManager.updateSubTask(sub1);
		Assertions.assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
		Assertions.assertEquals(sub1.getStartTime(), taskManager.getEpicById(epic.getId()).getStartTime());
		Assertions.assertEquals(sub2.getEndTime(), taskManager.getEpicById(epic.getId()).getEndTime());
	}
}

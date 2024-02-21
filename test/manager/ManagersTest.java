package manager;

import history.HistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

	@Test
	void testGetDefault() {
		TaskManager taskManager = Managers.getDefault();
		Assertions.assertNotNull(taskManager);
	}

	@Test
	void testGetDefaultHistory() {
		HistoryManager historyManager = Managers.getDefaultHistory();
		Assertions.assertNotNull(historyManager);
	}
}
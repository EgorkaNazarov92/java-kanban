package manager;

import history.HistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
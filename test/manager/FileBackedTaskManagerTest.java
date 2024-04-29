package manager;

import exception.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static manager.Managers.getFileBackedTaskManager;

public class FileBackedTaskManagerTest extends  TaskManagerTest<FileBackedTaskManager> {
	Path pathDir = Paths.get("resources");
	Path pathFile = Paths.get("resources/file.csv");

	@Override
	public void testStatusAndTimeEpic() {
		try {
			Files.deleteIfExists(pathFile);
			super.testStatusAndTimeEpic();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createEmptyFileTest() {
		try {
			Files.deleteIfExists(pathFile);
			Assertions.assertFalse(pathFile.toFile().isFile());
			Files.deleteIfExists(pathDir);
			Assertions.assertFalse(pathDir.toFile().isDirectory());
			taskManager = getFileBackedTaskManager();
			Assertions.assertTrue(pathFile.toFile().isFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void addTasksToFileTest() {
		try {
			Files.deleteIfExists(pathFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		taskManager = getFileBackedTaskManager();
		Task task = new Task("Task", "TaskDesc", Status.NEW);
		task = taskManager.createTask(task);
		Epic epic = new Epic("Epic", "EpicDesc");
		epic = taskManager.createEpic(epic);
		Subtask subtask = new Subtask("Subtask", "sub desc", Status.IN_PROGRESS, epic.getId());
		subtask = taskManager.createSubTask(subtask);
		taskManager.getTaskById(task.getId());
		taskManager.getSubTaskById(subtask.getId());
		try {
			Reader fileReader = new FileReader(pathFile.toFile());
			BufferedReader br = new BufferedReader(fileReader);
			br.readLine();
			while (br.ready()) {
				String line = br.readLine();
				Assertions.assertTrue((line.contains(String.valueOf(task.getId()))
						&& line.contains(task.getName()) && line.contains(task.getDescription()))
						|| (line.contains(String.valueOf(epic.getId()))
						&& line.contains(epic.getName()) && line.contains(epic.getDescription()))
						|| (line.contains(String.valueOf(subtask.getId()))
						&& line.contains(subtask.getName()) && line.contains(subtask.getDescription()))
						|| (line.contains(task.getId() + "," + subtask.getId())));
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void loadFileTest() {
		try {
			Files.deleteIfExists(pathFile);
			Files.createFile(pathFile);
			try (Writer writer = new FileWriter(pathFile.toFile())) {
				writer.write("id,type,name,status,description,startTime,endTime,epic\n");
				writer.write("1,TASK,New Task,DONE,New Task Desc,null,null\n");
				writer.write("2,EPIC,New Epic,NEW, New Epic Desc,null,null\n");
				writer.write("3,SUBTASK,New Subtask,NEW,New sub desc,null,null,2\n");
				writer.write("3,1");
			}
			taskManager = getFileBackedTaskManager();
			Task task = taskManager.getTaskById(1);
			List<Task> history = taskManager.getHistory();
			Assertions.assertEquals(task.getId(), 1);
			Assertions.assertEquals(task.getName(), "New Task");
			Assertions.assertEquals(task.getStatus(), Status.DONE);
			Epic epic = taskManager.getEpicById(2);
			Assertions.assertEquals(epic.getId(), 2);
			Assertions.assertEquals(epic.getName(), "New Epic");
			Assertions.assertEquals(epic.getStatus(), Status.NEW);
			Subtask subtask = taskManager.getSubTaskById(3);
			Assertions.assertEquals(subtask.getId(), 3);
			Assertions.assertEquals(subtask.getName(), "New Subtask");
			Assertions.assertEquals(subtask.getStatus(), Status.NEW);
			Assertions.assertEquals(subtask.getEpicId(), 2);
			Assertions.assertTrue(history.contains(task));
			Assertions.assertFalse(history.contains(epic));
			Assertions.assertTrue(history.contains(subtask));
		} catch (IOException e) {
			throw new ManagerSaveException(e.getMessage());
		}
	}
}

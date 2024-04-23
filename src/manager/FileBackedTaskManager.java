package manager;

import exception.ManagerSaveException;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTypes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
	Path pathDir = Paths.get("resources");
	Path pathFile = Paths.get("resources/file.csv");

	public FileBackedTaskManager() {
		super();
		if (!pathDir.toFile().isDirectory()) {
			try {
				Files.createDirectory(pathDir);
			} catch (IOException e) {
				throw new ManagerSaveException(e.getMessage());
			}
		}
		File file = pathFile.toFile();
		if (!pathFile.toFile().isFile()) {
			try {
				Files.createFile(pathFile);
			} catch (IOException e) {
				throw new ManagerSaveException(e.getMessage());
			}
		}
		loadFromFile(file);
	}

	@Override
	public Task createTask(Task task) {
		Task newTask = super.createTask(task);
		save();
		return newTask;
	}

	@Override
	public Epic createEpic(Epic epic) {
		Epic newEpic = super.createEpic(epic);
		save();
		return newEpic;
	}

	@Override
	public Subtask createSubTask(Subtask subtask) {
		Subtask newSub = super.createSubTask(subtask);
		save();
		return newSub;
	}

	@Override
	public void deleteAllTasks() {
		super.deleteAllTasks();
		save();
	}

	@Override
	public void deleteAllEpics() {
		super.deleteAllEpics();
		save();
	}

	@Override
	public void deleteAllSubTasks() {
		super.deleteAllSubTasks();
		save();
	}

	@Override
	public void deleteTaskById(int taskId) {
		super.deleteTaskById(taskId);
		save();
	}

	@Override
	public void deleteSubTaskById(int taskId) {
		super.deleteSubTaskById(taskId);
		save();
	}

	@Override
	public void deleteEpicById(int taskId) {
		super.deleteEpicById(taskId);
		save();
	}

	@Override
	public void updateTask(Task task) {
		super.updateTask(task);
		save();
	}

	@Override
	public void updateEpic(Epic epic) {
		super.updateEpic(epic);
		save();
	}

	@Override
	public void updateSubTask(Subtask subtask) {
		super.updateSubTask(subtask);
		save();
	}

	@Override
	public Task getTaskById(int taskId) {
		Task task = super.getTaskById(taskId);
		save();
		return task;
	}

	@Override
	public Subtask getSubTaskById(int subTaskId) {
		Subtask subtask = super.getSubTaskById(subTaskId);
		save();
		return subtask;
	}

	@Override
	public Epic getEpicById(int epicId) {
		Epic epic = super.getEpicById(epicId);
		save();
		return epic;
	}

	private void save() {
		try {
			Files.deleteIfExists(pathFile);
			Files.createFile(pathFile);
			try (Writer writer = new FileWriter(pathFile.toFile())) {
				writer.write("id,type,name,status,description,epic\n");
				for (Task task : tasks.values()) writer.write(task.toString() + "\n");
				for (Epic epic : epics.values()) writer.write(epic.toString() + "\n");
				for (Subtask subtask : subtasks.values()) writer.write(subtask.toString() + "\n");
				for (Task histrory : historyManager.getHistory()) writer.write(histrory.getId() + ",");
			}
		} catch (IOException e) {
			throw new ManagerSaveException(e.getMessage());
		}
	}

	private Task fromString(String value) {
		Task task = null;
		String[] split = value.split(",");
		int id = Integer.parseInt(split[0]);
		TaskTypes type = TaskTypes.valueOf(split[1]);
		String name = split[2];
		Status status = Status.valueOf(split[3]);
		String desc = split[4];
		switch (type) {
			case TASK -> task = new Task(name, desc, status, id);
			case EPIC -> task = new Epic(name, desc, id);
			case SUBTASK -> task = new Subtask(name, desc, status, id, Integer.parseInt(split[5]));
		}
		return task;
	}

	private void loadFromFile(File file) {
		try {
			Reader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			br.readLine();
			String currentLine = br.readLine();
			String nextLine = br.readLine();
			while (currentLine != null) {
				if (nextLine == null && !currentLine.contains(TaskTypes.TASK.toString())
						&& !currentLine.contains(TaskTypes.EPIC.toString())
						&& !currentLine.contains(TaskTypes.SUBTASK.toString())) {
					String[] ids = currentLine.split(",");
					for (String id : ids) {
						Task task;
						int taskId = Integer.parseInt(id);
						if (tasks.containsKey(taskId)) {
							task = tasks.get(taskId);
						} else if (epics.containsKey(taskId)) {
							task = epics.get(taskId);
						} else if (subtasks.containsKey(taskId)) {
							task = subtasks.get(taskId);
						} else {
							System.out.println("Нет такого таска");
							break;
						}
						historyManager.add(task);
					}
				} else {
					Task task = fromString(currentLine);
					int taskId = task.getId();
					switch (task.getType()) {
						case TASK:
							super.tasks.put(taskId, task);
							break;
						case EPIC:
							super.epics.put(taskId, (Epic) task);
							break;
						case SUBTASK:
							Subtask subtask = (Subtask) task;
							int epicId = subtask.getEpicId();
							Epic epic = super.epics.get(epicId);
							if (epic == null) {
								System.out.println("Нет такого tasks.Epic");
								break;
							}
							super.subtasks.put(taskId, subtask);
							epic.addSubTaskId(taskId);
							super.updateEpicStatus(epic);
							break;
					}
					if (super.taskId <= taskId) super.taskId = ++taskId;
				}
				currentLine = nextLine;
				nextLine = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			throw new ManagerSaveException(e.getMessage());
		}
	}
}
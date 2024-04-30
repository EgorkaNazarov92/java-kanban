import manager.Managers;
import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("Поехали!");
		TaskManager taskManager = Managers.getFileBackedTaskManager();
		while (true) {
			System.out.println("Что вы хотите выполнить?");
			printMenu();
			int action = Integer.parseInt(scanner.nextLine());
			System.out.println("С каким типом задач будем работать?");
			String taskType = scanner.nextLine().toLowerCase();
			switch (action) {
				case 1:
					switch (taskType) {
						case "task":
							List<Task> tasks = taskManager.getsTasks();
							for (Task task : tasks) {
								System.out.println(task.toString());
							}
							break;
						case "epic":
							List<Epic> epics = taskManager.getsEpics();
							for (Epic epic : epics) {
								System.out.println(epic.toString());
							}
							break;
						case "subtask":
							List<Subtask> subtasks = taskManager.getSubTasks();
							for (Subtask subtask : subtasks) {
								System.out.println(subtask.toString());
							}
							break;
						default:
							System.out.println("Нет такого типа задач");
					}
					break;
				case 2:
					switch (taskType) {
						case "task":
							taskManager.deleteAllTasks();
							break;
						case "epic":
							taskManager.deleteAllEpics();
							break;
						case "subtask":
							taskManager.deleteAllSubTasks();
							break;
						default:
							System.out.println("Нет такого типа задач");
					}
					break;
				case 3:
					switch (taskType) {
						case "task":
							Task task = taskManager.getTaskById(getTaskId());
							System.out.println(task.toString());
							break;
						case "epic":
							Epic epic = taskManager.getEpicById(getTaskId());
							System.out.println(epic.toString());
							break;
						case "subtask":
							Subtask subtask = taskManager.getSubTaskById(getTaskId());
							System.out.println(subtask.toString());
							break;
						default:
							System.out.println("Нет такого типа задач");
					}
					break;
				case 4:
					System.out.println("Название тикета:");
					String name = scanner.nextLine();
					System.out.println("Описание:");
					String desc = scanner.nextLine();
					System.out.println("Статуc:");
					Status status = Status.valueOf(scanner.nextLine().toUpperCase());
					System.out.println("StartTime:");
					LocalDateTime startTime = LocalDateTime.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
					System.out.println("Duration:");
					int duration = Integer.parseInt(scanner.nextLine());
					switch (taskType) {
						case "task":
							Task task = new Task(name, desc, status, startTime, duration);
							taskManager.createTask(task);
							break;
						case "epic":
							Epic epic = new Epic(name, desc);
							taskManager.createEpic(epic);
							break;
						case "subtask":
							System.out.println("Введите epicId");
							int epicId = Integer.parseInt(scanner.nextLine());
							Subtask subtask = new Subtask(name, desc, status, startTime, duration, epicId);
							taskManager.createSubTask(subtask);
							break;
						default:
							System.out.println("Нет такого типа задач");
					}
					break;
				case 5:
					System.out.println("Введите номер задачи");
					int taskId = Integer.parseInt(scanner.nextLine());
					System.out.println("Название тикета:");
					String newName = scanner.nextLine();
					System.out.println("Описание:");
					String newDesc = scanner.nextLine();
					System.out.println("Статус:");
					Status newStatus = Status.valueOf(scanner.nextLine().toUpperCase());
					System.out.println("StartTime:");
					LocalDateTime uodStartTime = LocalDateTime.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
					System.out.println("Duration:");
					int updDuration = Integer.parseInt(scanner.nextLine());
					switch (taskType) {
						case "task":
							Task task = new Task(newName, newDesc, newStatus, taskId, uodStartTime, updDuration);
							taskManager.updateTask(task);
							break;
						case "epic":
							Epic epic = new Epic(newName, newDesc, taskId);
							taskManager.updateEpic(epic);
							break;
						case "subtask":
							System.out.println("Введите epicId:");
							int epicId = Integer.parseInt(scanner.nextLine());
							Subtask subtask = new Subtask(newName, newDesc, newStatus, taskId, uodStartTime, updDuration, epicId);
							taskManager.updateSubTask(subtask);
							break;
						default:
							System.out.println("Нет такого типа задач");
					}
					break;
				case 6:
					switch (taskType) {
						case "task":
							taskManager.deleteTaskById(getTaskId());
							break;
						case "epic":
							taskManager.deleteEpicById(getTaskId());
							break;
						case "subtask":
							taskManager.deleteSubTaskById(getTaskId());
							break;
						default:
							System.out.println("Нет такого типа задач");
					}
					break;
				case 7:
					System.out.println("Введите номер эпика");
					int epicId = Integer.parseInt(scanner.nextLine());
					List<Subtask> subtasks = taskManager.getSubtasks(epicId);
					System.out.println(subtasks.toString());
					break;
				case 8:
					System.out.println(taskManager.getHistory());
					break;
				case 9:
					System.out.println(taskManager.getPrioritizedTasks());
					break;
				default:
					return;
			}
		}
	}

	public static void printMenu() {
		System.out.println("1. Получение списка всех задач.");
		System.out.println("2. Удаление всех задач");
		System.out.println("3. Получение по идентификатору");
		System.out.println("4. Создание задачу");
		System.out.println("5. Обновление");
		System.out.println("6. Удаление по идентификатору");
		System.out.println("7. Получение списка всех подзадач определённого эпика");
		System.out.println("8. Посмотреть историю.");
		System.out.println("9. Посмотреть отсортированный список");
	}

	public static int getTaskId() {
		System.out.println("Введите Id задачи");
		return Integer.parseInt(scanner.nextLine());

	}
}

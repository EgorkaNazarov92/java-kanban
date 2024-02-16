import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("Поехали!");
		TaskManager taskManager = new TaskManager();
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
							ArrayList<Task> tasks = taskManager.getsTasks();
							for (Task task : tasks) {
								System.out.println(task.toString());
							}
							break;
						case "epic":
							ArrayList<Epic> epics = taskManager.getsEpics();
							for (Epic epic : epics) {
								System.out.println(epic.toString());
							}
							break;
						case "subtask":
							ArrayList<Subtask> subtasks = taskManager.getsSubTasks();
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
					System.out.println("Статуы:");
					Status status = Status.valueOf(scanner.nextLine().toUpperCase());
					switch (taskType) {
						case "task":
							taskManager.createTask(name, desc, status);
							break;
						case "epic":
							taskManager.createEpic(name, desc);
							break;
						case "subtask":
							System.out.println("Введите epicId");
							int epicId = Integer.parseInt(scanner.nextLine());
							taskManager.createSubTask(name, desc, status, epicId);
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
					switch (taskType) {
						case "task":
							Task task = new Task(newName, newDesc, newStatus, taskId);
							taskManager.updateTask(task);
							break;
						case "epic":
							Epic epic = new Epic(newName, newDesc, taskId);
							taskManager.updateEpic(epic);
							break;
						case "subtask":
							System.out.println("Введите epicId:");
							int epicId = Integer.parseInt(scanner.nextLine());
							Subtask subtask = new Subtask(newName, newDesc, newStatus, taskId, epicId);
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
					ArrayList<Integer> subtasks = taskManager.getSubtasks(epicId);
					System.out.println(subtasks.toString());
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
	}

	public static int getTaskId() {
		System.out.println("Введите Id задачи");
		return Integer.parseInt(scanner.nextLine());

	}
}

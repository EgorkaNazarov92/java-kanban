package httpserver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

	// создаём экземпляр InMemoryTaskManager
	TaskManager manager = Managers.getDefault();
	// передаём его в качестве аргумента в конструктор HttpTaskServer
	HttpTaskServer taskServer = HttpTaskServer.httpServer(manager);
	Gson gson = HttpTaskServer.getGsonBuilder();

	public HttpTaskManagerTasksTest() throws IOException {
	}

	@BeforeEach
	public void setUp() {
		manager.deleteAllTasks();
		manager.deleteAllSubTasks();
		manager.deleteAllEpics();
		HttpTaskServer.start();
	}

	@AfterEach
	public void shutDown() {
		HttpTaskServer.stop();
	}

	@Test
	public void testAddTask() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Task1", "Testing task 2",
				Status.NEW, LocalDateTime.now(), 5);
		// конвертируем её в JSON
		String taskJson = gson.toJson(task);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/tasks");
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
				.build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		List<Task> tasksFromManager = manager.getTasks();

		assertNotNull(tasksFromManager, "Задачи не возвращаются");
		assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Task1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
	}

	@Test
	public void testUpdateTask() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Task1", "Testing task 1",
				Status.NEW, LocalDateTime.now(), 5);
		task = manager.createTask(task);
		Task newTask = new Task("NewName", "NewDesc",
				Status.NEW, task.getId(), LocalDateTime.now(), 60);

		Task taskTmp = manager.getTaskById(task.getId());
		assertEquals("Task1", taskTmp.getName(), "Некорректное имя задачи");
		assertEquals("Testing task 1", taskTmp.getDescription(), "Некорректное описание");
		assertEquals(5, taskTmp.getDuration(), "Некорректная продолжительность");
		// конвертируем её в JSON
		String taskJson = gson.toJson(newTask);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/tasks");
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
				.build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		taskTmp = manager.getTaskById(task.getId());
		assertEquals("NewName", taskTmp.getName(), "Некорректное имя задачи");
		assertEquals("NewDesc", taskTmp.getDescription(), "Некорректное описание");
		assertEquals(60, taskTmp.getDuration(), "Некорректная продолжительность");
	}

	@Test
	public void testGetTasks() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Task1", "Testing task 1",
				Status.NEW, LocalDateTime.now(), 5);
		Task task2 = new Task("Task2", "Testing task 2",
				Status.IN_PROGRESS, LocalDateTime.now().plusDays(1), 60);
		manager.createTask(task);
		manager.createTask(task2);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/tasks");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		JsonElement jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		List<Task> tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());

		assertNotNull(tasksFromManager, "Задачи не возвращаются");
		assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
		assertEquals("Task2", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
	}

	@Test
	public void testGetTask() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Task1", "Testing task 1",
				Status.NEW, LocalDateTime.now(), 5);
		manager.createTask(task);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/tasks/1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		JsonElement jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		Task returnTask = gson.fromJson(jsonElement, Task.class);

		assertNotNull(returnTask, "Задача не возвращается");
		assertEquals(task.getName(), returnTask.getName(), "Некорректное имя задачи");
		assertEquals(task.getStatus(), returnTask.getStatus(), "Некорректный статус");
		assertEquals(1, returnTask.getId(), "Некорректное время");
	}

	@Test
	public void testGetNullTask() throws IOException, InterruptedException {

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/tasks/1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(404, response.statusCode());
	}

	@Test
	public void testDeleteTask() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Task1", "Testing task 1",
				Status.NEW, LocalDateTime.now(), 5);
		manager.createTask(task);

		assertEquals(1, manager.getTasks().size());

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/tasks/1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		assertEquals(0, manager.getTasks().size());
	}

	@Test
	public void testEpicAndSubTask() throws IOException, InterruptedException {
		// создаём задачу
		Epic epic = new Epic("Epic", "Testing task 2");
		// конвертируем её в JSON
		String epicJson = gson.toJson(epic);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/epics");
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
				.build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		request = HttpRequest.newBuilder().uri(url).GET().build();
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
		JsonElement jsonElement = JsonParser.parseString(response.body());
		List<Epic> tasksFromManager = gson.fromJson(jsonElement, new Epic.EpicListTypeToken().getType());
		epic = tasksFromManager.getFirst();

		assertNotNull(tasksFromManager, "Задачи не возвращаются");
		assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Epic", epic.getName(), "Некорректное имя задачи");

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now(), 5, epic.getId());
		String subtaskJson = gson.toJson(subtask);
		url = URI.create("http://localhost:8080/subtasks");
		request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
				.build();
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		request = HttpRequest.newBuilder().uri(url).GET().build();
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
		jsonElement = JsonParser.parseString(response.body());
		List<Subtask> subtasksFromManager = gson.fromJson(jsonElement, new Subtask.SubtaskListTypeToken().getType());
		subtask = subtasksFromManager.getFirst();

		assertNotNull(subtasksFromManager, "Задачи не возвращаются");
		assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Sub", subtask.getName(), "Некорректное имя задачи");
	}

	@Test
	public void testAddEpicAndSubTask() throws IOException, InterruptedException {
		// создаём задачу
		Epic epic = new Epic("Epic", "Testing epic");
		// конвертируем её в JSON
		String epicJson = gson.toJson(epic);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/epics");
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
				.build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		List<Epic> epicsFromManager = manager.getEpics();

		assertNotNull(epicsFromManager, "Задачи не возвращаются");
		assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
		assertEquals("Epic", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now(), 5, epicsFromManager.getFirst().getId());
		String subtaskJson = gson.toJson(subtask);
		url = URI.create("http://localhost:8080/subtasks");
		request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
				.build();
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		List<Subtask> subtasksFromManager = manager.getSubTasks();
		subtask = subtasksFromManager.getFirst();

		assertNotNull(subtasksFromManager, "Задачи не возвращаются");
		assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Sub", subtask.getName(), "Некорректное имя задачи");
	}

	@Test
	public void testUpdateEpicAndSubTask() throws IOException, InterruptedException {
		// создаём задачу
		Epic epic = new Epic("Epic", "Testing epic");
		epic = manager.createEpic(epic);
		Epic newEpic = new Epic("NewEpic", "NewDesc", epic.getId());

		Epic epicTmp = manager.getEpicById(epic.getId());
		assertEquals("Epic", epicTmp.getName(), "Некорректное имя задачи");
		assertEquals("Testing epic", epicTmp.getDescription(), "Некорректное описание");
		// конвертируем её в JSON
		String taskJson = gson.toJson(newEpic);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/epics");
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
				.build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		epicTmp = manager.getEpicById(epic.getId());
		assertEquals("NewEpic", epicTmp.getName(), "Некорректное имя задачи");
		assertEquals("NewDesc", epicTmp.getDescription(), "Некорректное описание");

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now(), 5, epic.getId());
		subtask = manager.createSubTask(subtask);
		Subtask newSubtask = new Subtask("newSub", "newSubdesc", Status.DONE,
				subtask.getId(), LocalDateTime.now(), 60, epic.getId());

		Subtask subtaskTmp = manager.getSubTaskById(subtask.getId());
		assertEquals("Sub", subtaskTmp.getName(), "Некорректное имя задачи");
		assertEquals("subdesc", subtaskTmp.getDescription(), "Некорректное описание");

		String subtaskJson = gson.toJson(newSubtask);
		url = URI.create("http://localhost:8080/subtasks");
		request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
				.build();
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, response.statusCode());

		// проверяем, что создалась одна задача с корректным именем
		subtaskTmp = manager.getSubTaskById(subtask.getId());

		assertEquals("newSub", subtaskTmp.getName(), "Некорректное имя задачи");
		assertEquals("newSubdesc", subtaskTmp.getDescription(), "Некорректное описание");
		assertEquals(Status.DONE, subtaskTmp.getStatus(), "Некорректный статус");
	}

	@Test
	public void testGetEpicsAndSubTasks() throws IOException, InterruptedException {
		// создаём задачу
		Epic epic = new Epic("Epic", "Testing epic");
		Epic epic2 = new Epic("Epic2", "Testing epic2");
		epic = manager.createEpic(epic);
		epic2 = manager.createEpic(epic2);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/epics");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		JsonElement jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		List<Epic> tasksFromManager = gson.fromJson(jsonElement, new Epic.EpicListTypeToken().getType());

		assertNotNull(tasksFromManager, "Задачи не возвращаются");
		assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Epic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
		assertEquals("Epic2", tasksFromManager.get(1).getName(), "Некорректное имя задачи");

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now(), 5, epic.getId());
		Subtask subtask2 = new Subtask("Sub2", "subdesc2", Status.DONE,
				LocalDateTime.now().plusDays(1), 60, epic2.getId());
		manager.createSubTask(subtask);
		manager.createSubTask(subtask2);

		url = URI.create("http://localhost:8080/subtasks");
		request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		List<Subtask> subtasksFromManager = gson.fromJson(jsonElement, new Subtask.SubtaskListTypeToken().getType());

		assertNotNull(subtasksFromManager, "Задачи не возвращаются");
		assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Sub", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
		assertEquals("Sub2", subtasksFromManager.get(1).getName(), "Некорректное имя задачи");
	}

	@Test
	public void testGetEpicAndSubTask() throws IOException, InterruptedException {
		// создаём задачу
		Epic epic = new Epic("Epic", "Testing epic");
		manager.createEpic(epic);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/epics/1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		JsonElement jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		Epic returnTask = gson.fromJson(jsonElement, Epic.class);

		assertNotNull(returnTask, "Задача не возвращается");
		assertEquals(epic.getName(), returnTask.getName(), "Некорректное имя задачи");
		assertEquals(epic.getStatus(), returnTask.getStatus(), "Некорректный статус");
		assertEquals(1, returnTask.getId(), "Некорректный id");

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now(), 5, returnTask.getId());
		subtask = manager.createSubTask(subtask);
		url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
		request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		Subtask returnSubTask = gson.fromJson(jsonElement, Subtask.class);

		assertNotNull(returnSubTask, "Задача не возвращается");
		assertEquals(subtask.getName(), returnSubTask.getName(), "Некорректное имя задачи");
		assertEquals(subtask.getStatus(), returnSubTask.getStatus(), "Некорректный статус");
		assertEquals(subtask.getId(), returnSubTask.getId(), "Некорректный id");
	}

	@Test
	public void testGetNullEpic() throws IOException, InterruptedException {

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/epics/1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(404, response.statusCode());
	}

	@Test
	public void testGetNullSubtask() throws IOException, InterruptedException {

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/subtasks/1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(404, response.statusCode());
	}

	@Test
	public void testDeleteEpicAndSubTask() throws IOException, InterruptedException {
		// создаём задачу
		Epic epic = new Epic("Epic", "Testing epic");
		epic = manager.createEpic(epic);

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now(), 5, epic.getId());
		subtask = manager.createSubTask(subtask);

		assertEquals(1, manager.getEpics().size());
		assertEquals(1, manager.getSubTasks().size());

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
		HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());
		assertEquals(0, manager.getSubTasks().size());

		url = URI.create("http://localhost:8080/epics/" + epic.getId());
		request = HttpRequest.newBuilder().uri(url).DELETE().build();

		// вызываем рест, отвечающий за создание задач
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());
		assertEquals(0, manager.getEpics().size());
	}

	@Test
	public void testGetEpicSubTasks() throws IOException, InterruptedException {
		// создаём задачу
		Epic epic = new Epic("Epic", "Testing epic");
		epic = manager.createEpic(epic);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		JsonElement jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		List<Subtask> tasksFromManager = gson.fromJson(jsonElement, new Subtask.SubtaskListTypeToken().getType());
		assertEquals(0, tasksFromManager.size());

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now(), 5, epic.getId());
		manager.createSubTask(subtask);

		// вызываем рест, отвечающий за создание задач
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		tasksFromManager = gson.fromJson(jsonElement, new Subtask.SubtaskListTypeToken().getType());
		assertEquals(1, tasksFromManager.size());
	}

	@Test
	public void testHistory() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Task1", "Testing task 2",
				Status.NEW, LocalDateTime.now(), 5);
		task = manager.createTask(task);

		Epic epic = new Epic("Epic", "Testing epic");
		epic = manager.createEpic(epic);

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now().plusDays(1), 60, epic.getId());
		subtask = manager.createSubTask(subtask);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/history");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		JsonElement jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		List<Task> tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
		assertEquals(0, tasksFromManager.size());

		manager.getTaskById(task.getId());
		manager.getSubTaskById(subtask.getId());

		// вызываем рест, отвечающий за создание задач
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
		assertEquals(2, tasksFromManager.size());

		manager.getEpicById(epic.getId());
		// вызываем рест, отвечающий за создание задач
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
		assertEquals(3, tasksFromManager.size());
	}

	@Test
	public void testPrioritized() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Task1", "Testing task 1",
				Status.NEW, LocalDateTime.now(), 5);
		task = manager.createTask(task);

		Epic epic = new Epic("Epic", "Testing epic");
		epic = manager.createEpic(epic);

		Subtask subtask = new Subtask("Sub", "subdesc", Status.IN_PROGRESS,
				LocalDateTime.now().minusDays(1), 60, epic.getId());
		subtask = manager.createSubTask(subtask);

		Task task2 = new Task("Task2", "Testing task 2",
				Status.DONE, LocalDateTime.now().plusDays(1), 5);
		task2 = manager.createTask(task2);

		// создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/prioritized");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());

		JsonElement jsonElement = JsonParser.parseString(response.body());
		// проверяем, что создалась одна задача с корректным именем
		List<Task> tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
		assertEquals(3, tasksFromManager.size());
		assertEquals(subtask.getId(), tasksFromManager.getFirst().getId());
		assertEquals(task.getId(), tasksFromManager.get(1).getId());
		assertEquals(task2.getId(), tasksFromManager.getLast().getId());
	}
}
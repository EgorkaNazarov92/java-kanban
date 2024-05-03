package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exception.DurationException;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class HttpTaskServer implements HttpHandler {
	private static final int PORT = 8080;
	private static TaskManager manager;
	private static HttpServer httpServer;

	public HttpTaskServer(TaskManager manager) throws IOException {
		HttpTaskServer.manager = manager;
		httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

		switch (endpoint) {
			case TASKS: {
				handleTasks(exchange);
				break;
			}
			case SUBTASKS: {
				handleSubTasks(exchange);
				break;
			}
			case EPICS: {
				handleEpics(exchange);
				break;
			}
			case HISTORY: {
				handleHistory(exchange);
				break;
			}
			case PRIORTIZED: {
				handlePriortized(exchange);
				break;
			}
			default:
				BaseHttpHandler.sendNotFound(exchange);
		}
	}

	private void handleTasks(HttpExchange exchange) throws IOException {
		String[] pathParts = exchange.getRequestURI().getPath().split("/");
		String requestMethod = exchange.getRequestMethod();
		switch (requestMethod) {
			case "GET" :
				if (pathParts.length == 2) {
					List<Task> tasks = manager.getTasks();

					Gson gson = getGsonBuilder();
					String tasksJson = gson.toJson(tasks);
					BaseHttpHandler.sendAnswerJson(exchange, tasksJson, 200);
				} else {
					Optional<Integer> taskId = getTaskId(exchange);
					if (taskId.isEmpty()) {
						BaseHttpHandler.sendText(exchange, "Некорректно указан taskId", 400);
						return;
					}
					try {
						Task task = manager.getTaskById(taskId.get());
						Gson gson = getGsonBuilder();
						String taskJson = gson.toJson(task);
						BaseHttpHandler.sendAnswerJson(exchange, taskJson, 200);
					} catch (NoSuchElementException e) {
						BaseHttpHandler.sendNotFound(exchange);
					}
				}
				break;
			case "POST" :
				InputStream inputStream = exchange.getRequestBody();
				String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				Gson gson = getGsonBuilder();
				Task task = gson.fromJson(taskJson, Task.class);
				Optional<Integer> taskId = Optional.of(task.getId());
				try {
					if (taskId.get() == 0) {
						manager.createTask(task);
					} else {
						manager.updateTask(task);
					}
					BaseHttpHandler.sendHasInteractions(exchange);
				} catch (DurationException e) {
					BaseHttpHandler.sendText(exchange, e.getMessage(), 406);
				} catch (NoSuchElementException e) {
					BaseHttpHandler.sendNotFound(exchange);
				}
				break;
			case "DELETE" :
				Optional<Integer> taskDelId = getTaskId(exchange);
				if (taskDelId.isEmpty()) {
					BaseHttpHandler.sendText(exchange, "Некорректно указан taskId", 400);
					return;
				}
				try {
					manager.deleteTaskById(taskDelId.get());
					BaseHttpHandler.sendText(exchange, "Task удален", 200);
				} catch (NoSuchElementException e) {
					BaseHttpHandler.sendNotFound(exchange);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
		}
	}

	private void handleSubTasks(HttpExchange exchange) throws IOException {
		String[] pathParts = exchange.getRequestURI().getPath().split("/");
		String requestMethod = exchange.getRequestMethod();
		switch (requestMethod) {
			case "GET" :
				if (pathParts.length == 2) {
					List<Subtask> subtask = manager.getSubTasks();

					Gson gson = getGsonBuilder();
					String tasksJson = gson.toJson(subtask);
					BaseHttpHandler.sendAnswerJson(exchange, tasksJson, 200);
				} else {
					Optional<Integer> taskId = getTaskId(exchange);
					if (taskId.isEmpty()) {
						BaseHttpHandler.sendText(exchange, "Некорректно указан taskId", 400);
						return;
					}
					try {
						Subtask subtask = manager.getSubTaskById(taskId.get());
						Gson gson = getGsonBuilder();
						String taskJson = gson.toJson(subtask);
						BaseHttpHandler.sendAnswerJson(exchange, taskJson, 200);
					} catch (NoSuchElementException e) {
						BaseHttpHandler.sendNotFound(exchange);
					}
				}
				break;
			case "POST" :
				InputStream inputStream = exchange.getRequestBody();
				String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				Gson gson = getGsonBuilder();
				Subtask subtask = gson.fromJson(taskJson, Subtask.class);
				Optional<Integer> taskId = Optional.of(subtask.getId());
				try {
					if (taskId.get() == 0) {
						manager.createSubTask(subtask);
					} else {
						manager.updateSubTask(subtask);
					}
					BaseHttpHandler.sendHasInteractions(exchange);
				} catch (DurationException e) {
					BaseHttpHandler.sendText(exchange, e.getMessage(), 406);
				} catch (NoSuchElementException e) {
					BaseHttpHandler.sendNotFound(exchange);
				}
				break;
			case "DELETE" :
				Optional<Integer> taskDelId = getTaskId(exchange);
				if (taskDelId.isEmpty()) {
					BaseHttpHandler.sendText(exchange, "Некорректно указан taskId", 400);
					return;
				}
				try {
					manager.deleteSubTaskById(taskDelId.get());
					BaseHttpHandler.sendText(exchange, "Subtask удален", 200);
				} catch (NoSuchElementException e) {
					BaseHttpHandler.sendNotFound(exchange);
				}
				break;
		}
	}

	private void handleEpics(HttpExchange exchange) throws IOException {
		String[] pathParts = exchange.getRequestURI().getPath().split("/");
		String requestMethod = exchange.getRequestMethod();
		switch (requestMethod) {
			case "GET" :
				if (pathParts.length == 2) {
					List<Epic> epics = manager.getEpics();

					Gson gson = getGsonBuilder();
					String tasksJson = gson.toJson(epics);
					BaseHttpHandler.sendAnswerJson(exchange, tasksJson, 200);
				} else if (pathParts.length == 4) {
					Optional<Integer> taskId = getTaskId(exchange);
					if (taskId.isEmpty()) {
						BaseHttpHandler.sendText(exchange, "Некорректно указан taskId", 400);
						return;
					}
					try {
						manager.getEpicById(taskId.get());
						List<Subtask> subtasks = manager.getSubtasks(taskId.get());
						Gson gson = getGsonBuilder();
						String taskJson = gson.toJson(subtasks);
						BaseHttpHandler.sendAnswerJson(exchange, taskJson, 200);
					} catch (NoSuchElementException e) {
						BaseHttpHandler.sendNotFound(exchange);
					}

				} else {
					Optional<Integer> taskId = getTaskId(exchange);
					if (taskId.isEmpty()) {
						BaseHttpHandler.sendText(exchange, "Некорректно указан taskId", 400);
						return;
					}
					try {
						Epic epic = manager.getEpicById(taskId.get());
						Gson gson = getGsonBuilder();
						String taskJson = gson.toJson(epic);
						BaseHttpHandler.sendAnswerJson(exchange, taskJson, 200);
					} catch (NoSuchElementException e) {
						BaseHttpHandler.sendNotFound(exchange);
					}
				}
				break;
			case "POST" :
				InputStream inputStream = exchange.getRequestBody();
				String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				Gson gson = getGsonBuilder();
				Epic epic = gson.fromJson(taskJson, Epic.class);
				Optional<Integer> taskId = Optional.of(epic.getId());
				try {
					if (taskId.get() == 0) {
						manager.createEpic(epic);
					} else {
						manager.updateEpic(epic);
					}
					BaseHttpHandler.sendHasInteractions(exchange);
				} catch (DurationException e) {
					BaseHttpHandler.sendText(exchange, e.getMessage(), 406);
				} catch (NoSuchElementException e) {
					BaseHttpHandler.sendNotFound(exchange);
				}
				break;
			case "DELETE" :
				Optional<Integer> taskDelId = getTaskId(exchange);
				if (taskDelId.isEmpty()) {
					BaseHttpHandler.sendText(exchange, "Некорректно указан taskId", 400);
					return;
				}
				try {
					manager.deleteEpicById(taskDelId.get());
					BaseHttpHandler.sendText(exchange, "Epic удален", 200);
				} catch (NoSuchElementException e) {
					BaseHttpHandler.sendNotFound(exchange);
				}
				break;
		}
	}

	private void handleHistory(HttpExchange exchange) throws IOException {
		List<Task> tasks = manager.getHistory();
		Gson gson = getGsonBuilder();
		String tasksJson = gson.toJson(tasks);
		BaseHttpHandler.sendAnswerJson(exchange, tasksJson, 200);
	}

	private void handlePriortized(HttpExchange exchange) throws IOException {
		Set<Task> tasks = manager.getPrioritizedTasks();
		Gson gson = getGsonBuilder();
		String tasksJson = gson.toJson(tasks);
		BaseHttpHandler.sendAnswerJson(exchange, tasksJson, 200);
	}

	private Optional<Integer> getTaskId(HttpExchange exchange) {
		String[] pathParts = exchange.getRequestURI().getPath().split("/");
		try {
			return Optional.of(Integer.parseInt(pathParts[2]));
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
			return Optional.empty();
		}
	}

	private Endpoint getEndpoint(String requestPath, String requestMethod) {
		if (!Arrays.asList("GET", "POST", "DELETE").contains(requestMethod)) return Endpoint.UNKNOWN;

		String[] pathParts = requestPath.split("/");
		String type = pathParts[1];
		return switch (type) {
			case "tasks" -> Endpoint.TASKS;
			case "subtasks" -> Endpoint.SUBTASKS;
			case "epics" -> Endpoint.EPICS;
			case "history" -> Endpoint.HISTORY;
			case "prioritized" -> Endpoint.PRIORTIZED;
			default -> Endpoint.UNKNOWN;
		};
	}

	public static Gson getGsonBuilder() {
		LocalDateTimeTypeAdapter localTimeTypeAdapter = new LocalDateTimeTypeAdapter();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.serializeNulls();
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, localTimeTypeAdapter);
		return gsonBuilder.create();
	}

	public static void main(String[] args) throws IOException {
		HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getFileBackedTaskManager());
		httpServer.createContext("/tasks", httpTaskServer);
		httpServer.createContext("/subtasks", httpTaskServer);
		httpServer.createContext("/epics", httpTaskServer);
		httpServer.createContext("/history", httpTaskServer);
		httpServer.createContext("/prioritized",httpTaskServer);
		start();
	}

	public static HttpTaskServer httpServer(TaskManager manager) throws IOException {
		HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
		httpServer.createContext("/tasks", httpTaskServer);
		httpServer.createContext("/subtasks", httpTaskServer);
		httpServer.createContext("/epics", httpTaskServer);
		httpServer.createContext("/history", httpTaskServer);
		httpServer.createContext("/prioritized",httpTaskServer);
		return httpTaskServer;
	}

	public static void start() {
		httpServer.start();
	}

	public static void stop() {
		httpServer.stop(0);
	}
}

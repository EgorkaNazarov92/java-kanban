package history;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

	Map<Integer, Node> history;
	private Node first;
	private Node last;

	private static class Node {
		public Task task;
		public Node next;
		public Node prev;

		public Node(Task task) {
			this.task = task;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			return node.task.getId() == task.getId();
		}
	}

	public InMemoryHistoryManager() {
		this.history = new HashMap<>();
	}

	@Override
	public void add(Task task) {
		linkLast(task);
	}

	private void linkLast(Task task) {
		Node node = new Node(task);
		if (first == null) {
			node.next = null;
			node.prev = null;
			first = node;
		} else {
			int taskId = task.getId();
			if (history.containsKey(taskId)) {
				remove(taskId);
			}
			if (node.equals(first)) {
				node.next = null;
				node.prev = null;
				first = node;
			} else {
				last.next = node;
				node.prev = last;
			}
		}
		last = node;
		history.put(task.getId(), node);
	}

	@Override
	public void remove(int id) {
		Node node = history.get(id);
		removeNode(node);
		history.remove(id);
	}

	private void removeNode(Node node) {
		if (node.equals(first)) {
			Node next = node.next;
			if (next != null) {
				first = next;
			}
		} else if (node.equals(last)) {
			last = node.prev;
		} else {
			Node prev = node.prev;
			Node next = node.next;
			prev.next = next;
			next.prev = prev;
		}
	}

	@Override
	public List<Task> getHistory() {
		return getTasks();
	}

	private List<Task> getTasks() {
		List<Task> tasks = new ArrayList<>();
		Node result = first;
		for (int i = 0; i < history.size(); i++) {
			tasks.add(result.task);
			result = result.next;
		}
		return tasks;
	}
}

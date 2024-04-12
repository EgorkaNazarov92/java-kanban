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
		int taskId = task.getId();
		if (history.containsKey(taskId)) {
			remove(taskId);
		}

		Node node = new Node(task);
		if (first == null) {
			first = node;
		} else {
			last.next = node;
			node.prev = last;
		}
		last = node;
		history.put(task.getId(), node);
	}

	@Override
	public void remove(int id) {
		Node node = history.remove(id);
		removeNode(node);
	}

	private void removeNode(Node node) {
		if (node != null) {
			if (node.equals(first)) {
				first = node.next;
				if (first != null) {
					first.prev = null;
				}
			} else if (node.equals(last)) {
				last = node.prev;
				last.next = null;
			} else {
				Node prev = node.prev;
				Node next = node.next;
				prev.next = next;
				next.prev = prev;
			}
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

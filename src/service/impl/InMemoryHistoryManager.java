package service.impl;

import model.Task;
import service.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyList;

    public InMemoryHistoryManager() {
        this.historyList = new CustomLinkedList<>();
    }

    @Override
    public void add(Task task) {
       historyList.addLast(task);
    }

    @Override
    public void remove(int id) {
        historyList.removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getHistory();
    }

    private static class CustomLinkedList<T extends Task> {
        private final Map<Integer, Node<T>> memory = new HashMap<>();
        private Node<T> head;
        private Node<T> tail;

        public void addLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            Node<T> oldNode = memory.put(element.getId(), newNode);
            if (oldNode != null) {
                removeNode(oldNode);
            }
        }

        private void removeNode(Node<T> node) {
            final Node<T> prev = node.prev;
            final Node<T> next = node.next;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
            }
        }

        private void removeNode(int id) {
            if (memory.get(id) != null) {
                removeNode(memory.get(id));
            }
        }

        private ArrayList<T> getHistory() {
            ArrayList<T> result = new ArrayList<>();
            for (Node<T> node = head; node != null; node = node.next) {
                result.add(node.data);
            }
            return result;
        }

        private static class Node<E> {
            private final E data;
            private Node<E> prev;
            private Node<E> next;

            public Node(Node<E> prev, E data, Node<E> next) {
                this.data = data;
                this.prev = prev;
                this.next = next;
            }
        }
    }
}

package ru.practicum.konkov.customcollection;

import ru.practicum.konkov.task.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CustomLinkedList {
    private Node tail;

    public Node linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail != null) {
            oldTail.next = newNode;
        }
        return newNode;
    }

    public List<Task> getTasks(Map<Integer, Node> nodeList) {
        List<Task> tasks = new ArrayList<>();
        Node node = tail;
        while (node != null) {
            if (nodeList.containsValue(node)) {
                tasks.add(node.data);
            }
            node = node.prev;
        }
        return tasks;
    }

    public void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            node.next.prev = null;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            node.prev.next = null;
            tail = node.prev;
        }
    }
}

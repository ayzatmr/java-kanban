package utils;

import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {
    int size;
    Node head;
    Node tail;
    Map<Integer, Node> map = new HashMap<>();

    public static class Node {
        int key;
        Task data;
        Node previous;
        Node next;

        public Node(int key, Task data) {
            this.key = key;
            this.data = data;
        }
    }

    public Node get(int key) {
        return map.get(key);
    }

    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node node = head;
        while (node != null) {
            list.add(node.data);
            node = node.next;
        }
        return list;
    }

    public Task removeNode(Node node) {
        if (node == null) {
            return null;
        }

        Node prev = node.previous;
        Node next = node.next;
        if (prev == null && next == null) {
            head = null;
            tail = null;
            size--;
            map.clear();
            return node.data;
        }
        if (prev == null) {
            next.previous = null;
            head = next;
        } else {
            prev.next = next;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.previous = prev;
        }
        size--;
        map.remove(node.key);
        return node.data;
    }


    public void linkLast(int key, Task value) {
        Node currentNode = map.get(key);
        if (currentNode == null) {
            Node newNode = new Node(key, value);
            if (size == 0) {
                head = tail = newNode;
            } else {
                Node lastNode = tail;
                lastNode.next = newNode;
                newNode.previous = lastNode;
                tail = newNode;
            }
            size++;
            map.put(key, newNode);
        } else {
            Node previous = currentNode.previous;
            Node next = currentNode.next;

            if (next == null) {
                currentNode.data = value;
                return;
            }

            if (previous == null) {
                next.previous = null;
                head = next;
            } else {
                previous.next = next;
                next.previous = previous;
            }
            currentNode.data = value;
            tail.next = currentNode;
            currentNode.previous = tail;
            currentNode.next = null;
            tail = currentNode;
            map.put(key, tail);
        }
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
        map.clear();
    }
}
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLinkedList {

    private Node m_head;
    private int m_size;
    private ReentrantLock m_mutex;

    public ConcurrentLinkedList() {
        // Constructor
        m_head = null;
        m_size = 0;
        m_mutex = new ReentrantLock();
    }

    public int removeHead() {
        // Remove the head of the linked list
        m_mutex.lock();

        if (m_head == null) {
            m_mutex.unlock();
            return Integer.MIN_VALUE;
        }

        int value = m_head.data;

        m_head = m_head.next;

        m_size--;
        m_mutex.unlock();
        return value;
    }

    public int size() {
        // Return size of the linked list
        return m_size;
    }

    public boolean empty() {
        // Return if the linked list is empty
        return m_head == null;
    }

    public void insert(int data) {
        // Insert data to linked list maintaining the ordering
        m_mutex.lock();

        Node newNode = new Node(data);

        if (m_head == null) {
            m_head = newNode;
            m_size++;
            m_mutex.unlock();
            return;
        }

        if (m_head.data >= newNode.data) {
            newNode.next = m_head;
            m_head = newNode;
            m_size++;
            m_mutex.unlock();
            return;
        }

        Node curr = m_head;

        while (curr.next != null && curr.next.data < newNode.data) {
            curr = curr.next;
        }

        newNode.next = curr.next;

        curr.next = newNode;
        m_size++;
        m_mutex.unlock();
    }

    public boolean contains(int data) {
        // Search the data inside the linked list
        m_mutex.lock();

        Node temp = m_head;

        while (temp != null) {
            if (temp.data == data) {
                m_mutex.unlock();
                return true;
            }

            temp = temp.next;
        }

        m_mutex.unlock();
        return false;
    }

    private class Node {
        // Helper node class for the linked list
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

}
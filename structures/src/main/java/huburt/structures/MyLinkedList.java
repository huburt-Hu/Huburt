package huburt.structures;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Created by hubert on 2018/4/27.
 *
 * 简版LinkedList，基于双向链表实现，并且确保了迭代期间修改时抛出ConcurrentModificationException
 */
public class MyLinkedList<T> {

    private int theSize;
    private int modCount = 0;//改变次数
    private Node<T> beginMarker;
    private Node<T> endMarker;

    public MyLinkedList() {
        doClear();
    }

    public void clear() {
        doClear();
    }

    private void doClear() {
        beginMarker = new Node<T>(null, null, null);
        endMarker = new Node<T>(null, beginMarker, null);
        beginMarker.next = endMarker;
        theSize = 0;
        modCount++;
    }

    public int size() {
        return theSize;
    }

    public boolean isEmpty() {
        return theSize == 0;
    }

    public boolean add(T t) {
        add(size(), t);
        return true;
    }

    public void add(int idx, T t) {
        addBefore(getNode(idx, 0, size()), t);
    }

    public T get(int idx) {
        return getNode(idx).data;
    }

    public T set(int idx, T newVal) {
        Node<T> node = getNode(idx);
        T old = node.data;
        node.data = newVal;
        return old;
    }

    public T remove(int idx) {
        return remove(getNode(idx));
    }

    private void addBefore(Node<T> p, T t) {
//        Node<T> newNode = new Node<>(t, p.prev, p);
//        newNode.prev.next = newNode;
//        p.prev = newNode;
        //上面三行可以合并成一行
        p.prev = p.prev.next = new Node<>(t, p.prev, p);

        theSize++;
        modCount++;
    }

    private T remove(Node<T> node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
        theSize--;
        modCount++;
        return node.data;
    }

    private Node<T> getNode(int idx) {
        return getNode(idx, 0, size() - 1);
    }

    private Node<T> getNode(int idx, int lower, int upper) {
        Node<T> node;
        if (idx < lower || idx > upper) {
            throw new IndexOutOfBoundsException();
        }
        if (idx < size() / 2) {
            node = beginMarker.next;
            for (int i = 0; i < idx; i++) {
                node = node.next;
            }
        } else {
            node = endMarker;
            for (int i = size(); i > idx; i--) {
                node = node.prev;
            }
        }
        return node;
    }

    public MyIterator<T> iterator() {
        return new Itr();
    }

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        public Node(T data, Node<T> prev, Node<T> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private class Itr implements MyIterator<T> {

        private Node<T> current = beginMarker.next;
        private int expectedModCount = modCount;//检测是否在迭代期间修改了元素
        private boolean okToRemove = false;

        @Override
        public boolean hasNext() {
            return current != endMarker;
        }

        @Override
        public T next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T data = current.data;
            current = current.next;
            okToRemove = true;
            return data;
        }

        @Override
        public void remove() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!okToRemove) {
                throw new IllegalStateException();
            }
            MyLinkedList.this.remove(current.prev);
            expectedModCount++;
            okToRemove = false;
        }
    }

}

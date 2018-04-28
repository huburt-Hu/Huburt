package huburt.structures;

import java.util.NoSuchElementException;

/**
 * Created by hubert on 2018/4/27.
 * <p>
 * 用单链表实现的Stack
 */
public class StackByLinked<T> implements MyStack<T> {

    private Node<T> endMarker;
    private Node<T> topNode;
    private int theSize;

    public StackByLinked() {
        doClear();
    }

    @Override
    public void clear() {
        doClear();
    }

    private void doClear() {
        endMarker = new Node<>(null, null);
        topNode = endMarker;
        theSize = 0;
    }

    @Override
    public int size() {
        return theSize;
    }

    @Override
    public void push(T t) {
        topNode = new Node<>(t, topNode);
        theSize++;
    }

    @Override
    public T pop() {
        if (topNode == endMarker) {
            throw new NoSuchElementException();
        }
        T data = topNode.data;
        topNode = topNode.next;
        theSize--;
        return data;
    }

    @Override
    public T top() {
        if (topNode == endMarker) {
            throw new NoSuchElementException();
        }
        return topNode.data;
    }


    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }
}

package huburt.structures;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by hubert on 2018/4/27.
 */
public class MyQueue<T> {

    private static final int DEFAULT_CAPACITY = 5;

    private T[] array;
    private int front;//队头
    private int back;//队尾
    private int size;

    public MyQueue() {
        doClear();
    }

    public void enqueue(T t) {
        ensureCapacity();
        if (front < 0) {
            front++;
        }
        array[++back] = t;
        size++;

        System.out.println("array:" + Arrays.toString(array));
        System.out.println("; front:" + front + "; back:" + back + "; size:" + size);
        System.out.println("; Queue:" + this.toString());
    }

    public T dequeue() {
        if (front < 0 || front > back) {//未放入元素 或 元素已经取完
            throw new NoSuchElementException();
        }
        T t = array[front];
        array[front] = null;
        front++;
        size--;

        System.out.println("array:" + Arrays.toString(array));
        System.out.println("; front:" + front + "; back:" + back + "; size:" + size);
        System.out.println("; Queue:" + this.toString());
        return t;
    }

    public void clear() {
        doClear();
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = front; i <= back; i++) {
            sb.append(array[i]);
            if (i != back) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void doClear() {
        size = 0;
        front = -1;
        back = -1;
        array = (T[]) new Object[DEFAULT_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (back == array.length - 1) {//队尾到达数组末端
            if (size == array.length) {//队列元素与数组元素相等，需要扩展数组
                T[] old = array;
                array = (T[]) new Object[array.length * 2 + 1];
                for (int i = 0; i < old.length; i++) {
                    array[i] = old[i];
                }
            } else {//数组前端有空缺，移动元素到起始
                for (int i = front; i <= back; i++) {
                    array[i - front] = array[i];
                    array[i] = null;
                }
                back = back - front;
                front = 0;
            }
        }
    }
}

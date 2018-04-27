package huburt.structures;

/**
 * Created by hubert on 2018/4/27.
 * <p>
 * 使用数组实现的Stack
 */
public class StackByArray<T> implements MyStack<T> {
    private static final int DEFAULT_CAPACITY = 10;

    private int theSize;
    private T[] theItems;

    public StackByArray() {
        doClear();
    }

    @Override
    public void clear() {
        doClear();
    }

    private void doClear() {
        theSize = 0;
        ensureCapacity(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity(int newCapacity) {
        if (newCapacity < theSize) {
            return;
        }
        T[] old = theItems;
        //无法使用 new T[]; ，因此new一个Object数组然后强转
        theItems = (T[]) new Object[newCapacity];
        for (int i = 0; i < theSize; i++) {
            theItems[i] = old[i];
        }
    }

    @Override
    public int size() {
        return theSize;
    }

    @Override
    public void push(T t) {
        if (theSize == theItems.length) {
            ensureCapacity(theSize * 2 + 1);
        }
        theItems[theSize] = t;
        theSize++;
    }

    @Override
    public T pop() {
        T t = theItems[theSize - 1];
        theItems[theSize - 1] = null;
        theSize--;
        return t;
    }

    @Override
    public T top() {
        return theItems[theSize - 1];
    }
}

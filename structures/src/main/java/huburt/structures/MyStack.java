package huburt.structures;

/**
 * Created by hubert on 2018/4/27.
 */
public interface MyStack<T> {
    void push(T t);

    T pop();

    T top();

    int size();

    void clear();
}

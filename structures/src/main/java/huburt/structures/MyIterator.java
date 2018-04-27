package huburt.structures;

/**
 * Created by hubert on 2018/4/27.
 */
public interface MyIterator<T> {
    boolean hasNext();

    T next();

    void remove();
}

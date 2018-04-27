package huburt.structures;

public class MyClass {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

//        MyStack<Integer> stack = new StackByArray<>();
//        for (int i = 0; i < 100000; i++) {
//            stack.push(i);
//        }
//        while (stack.size() > 0) {
//            stack.pop();
//        }

        MyQueue<Integer> queue = new MyQueue<>();
        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }
        while (queue.size() > 5) {
            System.out.println(queue.dequeue());
        }
        for (int i = 10; i < 20; i++) {
            queue.enqueue(i);
        }
        while (queue.size() > 0) {
            System.out.println(queue.dequeue());
        }

        long end = System.currentTimeMillis();
        System.out.println("use time:" + (end - start));

    }
}

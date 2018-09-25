package app.huburt.design.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by hubert on 2018/7/5.
 */
public class DynamicProxy {

    public static void main(String[] args) {
        Man man = new Man();
        Proxy.newProxyInstance(man.getClass().getClassLoader(), man.getClass().getInterfaces(),
                new MyInvocationHandler(man));
    }

    interface Runner {
        void run();
    }

    static class Man implements Runner {
        @Override
        public void run() {
            System.out.println("main running");
        }
    }

    static class MyInvocationHandler implements InvocationHandler {

        private Object object;

        public MyInvocationHandler(Object object) {
            this.object = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(object, args);
        }
    }


}

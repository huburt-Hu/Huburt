package com.huburt.app.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hubert on 2018/5/23.
 * <p>
 * 模拟刷新token，在同步刷新token的时候锁住获取token的方法，在刷新成功时释放锁，获取token的方法返回最新的token
 */
public class RefreshToken {
    public static void main(String[] args) {
        final RefreshToken refreshToken = new RefreshToken();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ":" + refreshToken.getNewToken());
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ":" + refreshToken.getNewToken());
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ":" + refreshToken.getNewToken());
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ":" + refreshToken.getNewToken());
            }
        });
    }

    private final Lock lock = new ReentrantLock();
    String token;
    long time;

    //模拟请求获取新token
    public String getNewToken() {
        lock.lock();
        try {
            if (time == 0) {
                time = System.currentTimeMillis();
                Thread.sleep(1000);
                return token = "1234";
            } else {
                return token;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }

    }
}

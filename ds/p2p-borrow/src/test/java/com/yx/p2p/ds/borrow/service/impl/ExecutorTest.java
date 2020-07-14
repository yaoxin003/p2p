package com.yx.p2p.ds.borrow.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: yx
 * @date: 2020/07/14/7:14
 */
public class ExecutorTest {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 30,
                6, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());


        for(int i=0; i<100; i++){
            threadPoolExecutor.execute( () -> {
                System.out.println("start===" + Thread.currentThread().getId() + "," + Thread.currentThread().getName());
                try {
                    Thread.currentThread().sleep(6 * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("end===" + Thread.currentThread().getId() + "," + Thread.currentThread().getName());
            });
        }
    }
}

package com.primeledger.higgs.test;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {


    @Test
    public void lockTest(){

    }

    public static void main(String args[]){
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        long start = System.currentTimeMillis();


        lock.tryLock();
        try {
            condition.await(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        lock.unlock();
        System.out.println((end-start)/1000);
    }
}

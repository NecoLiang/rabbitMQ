package com.atyiche.rabbit.utils;

/**
 * @author liangyt
 * @create 2021-06-22 16:54
 */
public class SleepUtils {
    public static void sleep(int second){
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}

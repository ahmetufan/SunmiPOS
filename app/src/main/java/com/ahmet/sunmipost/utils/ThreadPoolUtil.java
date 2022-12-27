package com.ahmet.sunmipost.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Konu havuzu araçları
 */

public final class ThreadPoolUtil {

    private ExecutorService cachePool = Executors.newCachedThreadPool();

    private ExecutorService singlePool = Executors.newSingleThreadExecutor();

    private static final class SingletonHolder {
        private static final ThreadPoolUtil INSTANCE = new ThreadPoolUtil();
    }

    private ThreadPoolUtil() {
    }

    public static void executeInSinglePool(Runnable r) {
        if (r != null) {
            SingletonHolder.INSTANCE.singlePool.execute(r);
        }
    }

    public static void executeInCachePool(Runnable r) {
        if (r != null) {
            SingletonHolder.INSTANCE.cachePool.execute(r);
        }
    }

}

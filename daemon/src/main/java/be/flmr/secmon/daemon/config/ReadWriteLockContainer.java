package be.flmr.secmon.daemon.config;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReadWriteLockContainer {
    public static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}

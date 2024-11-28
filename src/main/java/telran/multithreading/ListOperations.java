package telran.multithreading;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ListOperations extends Thread {
    private ModelData modelData;
    private List<Integer> list;
    private Random random = new Random();
    private static ReentrantReadWriteLock rw_lock = new ReentrantReadWriteLock(false);
    private static Lock readlock = rw_lock.readLock();
    private static Lock writeLock = rw_lock.writeLock();
    private static AtomicLong syncCounter = new AtomicLong();

    public ListOperations(ModelData modelData, List<Integer> list) {
        this.modelData = modelData;
        this.list = list;
    }

    @Override
    public void run() {
        int nRuns = modelData.nRuns();
        int updateProb = modelData.updateProb();
        for (int i = 0; i < nRuns; i++) {
            if (chance(updateProb)) {
                update();
            } else {
                read();
            }
        }
    }

    private void read() {
        try {
            tryLock(readlock);
            int nReads = modelData.nReadOperations();
            for (int i = 0; i < nReads; i++) {
                list.get(list.size() - 1);
            }
        } finally {
            readlock.unlock();
        }
    }

    private void update() {
        try {
            tryLock(writeLock);
            int nUpdates = modelData.nUpdateOperations();
            for (int i = 0; i < nUpdates; i++) {
                list.remove(0);
                list.add(100);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private boolean chance(int updateProb) {
        int num = random.nextInt(0, 100);
        return num < updateProb;
    }

    private void tryLock(Lock lock) {
        while (!lock.tryLock()) {
            syncCounter.incrementAndGet();
        }
    }

    public static long getSyncCounter() {
        return syncCounter.get();
    }
}

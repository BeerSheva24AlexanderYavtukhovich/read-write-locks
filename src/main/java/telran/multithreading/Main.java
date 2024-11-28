package telran.multithreading;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static final int N_THREADS = 100;
    private static final int UPDATE_PROB = 50;
    private static final int N_RUNS = 1000;
    private static final int N_READ_OPERATIONS = 1000;
    private static final int N_UPDATE_OPERATIONS = 10;
    private static final int LIST_SIZE = 1000;

    public static void main(String[] args) {
        ListOperations[] threads = new ListOperations[N_THREADS];
        List<Integer> list = getList();
        ModelData modelData = new ModelData(UPDATE_PROB, N_RUNS, N_READ_OPERATIONS, N_UPDATE_OPERATIONS);
        startThreads(threads, list, modelData);
        joinThreads(threads);
        System.out.println("The value of sync counter is " + ListOperations.getSyncCounter());
    }

    private static void joinThreads(ListOperations[] threads) {
        Arrays.stream(threads).forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        });
    }

    private static List<Integer> getList() {
        List<Integer> list = new LinkedList<>();
        IntStream.range(0, LIST_SIZE).forEach(i -> list.add(i));
        return list;
    }

    private static void startThreads(ListOperations[] threads, List<Integer> list, ModelData modelData) {
        for (int i = 0; i < threads.length; i++) {
            ListOperations listOperations = new ListOperations(modelData, list);
            threads[i] = listOperations;
            listOperations.start();
        }
    }

}
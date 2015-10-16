package com.sourceforgery.nongui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class SelfTerminatingExecutorService extends AbstractExecutorService {
    private volatile boolean shutdown;
    private BlockingQueue<Runnable> blockingQueue;
    private Set<Runnable> runningTasks = new HashSet<>();
    private Set<Thread> threads = new HashSet<>();
    private volatile int idleThreads;
    private Object lock = new Object();

    private final int maxThreads;
    private final int threadTimeout;

    public SelfTerminatingExecutorService(int maxQueueLength, int maxThreads, int threadTimeout) {
        this.maxThreads = maxThreads;
        this.threadTimeout = threadTimeout;
        if (maxQueueLength <= 0) {
            blockingQueue = new LinkedBlockingQueue<>();
        } else
            blockingQueue = new LinkedBlockingQueue<>(maxQueueLength);
    }


    public SelfTerminatingExecutorService() {
        this(0, 10, 10000);
    }


    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        ArrayList<Runnable> futureTasks = new ArrayList<>();
        synchronized (lock) {
            futureTasks.addAll(blockingQueue);
            blockingQueue.clear();
            for (Thread t : threads) {
                t.interrupt();
            }
        }
        return futureTasks;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        synchronized (lock) {
            return runningTasks.isEmpty() && blockingQueue.isEmpty() && isShutdown();
        }
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        synchronized (threads) {
            long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
            long waitTime = deadline - System.currentTimeMillis();
            while (!isTerminated() && waitTime > 0) {
                threads.wait(waitTime);
                waitTime = deadline - System.currentTimeMillis();
            }
        }
        return isTerminated();
    }

    @Override
    public void execute(Runnable command) {
        blockingQueue.add(command);
        synchronized (threads) {
            if (idleThreads == 0 && threads.size() < maxThreads) {
                ExecutorThread executorThread = new ExecutorThread();
                threads.add(executorThread);
                executorThread.start();
            }
        }
    }

    private class ExecutorThread extends Thread {
        @Override
        public void run() {
            boolean didSomething;
            try {
                do {
                    didSomething = false;
                    try {
                        Runnable poll;
                        idleThreads++;
                        try {
                            poll = blockingQueue.poll(threadTimeout, MILLISECONDS);
                        } finally {
                            idleThreads--;
                        }
                        if (poll != null) {
                            didSomething = true;
                            poll.run();
                        }
                    } catch (InterruptedException e) {
                        // Normal when shutting down
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                } while (didSomething && !shutdown);
            } finally {
                synchronized (threads) {
                    threads.remove(this);
                    threads.notifyAll();
                }
            }
        }
    }
}

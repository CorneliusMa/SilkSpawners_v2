package de.corneliusmay.silkspawners.plugin.utils;

import java.time.Duration;

public class Schedule {

    @FunctionalInterface
    public interface Task {
        void run() throws InterruptedException;
    }

    private final Duration interval;

    private final Thread thread;

    public Schedule(String name, Duration interval, Task task) {
        this.interval = interval;
        thread = new Thread(() -> run(task), name);
        thread.setDaemon(true);
        thread.start();
    }

    public boolean isRunning(Duration interval) {
        return thread.isAlive() && this.interval.equals(interval);
    }

    public void stop() {
        thread.interrupt();
    }

    private void run(Task task) {
        try {
            while (true) {
                task.run();
                Thread.sleep(interval.toMillis());
            }
        } catch (InterruptedException ignored) {
        }
    }
}

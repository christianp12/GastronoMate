package it.unipi.lsmd.gastronomate.service.interfaces;

import it.unipi.lsmd.gastronomate.service.interfaces.Task;
import lombok.Getter;
import lombok.NonNull;

import java.util.Comparator;
import java.util.concurrent.*;

/*
     This class is used to manage tasks  
 */
public abstract class TaskManager {
    @Getter
    private PriorityBlockingQueue<Task> taskQueue;

    /*
        Comparator used to compare tasks by priority
        The creation Tasks have the highest priority, then the update tasks and finally the delete tasks
        If two tasks have the same priority, the one that was created first is executed first

     */
    private static final Comparator<Task> taskComparator = (o1, o2) -> {
        if(o1.getPriority() > o2.getPriority())
            return 1;
        else if(o1.getPriority() < o2.getPriority())
            return -1;
        else
            return Long.compare(o1.getTimestamp(), o2.getTimestamp()); //it is very unlikely that two tasks have the same timestamp
    };

    public TaskManager() {
        this.taskQueue = new PriorityBlockingQueue<>(20, taskComparator);
    }

    /*
        Starts the task manager
     */
    public abstract void start();

    /*
        Stops the task manager
     */
    public abstract void stop();

    /*
        Adds a task to the task queue
     */
    public synchronized void addTask(@NonNull Task task) {
        taskQueue.put(task);
    }

}

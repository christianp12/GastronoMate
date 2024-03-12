package it.unipi.lsmd.gastronomate.service.interfaces;

public interface ExecutorTaskService {

    public void executeTask(Task task);

    public void start();

    public void stop();

}

package it.unipi.lsmd.gastronomate.service.interfaces;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public abstract class Task implements Serializable {

   private static final int DEFAULT_PRIORITY = 5;
   @Getter
   private static final int MAX_RETRIES = 3;

   @Getter
   @Setter
   private int priority;

   @Getter
   private long timestamp;

   @Getter
   private int retries;

   public Task(int priority) {

       if(priority < 0 || priority > 10)
           this.priority = DEFAULT_PRIORITY;
       else
           this.priority = priority;

       this.retries = 0;
       this.timestamp = System.currentTimeMillis(); //timestamp in milliseconds
   }

   public synchronized void incrementRetries() {
      retries++;
   }

    public abstract void executeJob() throws Exception;

}

package com.authrus.common.task;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.thread.ThreadPool;

public class TaskScheduler implements Executor {   

   private static final Logger LOG = LoggerFactory.getLogger(TaskScheduler.class);
   
   private final ThreadPool pool;
   
   public TaskScheduler(ThreadPool pool) {
      this.pool = pool;
   }
   
   @Override
   public void execute(Runnable task) {
      pool.execute(task);
   }

   public void scheduleTask(Task task) {
      scheduleTask(task, 0);      
   }
   
   public void scheduleTask(Task task, long delay) {
      TaskRunnable adapter = new TaskRunnable(task);
      
      if(delay > 0) {
         pool.schedule(adapter, delay, TimeUnit.MILLISECONDS);
      } else {
         pool.execute(adapter);
      }
   }

   public void scheduleRunnable(Runnable task) {
      scheduleRunnable(task, 0);
   }
   
   public void scheduleRunnables(Runnable... tasks) {
      scheduleRunnable(new TaskList(tasks), 0);
   }
   
   public void scheduleRunnable(Runnable task, long delay) {
      if(delay > 0) {
         pool.schedule(task, delay, TimeUnit.MILLISECONDS);
      } else {
         pool.execute(task);
      }
   }
   
   public class TaskList implements Runnable {
      
      private final Runnable[] tasks;
      
      public TaskList(Runnable... tasks) {
         this.tasks = tasks;
      }
      
      @Override
      public void run() {
         try {
            for(int i = 0; i < tasks.length; i++) {
               if(tasks[i] != null) {
                  tasks[i].run();
               }
            }
         } catch(Exception e) {
            LOG.info("Could not execute task", e);
         }
      }
   }
   
   private class TaskRunnable implements Runnable {
      
      private final Task task;
      
      public TaskRunnable(Task task) {
         this.task = task;
      }
      
      public void run() {
         try {
            long repeat = task.executeTask();
            
            if(repeat > 0) {
               pool.schedule(this, repeat, TimeUnit.MILLISECONDS);
            }
         } catch(Exception e) {
            LOG.info("Could not execute task", e);
         }
      }
      
   }
}

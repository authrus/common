package com.authrus.common.task;

import static java.util.Collections.EMPTY_LIST;

import java.util.List;

public class TaskRegistry {

   private final TaskScheduler scheduler;
   private final List<Task> tasks;
   
   public TaskRegistry(TaskScheduler scheduler) {
      this(scheduler, EMPTY_LIST);
   }
   
   public TaskRegistry(TaskScheduler scheduler, List<Task> tasks) {
      this.scheduler = scheduler;
      this.tasks = tasks;
   }
   
   public void register() {
      for(Task task : tasks) {
         scheduler.scheduleTask(task, 0);
      }
   }
}

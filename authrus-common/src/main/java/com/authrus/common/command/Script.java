package com.authrus.common.command;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class Script implements Command {

   private final CommandParser command;
   private final String original;
   private final File directory;
   private final boolean redirect;
   private final boolean wait;
   private final long duration;

   public Script(String command, File directory) {
      this(command, directory, false);
   }

   public Script(String command, File directory, boolean redirect) {
      this(command, directory, redirect, true);
   }

   public Script(String command, File directory, boolean redirect, boolean wait) {
      this(command, directory, redirect, wait, -1);
   }
   
   public Script(String command, File directory, boolean redirect, boolean wait, long duration) {
      this.command = new CommandParser(command);
      this.directory = directory;
      this.duration = duration;
      this.original = command;
      this.redirect = redirect;
      this.wait = wait;
   }

   @Override
   public Console execute(Environment env) throws Exception {
      File path = directory.getCanonicalFile();

      if (!path.exists()) {
         throw new CommandException("Script directory '" + path + "' does not exist for " + original);
      }
      if (!path.isDirectory()) {
         throw new CommandException("Script directory '" + path + "' is not a directory for " + original);
      }
      return execute(env, path);
   }

   private Console execute(Environment env, File path) throws Exception {
      try {
         List<String> tokens = command.command();
         ProcessBuilder builder = env.createProcess(tokens);

         if (path != null) {
            builder.directory(path);
         }
         if (redirect) {
            builder.redirectErrorStream(true);
         }
         Process process = builder.start();
         InputStream input = process.getInputStream();

         if (wait) {
            if(duration > 0) {
               process.waitFor(duration, MILLISECONDS);
            } else {
               process.waitFor();
            }
         }
         return new InputStreamConsole(input, original);
      } catch (Exception e) {
         throw new CommandException("Error executing " + original + " in directory " + path, e);
      }
   }
}

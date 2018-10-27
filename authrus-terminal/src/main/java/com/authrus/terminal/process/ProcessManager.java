package com.authrus.terminal.process;

import java.io.File;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;

import com.zuooh.common.command.Console;
import com.zuooh.common.command.Environment;
import com.zuooh.common.command.Script;

@Builder
@AllArgsConstructor
public class ProcessManager {

   private final Environment environment;
   private final Process process;
   private final TailParser parser;
   
   public String getName() {
      return process.getName();
   }
   
   public File getDirectory() {
      return process.getDirectory();
   }
   
   @SneakyThrows
   public LogResult start() {
      Script script = process.start();
      Console console = script.execute(environment);
      String log = console.readAll();
      String name = process.getName();
      
      return LogResult.builder()
            .name(name)
            .log(log)
            .build();
   }
   
   @SneakyThrows
   public LogResult stop() {
      Script script = process.stop();
      Console console = script.execute(environment);
      String log = console.readAll();
      String name = process.getName();
      
      return LogResult.builder()
            .name(name)
            .log(log)
            .build();
   }
   
   @SneakyThrows
   public ImageResult image() {
      Script script = process.image();
      Console console = script.execute(environment);
      String image = console.readAll();
      String name = process.getName();
      
      return ImageResult.builder()
            .name(name)
            .image(image)
            .build();
   }
   
   @SneakyThrows
   public TailResult tail() {
      Script script = process.tail();
      Console console = script.execute(environment);
      String tail = console.readAll();
      String name = process.getName();
      Map<String, String> files = parser.parse(tail);
      
      return TailResult.builder()
            .files(files)
            .name(name)
            .build();
   }
   
}

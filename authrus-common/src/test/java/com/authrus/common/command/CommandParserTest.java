package com.authrus.common.command;

import java.util.List;

import junit.framework.TestCase;

import com.authrus.common.command.CommandParser;

public class CommandParserTest extends TestCase {

   public void testParser() {
      CommandParser parser = new CommandParser("ps -eaf | grep 'some text' | wc -l");
      List<String> command = parser.command();

      assertEquals(command.get(0), "ps");
      assertEquals(command.get(1), "-eaf");
      assertEquals(command.get(2), "|");
      assertEquals(command.get(3), "grep");
      assertEquals(command.get(4), "some text");
      assertEquals(command.get(5), "|");
      assertEquals(command.get(6), "wc");
      assertEquals(command.get(7), "-l");
   }

}

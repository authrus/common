package com.authrus.terminal.service;

public interface TerminalListener {
   void onTerminalInit();
   void onTerminalReady();
   void onTerminalCommand(String command) throws InterruptedException;
   void onTerminalResize(String columns, String rows);
   void onTerminalClose();
}

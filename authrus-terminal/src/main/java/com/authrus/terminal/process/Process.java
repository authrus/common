package com.authrus.terminal.process;

import java.io.File;

import com.zuooh.common.command.Script;

public interface Process {
   File getDirectory();
   String getName();
   String getHost();
   Script start();
   Script stop();
   Script tail();
   Script image();
}

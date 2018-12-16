package com.authrus.common.command;

import com.authrus.common.command.Console;

public interface Command {
   Console execute(Environment env) throws Exception;
}

package com.authrus.common.command;

import java.io.IOException;

public interface Console {
   String readAll() throws IOException;
   String readLine() throws IOException;
}

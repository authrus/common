package com.authrus.terminal;

import com.authrus.rest.EnableResourceServer;
import com.authrus.terminal.zookeeper.EnableZooKeeperServer;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@EnableResourceServer
@EnableZooKeeperServer
@SpringBootApplication
public class TerminalApplication {

	public static void main(String[] list) {
      SpringApplicationBuilder builder = new SpringApplicationBuilder(TerminalApplication.class);
      builder.web(WebApplicationType.NONE).run(list);
	}
}

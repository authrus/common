package com.authrus.gateway.deploy.trace;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.zuooh.http.proxy.trace.TraceCollector;
import com.zuooh.http.proxy.trace.TraceEvent;

@Slf4j
@AllArgsConstructor
public class TraceDistributor implements TraceCollector {

	private final Optional<List<TraceCollector>> collectors;
	
	@Override
	public void collect(TraceEvent event) {
		collectors.orElse(Collections.EMPTY_LIST)
			.stream()
			.forEach(collector -> {
				try {
					collector.collect(event);
				} catch(Exception e) {
					log.info("Could not dispatch event", e);
				}
			});
	}

}

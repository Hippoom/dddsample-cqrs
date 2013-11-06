package com.github.hippoom.dddsample.cargocqrs.config;

import lombok.Getter;
import lombok.Setter;

@Setter
public class Configurations {
	private String pathfinderSchema;
	private String pathfinderHost;
	@Getter
	private int pathfinderPort;
	private String pathfinderContextPath;
	private String pathfinderRoutesPath;

	public String getPathFInderRoutesUrl() {
		return pathfinderSchema + pathfinderHost + ":" + pathfinderPort
				+ getPathFInderRoutesPath();
	}

	public String getPathFInderRoutesPath() {
		return pathfinderContextPath + pathfinderRoutesPath;
	}
}

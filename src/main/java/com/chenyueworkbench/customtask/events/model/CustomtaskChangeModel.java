package com.chenyueworkbench.customtask.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CustomtaskChangeModel {
	private String type;
	private String action;
	private String customtaskId;
	private String correlationId;
	private String starterUserId;

	public CustomtaskChangeModel(String type, String action, String customtaskId, String correlationId, String starterUserId) {
		super();
		this.type = type;
		this.action = action;
		this.customtaskId = customtaskId;
		this.correlationId = correlationId;
		this.starterUserId = starterUserId;
	}
}

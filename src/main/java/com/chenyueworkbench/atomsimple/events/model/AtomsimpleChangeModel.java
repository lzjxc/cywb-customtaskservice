package com.chenyueworkbench.atomsimple.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AtomsimpleChangeModel {
	private String type;
	private String action;
	private String atomsimpleId;
	private String correlationId;
	private String starterUserId;

	public AtomsimpleChangeModel(String type, String action, String atomsimpleId, String correlationId, String starterUserId) {
		super();
		this.type = type;
		this.action = action;
		this.atomsimpleId = atomsimpleId;
		this.correlationId = correlationId;
		this.starterUserId = starterUserId;
	}
}

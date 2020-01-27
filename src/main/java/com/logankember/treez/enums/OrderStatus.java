package com.logankember.treez.enums;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
	PendingConfirmation("PendingConfirmation"),
	Confirmed("Confirmed"),
	OutForDelivery("OutForDelivery"),
	Delivered("Delivered");
	
	private String status;
	
	private OrderStatus(String status) {
		this.status = status;
	}
	
	@JsonValue
	public String jsonValue() {
		return this.status;
	}
}
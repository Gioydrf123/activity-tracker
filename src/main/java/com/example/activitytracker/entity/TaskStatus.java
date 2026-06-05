package com.example.activitytracker.entity;

public enum TaskStatus {
	BACKLOG("#808080"), // grigio
	IN_PROGRESS("#1E90FF"), // blu
	COMPLETED("#32CD32"); // verde

	private final String color;

	TaskStatus(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}
}

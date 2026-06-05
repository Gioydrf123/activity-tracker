package com.example.activitytracker.dto;

public class CreateTaskRequest {

	private String title;
	private String description;
	
	// getters e setters
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}

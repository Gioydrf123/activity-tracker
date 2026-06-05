package com.example.activitytracker.dto;

import com.example.activitytracker.entity.TaskStatus;

public class ChangeStatusRequest {

	private TaskStatus status;
	
	public TaskStatus getStatus() {
		return status;
	}
	
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
}

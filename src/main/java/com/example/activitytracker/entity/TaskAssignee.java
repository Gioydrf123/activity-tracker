package com.example.activitytracker.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "task_assignees")
@IdClass(TaskAssignee.TaskAssigneeId.class)
public class TaskAssignee {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	private Task task;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public TaskAssignee() {}

	public TaskAssignee(Task task, User user) {
		this.task = task;
		this.user = user;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof TaskAssignee))
		{
			return false;
		}
		TaskAssignee that = (TaskAssignee) o;
		return Objects.equals(task, that.task) && Objects.equals(user, that.user);
	}

	@Override
	public int hashCode() {
		return Objects.hash(task, user);
	}

	public static class TaskAssigneeId implements Serializable {

		private Long task;
		private Long user;

		public TaskAssigneeId() {}

		public TaskAssigneeId(Long task, Long user) {
			this.task = task;
			this.user = user;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof TaskAssigneeId)) {
				return false;
			}
			TaskAssigneeId that = (TaskAssigneeId) o;
			return Objects.equals(task, that.task) && Objects.equals(user, that.user);
		}

		@Override
		public int hashCode() {
			return Objects.hash(task, user);
		}
	}
}

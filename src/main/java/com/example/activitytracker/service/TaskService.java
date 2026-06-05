package com.example.activitytracker.service;

import com.example.activitytracker.dto.*;
import com.example.activitytracker.entity.*;
import com.example.activitytracker.repository.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	private final WorkLogRepository workLogRepository;
	private final CommentRepository commentRepository;

	// Iniezione via costruttore (consigliata)
	public TaskService(TaskRepository taskRepository, UserRepository userRepository, WorkLogRepository workLogRepository, CommentRepository commentRepository) {
		this.taskRepository = taskRepository;
		this.userRepository = userRepository;
		this.workLogRepository = workLogRepository;
		this.commentRepository = commentRepository;
	}

       // Crea un nuovo task in stato BACKLOG e lo assegna automaticamente al creatore.

	public Task createTask(String title, String description, User creator) {
		Task task = new Task();
		task.setTitle(title);
		task.setDescription(description);
		task.setCreatedBy(creator);
		task.setStatus(TaskStatus.BACKLOG);
		task = taskRepository.save(task);
		
		// Assegna automaticamente al creatore
		assignUserToTask(task, creator);
		return task;
	}

	// Cambia lo stato di un task rispettando le transizioni consentite.
 
	public void changeStatus(Long taskId, TaskStatus newStatus) {
		Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task non trovato con id: " + taskId));
		
		TaskStatus currentStatus = task.getStatus();
		
		switch (newStatus) {
			case IN_PROGRESS:
				if (currentStatus != TaskStatus.BACKLOG && currentStatus != TaskStatus.COMPLETED) {
					throw new IllegalStateException("Transizione non consentita: da " + currentStatus + " a " + newStatus);
				}
				break;
			case COMPLETED:
				if (currentStatus != TaskStatus.IN_PROGRESS) {
				  throw new IllegalStateException("Transizione non consentita: solo da IN_PROGRESS a COMPLETED");
				}
				break;
			case BACKLOG:
				// Non è mai permesso tornare a BACKLOG
				throw new IllegalStateException("Non è possibile tornare allo stato BACKLOG");
			default:
				throw new IllegalArgumentException("Stato non riconosciuto: " + newStatus);
		}
		
		task.setStatus(newStatus);
		// Il salvataggio avviene automaticamente grazie a @Transactional
	}

	// Assegna un utente a un task (versione pubblica con ID).
	
	 public void assignUserToTask(Long taskId, Long userId) {
		Task task = taskRepository
			.findById(taskId)
			.orElseThrow(() ->
			  new RuntimeException("Task non trovato con id: " + taskId)
			);

		User user = userRepository
			.findById(userId)
			.orElseThrow(() ->
				new RuntimeException("Utente non trovato con id: " + userId)
			);
		assignUserToTask(task, user);
	}

  // Assegna un utente a un task (versione privata con entità).

	private void assignUserToTask(Task task, User user) {
		TaskAssignee assignee = new TaskAssignee(task, user);

		if (!task.getAssignees().contains(assignee)) {
			task.getAssignees().add(assignee);
		}
	}

	// Registra ore lavorate su un task da parte di un utente.
	
	public void logWork(Long taskId, Double hours, User user) {
		Task task = taskRepository
			.findById(taskId)
			.orElseThrow(() ->
				new RuntimeException("Task non trovato con id: " + taskId)
			);

		WorkLog log = new WorkLog();
		log.setTask(task);
		log.setUser(user);
		log.setHours(hours);
		workLogRepository.save(log);
	}

	// Aggiunge un commento a un task e lo restituisce.
	
	public Comment addComment(Long taskId, String text, User user) {
		Task task = taskRepository
			.findById(taskId)
			.orElseThrow(() ->
				new RuntimeException("Task non trovato con id: " + taskId)
			);

		Comment comment = new Comment();
		comment.setText(text);
		comment.setTask(task);
		comment.setUser(user);

		return commentRepository.save(comment);
	}

  // ------------------------------------------------------
  // Metodi di lettura con DTO
  // ------------------------------------------------------

	@Transactional(readOnly = true)
	public List<TaskSummaryDto> getTasksForUserByStatus(User user, TaskStatus status) {
		List<Task> tasks;
		if (status != null) {
			tasks = taskRepository.findByAssigneeAndStatus(user.getId(), status);
		} else {
			tasks = taskRepository.findByAssignee(user.getId());
		}
		return tasks.stream().map(this::toSummary).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public TaskDetailDto getTaskDetail(Long taskId) {
		Task task = taskRepository
			.findById(taskId)
			.orElseThrow(() ->
				new RuntimeException("Task non trovato con id: " + taskId)
			);

		Double totalHours = workLogRepository.getTotalHoursByTask(taskId);
		List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(
			taskId
		);

		return toDetail(task, totalHours, comments);
	}

  // ------------------------------------------------------
  // Mapping verso DTO
  // ------------------------------------------------------

	private TaskSummaryDto toSummary(Task t) {
		Double totalHours = workLogRepository.getTotalHoursByTask(t.getId());
		List<String> assigneeNames = t
			.getAssignees()
			.stream()
			.map(a ->
				a.getUser().getFullName() != null
					? a.getUser().getFullName()
					: a.getUser().getUsername()
			)
			.collect(Collectors.toList());
		
		return new TaskSummaryDto(
			t.getId(),
			t.getTitle(),
			t.getStatus().name(),
			t.getStatus().getColor(),
			totalHours,
			assigneeNames
		);
	}

	private TaskDetailDto toDetail(
		Task t,
		Double totalHours,
		List<Comment> comments
	) {
		TaskSummaryDto summary = toSummary(t); // riusa il mapping di base
		
		List<CommentDto> commentDtos = comments
			.stream()
			.map(c ->
				new CommentDto(
					c.getId(),
					c.getText(),
					c.getUser().getUsername(),
					c.getCreatedAt()
				)
			)
		  .collect(Collectors.toList());
		
		return new TaskDetailDto(
			summary.getId(),
			summary.getTitle(),
			summary.getStatus(),
			summary.getStatusColor(),
			totalHours,
			summary.getAssigneeNames(),
			t.getDescription(),
			t.getCreatedBy() != null ? t.getCreatedBy().getUsername() : null,
			commentDtos
		);
	}
}

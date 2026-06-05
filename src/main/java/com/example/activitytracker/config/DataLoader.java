package com.example.activitytracker.config;

import com.example.activitytracker.entity.*;
import com.example.activitytracker.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

//pacchetti scritti per testare e vedere dati nel database

@Configuration
public class DataLoader {

	@Bean
	public CommandLineRunner initData(
		UserRepository userRepository,
		TaskRepository taskRepository,
		WorkLogRepository workLogRepository,
		CommentRepository commentRepository,
		PasswordEncoder passwordEncoder
	) {
		return args -> {
			if (userRepository.findByUsername("mario").isEmpty()) {
				System.out.println(
					" [DATA LOADER] Iniezione dati di test in corso..."
				);
				
				// 1. Inserimento Utenti
				User mario = new User(
					"mario",
					passwordEncoder.encode("password"),
					"Mario Rossi"
				);
				User anna = new User(
					"anna",
					passwordEncoder.encode("password"),
					"Anna Bianchi"
				);
				mario = userRepository.save(mario);
				anna = userRepository.save(anna);
				
				// 2. Inserimento Task (SALVATAGGIO IN DUE FASI PER EVITARE CRASH HIBERNATE)
				Task t1 = new Task();
				t1.setTitle("Task 1");
				t1.setDescription("Primo task di esempio");
				t1.setStatus(TaskStatus.BACKLOG);
				t1.setCreatedBy(mario);
				t1 = taskRepository.save(t1); // PRIMA SALVA (GENERA ID)
				t1.getAssignees().add(new TaskAssignee(t1, mario)); // POI ASSEGNA
				taskRepository.save(t1); // AGGIORNA
				
				Task t2 = new Task();
				t2.setTitle("Task 2");
				t2.setDescription("Secondo task");
				t2.setStatus(TaskStatus.IN_PROGRESS);
				t2.setCreatedBy(mario);
				t2 = taskRepository.save(t2); // PRIMA SALVA
				t2.getAssignees().add(new TaskAssignee(t2, mario));
				t2.getAssignees().add(new TaskAssignee(t2, anna));
				taskRepository.save(t2); // AGGIORNA
				
				Task t3 = new Task();
				t3.setTitle("Task 3");
				t3.setDescription("Task completato");
				t3.setStatus(TaskStatus.COMPLETED);
				t3.setCreatedBy(anna);
				t3 = taskRepository.save(t3); // PRIMA SALVA
				t3.getAssignees().add(new TaskAssignee(t3, anna));
				taskRepository.save(t3); // AGGIORNA
				
				// 3. Inserimento Ore Lavorate
				WorkLog wl1 = new WorkLog();
				wl1.setTask(t2);
				wl1.setUser(mario);
				wl1.setHours(2.5);
				workLogRepository.save(wl1);
				
				WorkLog wl2 = new WorkLog();
				wl2.setTask(t3);
				wl2.setUser(anna);
				wl2.setHours(1.0);
				workLogRepository.save(wl2);
				
				// 4. Inserimento Commenti
				Comment c1 = new Comment();
				c1.setTask(t2);
				c1.setUser(mario);
				c1.setText("Iniziato sviluppo");
				commentRepository.save(c1);

				Comment c2 = new Comment();
				c2.setTask(t3);
				c2.setUser(anna);
				c2.setText("Completato con successo");
				commentRepository.save(c2);

				System.out.println("✅ [DATA LOADER] Dati inseriti con successo!");
			}
		};
	}
}

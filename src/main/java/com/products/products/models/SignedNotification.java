package com.products.products.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SignedNotification {
	
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		private String username;          // who receives it (cashier)
	    private String message;       // notification text
	    private String pdfPath;       // path to signed PDF
   
	    private boolean isRead= false; // whether the notification has been read
	    private LocalDateTime createdAt = LocalDateTime.now();
		
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getPdfPath() {
			return pdfPath;
		}
		public void setPdfPath(String pdfPath) {
			this.pdfPath = pdfPath;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public boolean isRead() {
			return isRead;
		}
		public void setRead(boolean isRead) {
			this.isRead = isRead;
		}

	    // getters and setters
	}



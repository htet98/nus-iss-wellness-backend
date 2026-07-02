package nus.iss.wellness.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ChatSession session;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime createdAt;

    public ChatMessage() {
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public ChatSession getSession() {
        return session;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ChatMessage(Long messageId, ChatSession session, String sender, String message, LocalDateTime createdAt) {
        super();
        this.messageId = messageId;
        this.session = session;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }

    public ChatMessage(ChatSession session, String sender, String message, LocalDateTime createdAt) {
        super();
        this.session = session;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }

}

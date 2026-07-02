package nus.iss.wellness.backend.dto.response;

import nus.iss.wellness.backend.model.ChatSession;
import java.time.LocalDateTime;
/**
 *  Author: Htet Nandar
 */
public class ChatSessionResponse {

    private Long id;
    private Long userId;
    private String title;
    private boolean active;
    private LocalDateTime createdAt;

    public static ChatSessionResponse from(ChatSession session) {
        ChatSessionResponse r = new ChatSessionResponse();
        r.id        = session.getId();
        r.userId    = session.getUser().getUserId();
        r.title     = session.getTitle();
        r.active    = session.isActive();
        r.createdAt = session.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

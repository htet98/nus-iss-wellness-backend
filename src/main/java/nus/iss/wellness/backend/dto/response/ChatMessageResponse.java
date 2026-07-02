package nus.iss.wellness.backend.dto.response;

import nus.iss.wellness.backend.model.ChatMessage;
import java.time.LocalDateTime;
/**
 *  Author: Htet Nandar
 */
public class ChatMessageResponse {

    private Long id;
    private Long sessionId;
    private String senderRole;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage msg) {
        ChatMessageResponse r = new ChatMessageResponse();
        r.id         = msg.getId();
        r.sessionId  = msg.getSession().getId();
        r.senderRole = msg.getSenderRole().name();
        r.content    = msg.getContent();
        r.createdAt  = msg.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getSessionId() { return sessionId; }
    public String getSenderRole() { return senderRole; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

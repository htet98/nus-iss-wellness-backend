package nus.iss.wellness.backend.dto.response;

import java.time.LocalDateTime;
/**
 *  Author: Htet Nandar
 */
public class ChatResponse {

    private Long sessionId;
    private Long messageId;       // ID of the saved assistant message
    private String reply;
    private LocalDateTime timestamp;

    public ChatResponse(Long sessionId, Long messageId, String reply, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.messageId = messageId;
        this.reply     = reply;
        this.timestamp = timestamp;
    }

    public Long getSessionId() { return sessionId; }
    public Long getMessageId() { return messageId; }
    public String getReply() { return reply; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

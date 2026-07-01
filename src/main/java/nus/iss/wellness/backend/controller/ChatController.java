package nus.iss.wellness.backend.controller;

import jakarta.validation.Valid;
import nus.iss.wellness.backend.dto.request.ChatRequest;
import nus.iss.wellness.backend.dto.response.ChatMessageResponse;
import nus.iss.wellness.backend.dto.response.ChatResponse;
import nus.iss.wellness.backend.dto.response.ChatSessionResponse;
import nus.iss.wellness.backend.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  Author: Htet Nandar
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // ── Sessions ───────────────────────────────────────────────────────────

    /**
     * POST /api/chat/sessions?title=My+Chat
     * title is optional — defaults to "New Chat" if omitted.
     */
    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionResponse> createSession(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "New Chat") String title) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.createSession(userId, title));
    }

    /**
     * GET /api/chat/sessions
     * Returns all sessions for the user, newest first.
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponse>> getSessions(
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(chatService.getSessions(userId));
    }

    /**
     * DELETE /api/chat/sessions/{sessionId}
     * Deletes a session and all its messages (cascade).
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long sessionId) {

        chatService.deleteSession(userId, sessionId);
        return ResponseEntity.noContent().build();
    }

    // ── Messages ───────────────────────────────────────────────────────────

    /**
     * POST /api/chat/sessions/{sessionId}/messages
     * Body: { "message": "How do I improve my sleep?", "userContext": {...} }
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ChatResponse> sendMessage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long sessionId,
            @Valid @RequestBody ChatRequest request) {

        return ResponseEntity.ok(chatService.sendMessage(userId, sessionId, request));
    }

    /**
     * GET /api/chat/sessions/{sessionId}/messages
     * Returns full message history for the session, oldest first.
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(chatService.getHistory(userId, sessionId));
    }
}

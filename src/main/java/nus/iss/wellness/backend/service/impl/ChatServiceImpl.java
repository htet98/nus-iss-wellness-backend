package nus.iss.wellness.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nus.iss.wellness.backend.config.PythonAiConfig;
import nus.iss.wellness.backend.dto.request.ChatRequest;
import nus.iss.wellness.backend.dto.response.ChatMessageResponse;
import nus.iss.wellness.backend.dto.response.ChatResponse;
import nus.iss.wellness.backend.dto.response.ChatSessionResponse;
import nus.iss.wellness.backend.exception.ResourceNotFoundException;
import nus.iss.wellness.backend.model.ChatMessage;
import nus.iss.wellness.backend.model.ChatMessage.SenderRole;
import nus.iss.wellness.backend.model.ChatSession;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.repository.ChatMessageRepository;
import nus.iss.wellness.backend.repository.ChatSessionRepository;
import nus.iss.wellness.backend.repository.UserRepository;
import nus.iss.wellness.backend.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
/**
 *  Author: Htet Nandar
 */
@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PythonAiConfig aiConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ChatServiceImpl(ChatSessionRepository sessionRepository,
                           ChatMessageRepository messageRepository,
                           UserRepository userRepository,
                           PythonAiConfig aiConfig,
                           ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository    = userRepository;
        this.aiConfig          = aiConfig;
        this.objectMapper      = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(aiConfig.getConnectTimeoutSeconds()))
                .version(HttpClient.Version.HTTP_1_1)   // force HTTP/1.1 — uvicorn doesn't support h2c
                .build();
    }

    // ── Create session ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public ChatSessionResponse createSession(Long userId, String title) {
        User user = findUser(userId);
        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setTitle(title != null ? title : "New Chat");
        return ChatSessionResponse.from(sessionRepository.save(session));
    }

    // ── List sessions ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getSessions(Long userId) {
        return sessionRepository.findByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(ChatSessionResponse::from)
                .toList();
    }

    // ── Send message ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public ChatResponse sendMessage(Long userId, Long sessionId, ChatRequest request) {
        // 1. Verify session belongs to user
        ChatSession session = sessionRepository.findByIdAndUserUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + sessionId));

        // 2. Load existing message history from DB
        List<ChatMessage> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        // 3. Save the user's message first
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSession(session);
        userMessage.setSenderRole(SenderRole.user);
        userMessage.setContent(request.getMessage());
        messageRepository.save(userMessage);

        // 4. Call Python AI service
        String aiReply = callPythonChat(sessionId, request.getMessage(), history, request.getUserContext());

        // 5. Save assistant reply
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setSession(session);
        assistantMessage.setSenderRole(SenderRole.assistant);
        assistantMessage.setContent(aiReply);
        ChatMessage saved = messageRepository.save(assistantMessage);

        return new ChatResponse(sessionId, saved.getId(), aiReply, LocalDateTime.now());
    }

    // ── Get history ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(Long userId, Long sessionId) {
        // Verify ownership
        sessionRepository.findByIdAndUserUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + sessionId));

        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    // ── Delete session ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteSession(Long userId, Long sessionId) {
        ChatSession session = sessionRepository.findByIdAndUserUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + sessionId));
        sessionRepository.delete(session);
    }

    // ── Private: call Python /api/chat ─────────────────────────────────────

    private String callPythonChat(Long sessionId,
                                  String message,
                                  List<ChatMessage> history,
                                  Map<String, String> userContext) {
        try {
            // Build request body
            ObjectNode body = objectMapper.createObjectNode();
            body.put("session_id", String.valueOf(sessionId));
            body.put("message", message);

            // Convert DB history → [{role, content}] list
            ArrayNode historyNode = objectMapper.createArrayNode();
            for (ChatMessage msg : history) {
                ObjectNode m = objectMapper.createObjectNode();
                m.put("role", msg.getSenderRole().name());
                m.put("content", msg.getContent());
                historyNode.add(m);
            }
            body.set("history", historyNode);

            if (userContext != null && !userContext.isEmpty()) {
                body.set("user_context", objectMapper.valueToTree(userContext));
            }

            String requestBody = objectMapper.writeValueAsString(body);
            log.debug("Sending to Python AI: {}", requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(aiConfig.getBaseUrl() + "/api/chat"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(aiConfig.getReadTimeoutSeconds()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "AI service error: HTTP " + response.statusCode() + " — " + response.body());
            }

            JsonNode json = objectMapper.readTree(response.body());
            return json.get("reply").asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to reach AI service: " + e.getMessage(), e);
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }
}

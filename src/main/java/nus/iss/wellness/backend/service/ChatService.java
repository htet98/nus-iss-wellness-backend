package nus.iss.wellness.backend.service;

import nus.iss.wellness.backend.dto.request.ChatRequest;
import nus.iss.wellness.backend.dto.response.ChatMessageResponse;
import nus.iss.wellness.backend.dto.response.ChatResponse;
import nus.iss.wellness.backend.dto.response.ChatSessionResponse;

import java.util.List;
/**
 *  Author: Htet Nandar
 */
public interface ChatService {

    // Create a new chat session
    ChatSessionResponse createSession(Long userId, String title);

    // Get all sessions for a user
    List<ChatSessionResponse> getSessions(Long userId);

    // Send a message — loads history, calls Python AI, saves both messages, returns reply
    ChatResponse sendMessage(Long userId, Long sessionId, ChatRequest request);

    // Get full message history for a session
    List<ChatMessageResponse> getHistory(Long userId, Long sessionId);

    // Close (soft-delete) a session
    void deleteSession(Long userId, Long sessionId);
}

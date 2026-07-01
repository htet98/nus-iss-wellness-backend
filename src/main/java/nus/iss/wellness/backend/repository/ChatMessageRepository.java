package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *  Author: Htet Nandar
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // All messages in a session, ordered chronologically
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}

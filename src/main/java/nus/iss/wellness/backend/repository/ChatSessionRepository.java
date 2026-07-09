package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 *  Author: Htet Nandar
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    // All sessions for a user, newest first
    List<ChatSession> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    // Single session — verifies ownership
    Optional<ChatSession> findByIdAndUserUserId(Long id, Long userId);
}

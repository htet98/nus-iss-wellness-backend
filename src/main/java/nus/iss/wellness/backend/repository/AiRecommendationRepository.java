package nus.iss.wellness.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nus.iss.wellness.backend.model.AiRecommendation;
import nus.iss.wellness.backend.model.User;

// Author: Cecil


@Repository
public interface AiRecommendationRepository extends JpaRepository <AiRecommendation, Long> {

    Optional<AiRecommendation> findTopByUserOrderByGeneratedAtDesc(User user);

}
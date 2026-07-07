package nus.iss.wellness.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nus.iss.wellness.backend.config.PythonAiConfig;
import nus.iss.wellness.backend.dto.response.AiRecommendationResponse;
import nus.iss.wellness.backend.model.AiRecommendation;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.WellnessRecord;
import nus.iss.wellness.backend.repository.AiRecommendationRepository;
import nus.iss.wellness.backend.repository.UserRepository;
import nus.iss.wellness.backend.repository.WellnessRecordRepository;
import nus.iss.wellness.backend.service.AiRecommendationService;

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
import java.util.Optional;

/**
 * Author: Htet Nandar
 */
@Service
public class AiRecommendationServiceImpl implements AiRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(AiRecommendationServiceImpl.class);

    // Regenerate if the saved recommendation is older than this many hours
    private static final int STALE_HOURS = 24;

    private final UserRepository              userRepository;
    private final WellnessRecordRepository    wellnessRepository;
    private final AiRecommendationRepository  recommendationRepository;
    private final PythonAiConfig              aiConfig;
    private final ObjectMapper                objectMapper;
    private final HttpClient                  httpClient;

    public AiRecommendationServiceImpl(
            UserRepository userRepository,
            WellnessRecordRepository wellnessRepository,
            AiRecommendationRepository recommendationRepository,
            PythonAiConfig aiConfig,
            ObjectMapper objectMapper) {
        this.userRepository           = userRepository;
        this.wellnessRepository       = wellnessRepository;
        this.recommendationRepository = recommendationRepository;
        this.aiConfig                 = aiConfig;
        this.objectMapper             = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(aiConfig.getConnectTimeoutSeconds()))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    // ── GET latest (auto-generates if none exists or stale) ──────────────────

    @Override
    @Transactional
    public Optional<AiRecommendationResponse> getLatest(Long userId) {
        User user = findUser(userId);
        Optional<AiRecommendation> latest =
                recommendationRepository.findTopByUserOrderByGeneratedAtDesc(user);

        if (latest.isEmpty()) {
            log.info("[Recommendation] No recommendation in DB for userId={} — generating now…", userId);
            return Optional.of(generate(userId));
        }

        AiRecommendation rec = latest.get();
        long hoursOld = Duration.between(rec.getGeneratedAt(), LocalDateTime.now()).toHours();
        log.info("[Recommendation] Found in DB for userId={} — {} hours old", userId, hoursOld);

        if (hoursOld >= STALE_HOURS) {
            log.info("[Recommendation] Stale — regenerating for userId={}", userId);
            return Optional.of(generate(userId));
        }

        return Optional.of(AiRecommendationResponse.from(rec));
    }

    // ── GENERATE ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AiRecommendationResponse generate(Long userId) {
        User user = findUser(userId);
        log.info("[Recommendation] generate() called for userId={} — calling Python AI service…", userId);

        // Collect the user's most recent wellness stats
        String sleep = wellnessRepository
                .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(user, WellnessRecord.Category.sleep)
                .map(r -> r.getValue() + " hrs").orElse("not recorded");

        String exercise = wellnessRepository
                .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(user, WellnessRecord.Category.exercise)
                .map(r -> r.getDurationMinutes() + " mins").orElse("not recorded");

        String water = wellnessRepository
                .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(user, WellnessRecord.Category.water)
                .map(r -> r.getValue() + " L").orElse("not recorded");

        String steps = wellnessRepository
                .findTopByUserAndCategoryOrderByRecordDateDescCreatedAtDesc(user, WellnessRecord.Category.steps)
                .map(r -> String.format("%.0f steps", r.getValue())).orElse("not recorded");

        log.info("[Recommendation] User stats — sleep={}, exercise={}, water={}, steps={}",
                sleep, exercise, water, steps);

        String prompt = String.format(
            "Generate a personalized wellness recommendation based on these recent stats:\n" +
            "- Sleep: %s\n- Exercise: %s\n- Water intake: %s\n- Steps: %s\n\n" +
            "Format your response exactly like this:\n" +
            "Title: [5 words or fewer]\n\n[2-3 sentence recommendation]",
            sleep, exercise, water, steps
        );

        String aiReply = callPythonChat(prompt);
        log.info("[Recommendation] AI reply received ({} chars)", aiReply.length());

        // Parse "Title: <title>\n\n<body>" if the AI followed the format
        String title;
        String recommendation;
        if (aiReply.startsWith("Title:")) {
            int split = aiReply.indexOf("\n\n");
            if (split > 0) {
                title          = aiReply.substring(6, split).trim();
                recommendation = aiReply.substring(split + 2).trim();
            } else {
                int newline = aiReply.indexOf('\n');
                title          = newline > 0 ? aiReply.substring(6, newline).trim() : "Your Wellness Tip";
                recommendation = newline > 0 ? aiReply.substring(newline).trim() : aiReply;
            }
        } else {
            title          = "Your Wellness Tip";
            recommendation = aiReply;
        }

        log.info("[Recommendation] Parsed title=\"{}\"", title);

        AiRecommendation rec = new AiRecommendation();
        rec.setUser(user);
        rec.setTitle(title);
        rec.setRecommendation(recommendation);
        rec.setStatus("generated");
        rec.setGeneratedAt(LocalDateTime.now());

        AiRecommendation saved = recommendationRepository.save(rec);
        log.info("[Recommendation] Saved to DB with id={}", saved.getRecommendationId());

        return AiRecommendationResponse.from(saved);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String callPythonChat(String message) {
        try {
            log.info("[Recommendation] POST {}/api/chat", aiConfig.getBaseUrl());

            ObjectNode body = objectMapper.createObjectNode();
            body.put("message", message);
            body.set("history", objectMapper.createArrayNode());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aiConfig.getBaseUrl() + "/api/chat"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(aiConfig.getReadTimeoutSeconds()))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            log.info("[Recommendation] Python response status={} body={}", response.statusCode(), response.body());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                    "AI service error: HTTP " + response.statusCode() + " — " + response.body());
            }

            JsonNode json = objectMapper.readTree(response.body());
            JsonNode replyNode = json.get("reply");
            if (replyNode == null) {
                throw new RuntimeException(
                    "Python response missing 'reply' field. Got: " + response.body());
            }
            return replyNode.asText();

        } catch (RuntimeException e) {
            throw e;   // re-throw as-is so the message is preserved
        } catch (java.net.ConnectException e) {
            throw new RuntimeException(
                "Python AI service is not running at " + aiConfig.getBaseUrl()
                + " — start it with: uvicorn main:app --reload", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reach Python AI service: "
                    + e.getClass().getSimpleName() + " — " + e.getMessage(), e);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }
}

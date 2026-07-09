-- ============================================================
-- Wellness App — MySQL Schema
-- Run order matters: referenced tables must exist first.
-- All statements use IF NOT EXISTS for idempotency.
-- ============================================================
-- Disable foreign key checks to allow dropping tables with constraints
SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_profile;
DROP TABLE IF EXISTS wellness_records;
DROP TABLE IF EXISTS ai_recommendations;
DROP TABLE IF EXISTS chat_sessions;
DROP TABLE IF EXISTS chat_messages;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS users
(
    user_id       BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(5)   NOT NULL,
    email         VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    jwt_token     TEXT NULL,

    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email (email)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_profile
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    first_name   VARCHAR(50)  NULL,
    last_name    VARCHAR(50)  NULL,
    gender       VARCHAR(10)  NULL,
    date_of_birth DATE        NULL,
    address      VARCHAR(255) NULL,
    height_cm    DOUBLE       NULL,
    weight_kg    DOUBLE       NULL,
    fitness_goal VARCHAR(255) NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_profile_user_id (user_id),
    CONSTRAINT fk_user_profile_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
                                                                 ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS wellness_records
(
    id               BIGINT                                        NOT NULL AUTO_INCREMENT,
    user_id          BIGINT                                        NOT NULL,
    category         ENUM ('sleep','exercise','mood','water','steps') NOT NULL,
    value            DOUBLE                                        NOT NULL,
    calories_burned  DOUBLE                                        NULL,
    unit             VARCHAR(20)                                   NULL,
    duration_minutes INT                                           NULL,
    record_date      DATE                                          NOT NULL,
    notes            TEXT                                          NULL,
    created_at       TIMESTAMP                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_wellness_user_date (user_id, record_date DESC),
    INDEX idx_wellness_category (user_id, category),
    CONSTRAINT fk_wellness_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS ai_recommendations
(
    recommendation_id       BIGINT                         NOT NULL AUTO_INCREMENT,
    user_id      BIGINT                                    NOT NULL,
    title        VARCHAR(200)                              NOT NULL,
    recommendation      TEXT                               NOT NULL,
    status       ENUM ('pending','generated','dismissed')  NOT NULL DEFAULT 'pending',
    generated_at TIMESTAMP                                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (recommendation_id),
    INDEX idx_ai_rec_user (user_id, generated_at DESC),
    CONSTRAINT fk_ai_rec_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS chat_sessions
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(200) NULL,
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_chat_session_user (user_id, created_at DESC),
    CONSTRAINT fk_chat_session_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS chat_messages
(
    id          BIGINT                               NOT NULL AUTO_INCREMENT,
    session_id  BIGINT                               NOT NULL,
    sender_role ENUM ('user','assistant','system')   NOT NULL,
    content     TEXT                                 NOT NULL,
    created_at  TIMESTAMP                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_chat_msg_session (session_id, created_at ASC),
    CONSTRAINT fk_chat_msg_session
    FOREIGN KEY (session_id) REFERENCES chat_sessions (id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
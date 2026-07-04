-- ============================================================
-- Wellness App — Sample Seed Data
-- Passwords are BCrypt hashes of "Password123!"
-- Uses INSERT IGNORE to avoid errors on re-run.
-- ============================================================

-- ──────────────────────────────────────────────────────────────
-- Users
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO users (user_id, username, password_hash, role, email, created_at)
VALUES
    (1, 'alice',  '9cba73c31ac15d21512382ce6b21e83f8b9fddd31196ff4f54559a8e29add1e3bc4038c86c9bee7512d0d8ea72ec9480580dc677a9f172b46366ecb5198615cc', 'ADMIN', 'alice@example.com',  NOW()),
    (2, 'bob',    '9cba73c31ac15d21512382ce6b21e83f8b9fddd31196ff4f54559a8e29add1e3bc4038c86c9bee7512d0d8ea72ec9480580dc677a9f172b46366ecb5198615cc', 'user', 'bob@example.com',    NOW()),
    (3, 'carol',  '9cba73c31ac15d21512382ce6b21e83f8b9fddd31196ff4f54559a8e29add1e3bc4038c86c9bee7512d0d8ea72ec9480580dc677a9f172b46366ecb5198615cc', 'user', 'carol@example.com',  NOW());

-- After hashing the passwords, the actual stored values will be:
# (1, 'alice',  '$2b$10$D0MgzqdC.e1NzJmwpg5lZOvepUL4fIUG1b1zVWo4eePQ8VBcsJT3G', 'alice@example.com',  NOW()),
# (2, 'bob',    '$2b$10$D0MgzqdC.e1NzJmwpg5lZOvepUL4fIUG1b1zVWo4eePQ8VBcsJT3G', 'bob@example.com',    NOW()),
# (3, 'carol',  '$2b$10$D0MgzqdC.e1NzJmwpg5lZOvepUL4fIUG1b1zVWo4eePQ8VBcsJT3G', 'carol@example.com',  NOW());


-- ──────────────────────────────────────────────────────────────
-- User Profiles
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO user_profile (user_id, first_name, last_name, gender, date_of_birth, address, height_cm, weight_kg, fitness_goal)
VALUES
    (1, 'Alice', 'Tan',    'FEMALE', '1995-03-15', '10 Orchard Rd, Singapore', 163.0, 58.0,  'LOSE_WEIGHT'),
    (2, 'Bob',   'Lim',    'MALE',   '1990-07-22', '25 Marina Bay, Singapore', 178.0, 82.0,  'BUILD_MUSCLE'),
    (3, 'Carol', 'Wong',   'FEMALE', '1998-11-03', '5 Jurong East, Singapore', 158.0, 52.0,  'IMPROVE_SLEEP');

-- ──────────────────────────────────────────────────────────────
-- Wellness Records — Alice (user_id = 1)
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO wellness_records (user_id, category, value, calories_burned, unit, duration_minutes, record_date, notes)
VALUES
    -- Sleep
    (1, 'sleep',    7.5,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 6 DAY,  'Felt well rested'),
    (1, 'sleep',    6.0,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 5 DAY,  'Woke up once during night'),
    (1, 'sleep',    8.0,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 4 DAY,  'Great sleep'),
    (1, 'sleep',    5.5,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 3 DAY,  'Stressed, could not sleep well'),
    (1, 'sleep',    7.0,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 2 DAY,  NULL),
    -- Exercise
    (1, 'exercise', 5.2,  320,  'km',    35,    CURDATE() - INTERVAL 6 DAY,  'Morning jog'),
    (1, 'exercise', 0,    250,  'session', 45,   CURDATE() - INTERVAL 4 DAY,  'Yoga session'),
    (1, 'exercise', 3.0,  180,  'km',    25,    CURDATE() - INTERVAL 2 DAY,  'Evening walk'),
    -- Water
    (1, 'water',    2.2,  NULL, 'liters', NULL,  CURDATE() - INTERVAL 6 DAY,  NULL),
    (1, 'water',    1.8,  NULL, 'liters', NULL,  CURDATE() - INTERVAL 5 DAY,  'Forgot to drink enough'),
    (1, 'water',    2.5,  NULL, 'liters', NULL,  CURDATE() - INTERVAL 4 DAY,  NULL),
    (1, 'water',    2.0,  NULL, 'liters', NULL,  CURDATE() - INTERVAL 3 DAY,  NULL),
    (1, 'water',    2.3,  NULL, 'liters', NULL,  CURDATE() - INTERVAL 2 DAY,  NULL),
    -- Steps
    (1, 'steps',    8500, NULL, 'steps', NULL,  CURDATE() - INTERVAL 6 DAY,  NULL),
    (1, 'steps',    6200, NULL, 'steps', NULL,  CURDATE() - INTERVAL 5 DAY,  'Work from home day'),
    (1, 'steps',    10300,NULL, 'steps', NULL,  CURDATE() - INTERVAL 4 DAY,  'Walked to lunch'),
    (1, 'steps',    9100, NULL, 'steps', NULL,  CURDATE() - INTERVAL 3 DAY,  NULL),
    (1, 'steps',    11200,NULL, 'steps', NULL,  CURDATE() - INTERVAL 2 DAY,  NULL),
    -- Mood
    (1, 'mood',     7,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 6 DAY,  'Feeling motivated'),
    (1, 'mood',     5,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 5 DAY,  'Bit stressed at work'),
    (1, 'mood',     8,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 4 DAY,  'Great after yoga'),
    (1, 'mood',     4,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 3 DAY,  'Poor sleep affected mood'),
    (1, 'mood',     7,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 2 DAY,  NULL);

-- ──────────────────────────────────────────────────────────────
-- Wellness Records — Bob (user_id = 2)
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO wellness_records (user_id, category, value, calories_burned, unit, duration_minutes, record_date, notes)
VALUES
    (2, 'sleep',    6.5,  NULL, 'hours',  NULL, CURDATE() - INTERVAL 5 DAY, NULL),
    (2, 'sleep',    7.0,  NULL, 'hours',  NULL, CURDATE() - INTERVAL 3 DAY, NULL),
    (2, 'exercise', 0,    450,  'session', 60,   CURDATE() - INTERVAL 5 DAY, 'Chest and triceps'),
    (2, 'exercise', 0,    500,  'session', 75,   CURDATE() - INTERVAL 3 DAY, 'Back and biceps day'),
    (2, 'exercise', 0,    420,  'session', 60,   CURDATE() - INTERVAL 1 DAY, 'Leg day'),
    (2, 'water',    3.0,  NULL, 'liters', NULL,  CURDATE() - INTERVAL 5 DAY, 'Post workout hydration'),
    (2, 'water',    2.8,  NULL, 'liters', NULL,  CURDATE() - INTERVAL 3 DAY, NULL),
    (2, 'steps',    7800, NULL, 'steps',  NULL,  CURDATE() - INTERVAL 5 DAY, NULL),
    (2, 'steps',    5200, NULL, 'steps',  NULL,  CURDATE() - INTERVAL 3 DAY, 'Rest day'),
    (2, 'mood',     8,    NULL, '1-10',   NULL,  CURDATE() - INTERVAL 5 DAY, 'Good workout'),
    (2, 'mood',     7,    NULL, '1-10',   NULL,  CURDATE() - INTERVAL 3 DAY, NULL);

-- ──────────────────────────────────────────────────────────────
-- Wellness Records — Carol (user_id = 3)
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO wellness_records (user_id, category, value, calories_burned, unit, duration_minutes, record_date, notes)
VALUES
    (3, 'sleep',    5.0,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 6 DAY, 'Insomnia again'),
    (3, 'sleep',    6.5,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 5 DAY, NULL),
    (3, 'sleep',    5.5,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 4 DAY, 'Woke up early'),
    (3, 'sleep',    7.5,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 3 DAY, 'Used sleep meditation'),
    (3, 'sleep',    8.0,  NULL, 'hours', NULL,  CURDATE() - INTERVAL 2 DAY, 'Best sleep in weeks'),
    (3, 'exercise', 0,    150,  'session', 30,  CURDATE() - INTERVAL 5 DAY, 'Gentle stretching'),
    (3, 'exercise', 0,    200,  'session', 30,  CURDATE() - INTERVAL 2 DAY, 'Meditation + yoga'),
    (3, 'water',    1.5,  NULL, 'liters', NULL, CURDATE() - INTERVAL 6 DAY, 'Forgot to drink'),
    (3, 'water',    2.0,  NULL, 'liters', NULL, CURDATE() - INTERVAL 4 DAY, NULL),
    (3, 'water',    2.2,  NULL, 'liters', NULL, CURDATE() - INTERVAL 2 DAY, NULL),
    (3, 'steps',    4500, NULL, 'steps', NULL,  CURDATE() - INTERVAL 6 DAY, NULL),
    (3, 'steps',    6800, NULL, 'steps', NULL,  CURDATE() - INTERVAL 4 DAY, NULL),
    (3, 'steps',    7200, NULL, 'steps', NULL,  CURDATE() - INTERVAL 2 DAY, NULL),
    (3, 'mood',     4,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 6 DAY, 'Tired and anxious'),
    (3, 'mood',     5,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 4 DAY, 'Better after stretching'),
    (3, 'mood',     7,    NULL, '1-10',  NULL,  CURDATE() - INTERVAL 2 DAY, 'Sleep improvement helping');

-- ──────────────────────────────────────────────────────────────
-- AI Recommendations (sample pre-seeded)
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO ai_recommendations (user_id, title, recommendation, status, generated_at)
VALUES
    (1, 'Improve Your Sleep Consistency',
     'Your average sleep over the past week is 6.8 hours, which is slightly below the recommended 7-9 hours. On nights when you slept under 6 hours, your mood score dropped by an average of 2 points the next day. Try setting a consistent bedtime of 10:30 PM and avoiding screens 1 hour before bed to improve both duration and quality.',
     'generated', NOW() - INTERVAL 2 DAY),

    (2, 'Optimize Recovery Between Strength Sessions',
     'You are training 3 days consecutively without adequate rest. For muscle hypertrophy, allow 48 hours between sessions targeting the same muscle group. Consider incorporating a rest day between your chest/triceps and back/biceps sessions, and prioritize 7-8 hours of sleep for optimal muscle repair.',
     'generated', NOW() - INTERVAL 1 DAY),

    (3, 'Sleep Hygiene Plan for Stress Reduction',
     'Your sleep data shows a positive trend — you improved from 5.0 to 8.0 hours over the past week using meditation techniques. Keep building on this by maintaining a consistent wake time even on weekends. Your mood scores correlate strongly with sleep quality (r=0.85), so protecting your sleep will be the highest-impact intervention for stress reduction.',
     'generated', NOW());

-- ──────────────────────────────────────────────────────────────
-- Chat Sessions
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO chat_sessions (id, user_id, title, is_active, created_at)
VALUES
    (1, 1, 'How to improve my running pace?',   1, NOW() - INTERVAL 3 DAY),
    (2, 1, 'Best foods for weight loss',         0, NOW() - INTERVAL 1 DAY),
    (3, 3, 'Tips for better sleep',              1, NOW() - INTERVAL 2 DAY);

-- ──────────────────────────────────────────────────────────────
-- Chat Messages
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO chat_messages (session_id, sender_role, content, created_at)
VALUES
    (1, 'user',      'How can I improve my 5km running pace?',                                                          NOW() - INTERVAL 3 DAY),
    (1, 'assistant', 'To improve your 5km pace, focus on interval training 1-2 times per week. Run at 90% effort for 400m, then recover with a 90-second slow jog. Aim for 6-8 intervals per session. Also ensure you include one easy long run per week to build aerobic base. Based on your current 5.2km runs, you have a solid foundation to work from!',
                                                                                                                        NOW() - INTERVAL 3 DAY),
    (1, 'user',      'How often should I do interval training?',                                                        NOW() - INTERVAL 3 DAY),
    (1, 'assistant', 'For most runners, 1-2 interval sessions per week is optimal. More than that increases injury risk before your body adapts. Fill the remaining days with easy aerobic runs at a comfortable conversational pace, and take at least one full rest day. After 4-6 weeks of consistent interval work you should notice your pace improving.',
                                                                                                                        NOW() - INTERVAL 3 DAY),
    (3, 'user',      'I have been struggling with sleep. Any tips?',                                                    NOW() - INTERVAL 2 DAY),
    (3, 'assistant', 'Looking at your data, your sleep has improved significantly from 5.0 to 8.0 hours over the past week — great progress! To maintain this, keep your bedtime and wake time consistent every day (even weekends). Continue the meditation you have been doing, keep your bedroom cool (15-18°C), and avoid caffeine after 2 PM. Your mood scores clearly rise when you sleep well, so this is your highest-impact habit to protect.',
                                                                                                                        NOW() - INTERVAL 2 DAY);

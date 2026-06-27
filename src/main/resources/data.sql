-- ============================================================
-- Wellness App — Sample Seed Data
-- Passwords are BCrypt hashes of "Password123!"
-- Uses INSERT IGNORE to avoid errors on re-run.
-- ============================================================

-- ──────────────────────────────────────────────────────────────
-- Users
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO users (user_id, username, password_hash, email, created_at)
VALUES
    (1, 'alice',  'Password123!', 'alice@example.com',  NOW()),
    (2, 'bob',    'Password123!', 'bob@example.com',    NOW()),
    (3, 'carol',  'Password123!', 'carol@example.com',  NOW());

-- After hashing the passwords, the actual stored values will be:
# (1, 'alice',  '$2b$10$D0MgzqdC.e1NzJmwpg5lZOvepUL4fIUG1b1zVWo4eePQ8VBcsJT3G', 'alice@example.com',  NOW()),
# (2, 'bob',    '$2b$10$D0MgzqdC.e1NzJmwpg5lZOvepUL4fIUG1b1zVWo4eePQ8VBcsJT3G', 'bob@example.com',    NOW()),
# (3, 'carol',  '$2b$10$D0MgzqdC.e1NzJmwpg5lZOvepUL4fIUG1b1zVWo4eePQ8VBcsJT3G', 'carol@example.com',  NOW());


-- ──────────────────────────────────────────────────────────────
-- User Profiles
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO user_profile (user_id, first_name, last_name, gender, date_of_birth, address, height_cm, weight_kg, fitness_goal)
VALUES
    (1, 'Alice', 'Tan',    'Female', '1995-03-15', '10 Orchard Rd, Singapore', 163.0, 58.0,  'Lose 5kg and improve cardio endurance'),
    (2, 'Bob',   'Lim',    'Male',   '1990-07-22', '25 Marina Bay, Singapore', 178.0, 82.0,  'Build muscle mass and increase strength'),
    (3, 'Carol', 'Wong',   'Female', '1998-11-03', '5 Jurong East, Singapore', 158.0, 52.0,  'Improve sleep quality and reduce stress');

-- ──────────────────────────────────────────────────────────────
-- Wellness Records — Alice (user_id = 1)
-- ──────────────────────────────────────────────────────────────
-- Wellness Records
INSERT IGNORE INTO wellness_records (user_id, activity_type, record_date, duration_minutes, notes)
VALUES
    (1, 'Running',  CURDATE() - INTERVAL 6 DAY, 30, 'Morning run at the park'),
    (1, 'Cycling',  CURDATE() - INTERVAL 5 DAY, 45, 'Evening ride'),
    (1, 'Swimming', CURDATE() - INTERVAL 4 DAY, 60, 'Lap swimming at the pool'),
    (1, 'Yoga',     CURDATE() - INTERVAL 3 DAY, 40, 'Stretch and relax'),
    (1, 'Walking',  CURDATE() - INTERVAL 2 DAY, 25, 'Walk around the neighbourhood'),
    (2, 'Running',  CURDATE() - INTERVAL 5 DAY, 35, NULL),
    (3, 'Yoga',     CURDATE() - INTERVAL 4 DAY, 45, 'Morning yoga');
-- ──────────────────────────────────────────────────────────────

-- ──────────────────────────────────────────────────────────────
-- AI Recommendations (sample pre-seeded)
-- ──────────────────────────────────────────────────────────────
INSERT IGNORE INTO ai_recommendations (user_id, title, content, status, generated_at)
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

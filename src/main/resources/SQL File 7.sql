USE wellness_db;

SELECT category, record_date, COUNT(*) AS entries
FROM wellness_records
WHERE user_id = 1
GROUP BY category, record_date
HAVING COUNT(*) > 1;

SET SQL_SAFE_UPDATES = 0;

DELETE w1 FROM wellness_records w1
JOIN wellness_records w2
  ON w1.user_id = w2.user_id
 AND w1.category = w2.category
 AND w1.record_date = w2.record_date
 AND w1.id < w2.id;
 
 SET SQL_SAFE_UPDATES = 1;
 
 SELECT id, category, value, record_date
FROM wellness_records
WHERE user_id = 1 AND record_date = CURDATE();
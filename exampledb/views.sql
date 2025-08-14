-- user view
SELECT u.user_id, name, user_name_id
FROM users u LEFT JOIN user_names un ON u.user_id = un.user_id
WHERE NOT EXISTS (SELECT *
    FROM user_names
    WHERE user_names.prior_user_name_id = un.user_name_id)
    AND u.user_id = "dev0-jay";

-- exercise view    
SELECT e.exercise_name, e.is_isometric, ewg.muscle_group_name, en.exercise_note,
        name AS created_by_user
        FROM exercises e LEFT JOIN exercise_works_group ewg ON e.exercise_name = ewg.exercise_name AND
            e.created_by_user_id = ewg.created_by_user_id
            LEFT JOIN exercise_notes en ON e.exercise_name = en.exercise_name AND
            e.created_by_user_id = en.created_by_user_id
            LEFT JOIN (
                SELECT u.user_id, name, user_name_id
                FROM users u LEFT JOIN user_names un ON u.user_id = un.user_id
                WHERE NOT EXISTS (SELECT *
                    FROM user_names
                    WHERE user_names.prior_user_name_id = un.user_name_id
                )) UserSubquery ON UserSubquery.user_id = e.created_by_user_id
        WHERE NOT EXISTS (
            SELECT *
            FROM exercise_deletions ed
            WHERE ed.exercise_name = e.exercise_name
                AND ed.created_by_user_id = e.created_by_user_id)
            AND e.exercise_name = "bench-press"
            AND e.created_by_user_id = "dev0-jay";
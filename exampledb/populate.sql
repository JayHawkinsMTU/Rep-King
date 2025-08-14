PRAGMA foreign_keys = ON;

INSERT INTO users(user_id)
VALUES ("dev0-jay");

INSERT INTO user_names (user_name_id, prior_user_name_id, user_id, name)
VALUES ("ignore", null, "dev0-jay", "not-final-name"),
    ("final", "ignore", "dev0-jay", "Jay");
    
INSERT INTO muscle_groups (muscle_group_name)
VALUES ("biceps"), ("calves"), ("chest"), ("core"), ("forearms"), ("glutes"),
    ("hamstrings"), ("hips"), ("lats"), ("lower-back"), ("shoulders"), ("quads"),
    ("traps"), ("triceps");
    
-- Use this timestamp for everything: 2025-08-09 00:00:00.000
-- (YYYY-MM-DD HH:MM:SS.SSS)
INSERT INTO exercises(exercise_name, timestamp, created_by_user_id, is_isometric)
VALUES ("bench-press", "2025-08-09 00:00:00.000", "dev0-jay", 0),
    ("squat", "2025-08-09 00:00:00.000", "dev0-jay", 0),
    ("plank", "2025-08-09 00:00:00.000", "dev0-jay", 1);

INSERT INTO exercise_works_group (exercise_name, exercise_timestamp, created_by_user_id, muscle_group_name)
VALUES ("bench-press", "2025-08-09 00:00:00.000", "dev0-jay", "chest"),
    ("bench-press", "2025-08-09 00:00:00.000", "dev0-jay", "triceps"),
    ("squat", "2025-08-09 00:00:00.000", "dev0-jay", "quads"),
    ("squat", "2025-08-09 00:00:00.000", "dev0-jay", "glutes"),
    ("plank", "2025-08-09 00:00:00.000", "dev0-jay", "core");

INSERT INTO exercise_deletions (exercise_name, exercise_timestamp, created_by_user_id)
    VALUES ("bench-press", "2025-08-09 00:00:00.000", "dev0-jay");
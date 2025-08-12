CREATE TABLE users(
    user_id VARCHAR(36) PRIMARY KEY NOT NULL
);

CREATE TABLE user_names(
    user_name_id VARCHAR(36) PRIMARY KEY NOT NULL,
    prior_user_name_id VARCHAR(36),
    user_id VARCHAR(36) NOT NULL,
    name VARCHAR(32) NOT NULL,
    FOREIGN KEY (prior_user_name_id)
    REFERENCES user_names(user_name_id)
        ON DELETE SET NULL,
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE TABLE muscle_groups(
    muscle_group_name VARCHAR(16) PRIMARY KEY NOT NULL
);

CREATE TABLE exercises(
    exercise_name VARCHAR(50) NOT NULL,
    created_by_user_id VARCHAR(36) NOT NULL,
    is_isometric INT NOT NULL DEFAULT 0,
    PRIMARY KEY (exercise_name, created_by_user_id),
    FOREIGN KEY (created_by_user_id)
    REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE TABLE exercise_works_group(
    exercise_name VARCHAR(50) NOT NULL,
    created_by_user_id VARCHAR(36) NOT NULL,
    muscle_group_name VARCHAR(16) NOT NULL,
    PRIMARY KEY (exercise_name, created_by_user_id, muscle_group_name),
    FOREIGN KEY (exercise_name)
    REFERENCES exercises(exercise_name),
    FOREIGN KEY (created_by_user_id)
    REFERENCES exercises(created_by_user_id)
        ON DELETE CASCADE,
    FOREIGN KEY (muscle_group_name)
    REFERENCES muscle_groups(muscle_group_name)
);

CREATE TABLE working_sets(
    timestamp VARCHAR(25) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(36) NOT NULL,
    exercise_name VARCHAR(50) NOT NULL,
    exercise_created_by_user_id VARCHAR(36) NOT NULL,
    weight REAL NOT NULL DEFAULT 0,
    reps INT DEFAULT NULL,
    time TEXT DEFAULT NULL,
    PRIMARY KEY (timestamp, user_id, exercise_name, created_by_user_id),
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
        ON DELETE CASCADE,
    FOREIGN KEY (exercise_name, created_by_user_id)
    REFERENCES exercises(exercise_name, created_by_user_id)
);

CREATE TABLE working_set_deletions(
    timestamp VARCHAR(25) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(36) NOT NULL,
    exercise_name VARCHAR(50) NOT NULL,
    exercise_created_by_user_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (timestamp, user_id, exercise_name, created_by_user_id),
    FOREIGN KEY (timestamp, user_id, exercise_name, created_by_user_id)
    REFERENCES working_sets(timestamp, user_id, exercise_name, created_by_user_id)
        ON DELETE CASCADE
);

CREATE TABLE exercise_notes(
    user_id VARCHAR(36) NOT NULL,
    exercise_name VARCHAR(50) NOT NULL,
    created_by_user_id VARCHAR(36) NOT NULL,
    exercise_note TEXT,
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
        ON DELETE CASCADE,
    FOREIGN KEY (exercise_name, created_by_user_id)
    REFERENCES exercises(exercise_name, created_by_user_id)
);

CREATE TABLE working_sets_per_muscle_group_per_week_targets(
    target_id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    muscle_group_name VARCHAR(16) NOT NULL,
    prior_target_id VARCHAR(36),
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
        ON DELETE CASCADE,
    FOREIGN KEY (muscle_group_name)
    REFERENCES muscle_groups(muscle_group_name),
    FOREIGN KEY (prior_target_id)
    REFERENCES working_sets_per_muscle_group_per_week_targets(target_id)
);
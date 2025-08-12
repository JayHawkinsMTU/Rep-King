package dev.jayhawkins.repking.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.intellij.lang.annotations.Language


class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        configureDatabase(db)
        createTables(db)
        populateDatabase(db)
        TODO("Not yet implemented")
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Populates database with universal initial data such as the dev users,
     * exercises, muscle groups, but not the actual user data.
     *
     * @property db The database to populate
     */
    private fun populateDatabase(db: SQLiteDatabase) {
        val devUserId = "dev0-jay"
        // Dev users for initial exercises
        @Language("RoomSql") val populateUsers = """
            INSERT INTO users (user_id) VALUES (${devUserId})
        """.trimIndent()
        db.execSQL(populateUsers)

        @Language("RoomSql") val populateUserNames = """
            INSERT INTO user_names (user_name_id, user_id, name)
                VALUES (${devUserId}, ${devUserId}, "Jay")
        """.trimIndent()
        db.execSQL(populateUserNames)

        @Language("RoomSql") val populateMuscleGroups = """
            INSERT INTO muscle_groups (muscle_group_name)
                VALUES ("biceps"), ("calves"), ("chest"), ("core"), ("forearms"),
                ("glutes"), ("hamstrings"), ("hips"), ("lats"), ("lower-back"),
                ("shoulders"), ("quads"), ("traps"), ("triceps")
        """.trimIndent()
        db.execSQL(populateMuscleGroups)

        // TODO: increase initial exercises set once stable
        @Language("RoomSql") val populateExercises = """
            INSERT INTO exercises (exercise_name, created_by_user_id, is_isometric)
                VALUES ("flat-barbell-bench-press", ${devUserId}, 0),
                ("cable-curls", ${devUserId}, 0),
                ("calf-raises", ${devUserId}, 0),
                ("dumbbell-flyes", ${devUserId}, 0),
                ("cable-crunch", ${devUserId}, 0),
                ("plank", ${devUserId}, 1),
                ("dumbbell-wrist-extensions", ${devUserId}, 0),
                ("barbell-hip-thrusts", ${devUserId}, 0),
                ("hamstring-curl-machine", ${devUserId}, 0),
                ("hip-induction-machine", ${devUserId}, 0),
                ("cable-lat-pull-downs", ${devUserId}, 0),
                ("lower-back-extension-machine", ${devUserId}, 0),
                ("dumbbell-shoulder-press", ${devUserId}, 0),
                ("leg-extension-machine", ${devUserId}, 0),
                ("dumbbell-shrugs", ${devUserId}, 0),
                ("rope-triceps-pushdown", ${devUserId}, 0)
        """.trimIndent()
        db.execSQL(populateExercises)

        val populateExerciseWorksGroup = """
            INSERT INTO exercise_works_group (exercise_name, created_by_user_id, muscle_group_name)
                VALUES (flat-barbell-bench-press, 
        """.trimIndent()
    }

    /**
     * Configures database rules
     *
     * @property db The database to configure
     */
    private fun configureDatabase(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON")
    }

    /**
     * Creates all tables for the database
     *
     * @property db The database to make tables for
     */
    private fun createTables(db: SQLiteDatabase) {
        // GUID stored as a hyphenated string
        // User is an entity
        @Language("RoomSql") val createUsersTable = """
            CREATE TABLE users(
                user_id VARCHAR(36) PRIMARY KEY NOT NULL
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        // Name of user stored using "Mutable Property" pattern
        @Language("RoomSql") val createUserNamesTable = """
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
            )
        """.trimIndent()
        db.execSQL(createUserNamesTable)

        @Language("RoomSql") val createMuscleGroupsTable = """
            CREATE TABLE muscle_groups(
                muscle_group_name VARCHAR(16) PRIMARY KEY NOT NULL
            )
        """.trimIndent()
        db.execSQL(createMuscleGroupsTable)

        @Language("RoomSql") val createExerciseTable = """
            CREATE TABLE exercises(
                exercise_name VARCHAR(50) NOT NULL,
                created_by_user_id VARCHAR(36) NOT NULL,
                is_isometric INT NOT NULL DEFAULT 0,
                PRIMARY KEY (exercise_name, created_by_user_id),
                FOREIGN KEY (created_by_user_id)
                REFERENCES users(user_id)
                    ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(createExerciseTable)

        @Language("RoomSql") val createExerciseDeletionsTable = """
            CREATE TABLE exercises(
                exercise_name VARCHAR(50) NOT NULL,
                created_by_user_id VARCHAR(36) NOT NULL,
                PRIMARY KEY (exercise_name, created_by_user_id),
                FOREIGN KEY (exercise_name, created_by_user_id)
                REFERENCES exercises(exercise_name, created_by_user_id)
                    ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(createExerciseDeletionsTable)

        @Language("RoomSql") val createExerciseWorksGroupTable = """
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
            )
        """.trimIndent()
        db.execSQL((createExerciseWorksGroupTable))

        @Language("RoomSql") val createWorkingSetsTable = """
            CREATE TABLE working_sets(
                timestamp VARCHAR(25) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                user_id VARCHAR(36) NOT NULL,
                exercise_name VARCHAR(50) NOT NULL,
                exercise_created_by_user_id VARCHAR(36) NOT NULL,
                weight REAL NOT NULL DEFAULT 0,
                reps INT DEFAULT NULL,
                time TEXT DEFAULT NULL,
                PRIMARY KEY (timestamp, user_id, exercise_name, exercise_created_by_user_id),
                FOREIGN KEY (user_id)
                REFERENCES users(user_id)
                    ON DELETE CASCADE,
                FOREIGN KEY (exercise_name, exercise_created_by_user_id)
                REFERENCES exercises(exercise_name, created_by_user_id)
            )
        """.trimIndent()
        db.execSQL((createWorkingSetsTable))

        @Language("RoomSql") val createWorkingSetDeletionsTable = """
            CREATE TABLE working_set_deletions(
                timestamp VARCHAR(25) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                user_id VARCHAR(36) NOT NULL,
                exercise_name VARCHAR(50) NOT NULL,
                exercise_created_by_user_id VARCHAR(36) NOT NULL,
                PRIMARY KEY (timestamp, user_id, exercise_name, exercise_created_by_user_id),
                FOREIGN KEY (timestamp, user_id, exercise_name, exercise_created_by_user_id)
                REFERENCES working_sets(timestamp, user_id, exercise_name, created_by_user_id)
                    ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL((createWorkingSetDeletionsTable))

        @Language("RoomSql") val createExerciseNotesTable = """
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
            )
        """.trimIndent()
        db.execSQL(createExerciseNotesTable)

        // shortest kotlin variable name
        @Language("RoomSql") val createWorkingSetsPerMuscleGroupPerWeekTargetsTable = """
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
            )
        """.trimIndent()
        db.execSQL(createWorkingSetsPerMuscleGroupPerWeekTargetsTable)
    }

    companion object {
        const val DATABASE_NAME = "rk_user.db"
        const val DATABASE_VERSION = 1
    }
}

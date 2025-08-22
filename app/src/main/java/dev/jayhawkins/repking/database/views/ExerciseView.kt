package dev.jayhawkins.repking.database.views

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import dev.jayhawkins.repking.database.exceptions.EmptyCursorException
import org.intellij.lang.annotations.Language

class ExerciseView : DatabaseView {
    var exerciseName: String = ""
    var createdByUserId: String = ""
    var createdByUserName: String = ""
    var musclesWorked: List<String> = emptyList()
    var isIsometric: Boolean = false
    var notes: String = ""

    @Language("RoomSql") override val viewQuery: String = """
        SELECT e.exercise_name, e.is_isometric, ewg.muscle_group_name, en.exercise_note,
            name AS created_by
        FROM exercises e LEFT JOIN exercise_works_group ewg ON e.exercise_name = ewg.exercise_name AND
            e.created_by_user_id = ewg.created_by_user_id
            LEFT JOIN exercise_notes en ON e.exercise_name = en.exercise_name AND
            e.created_by_user_id = en.created_by_user_id
            LEFT JOIN (
                ${UserView.GENERIC_VIEW_QUERY}
            ) UserSubquery ON UserSubquery.user_id = e.created_by_user_id
        WHERE NOT EXISTS (
            SELECT *
            FROM exercise_deletions ed
            WHERE ed.exercise_name = e.exercise_name
                AND ed.created_by_user_id = e.created_by_user_id)
            AND e.exercise_name = ?
            AND e.created_by_user_id = ?
    """.trimIndent()

    constructor(exerciseName: String, createdByUserId: String, db: SQLiteDatabase) {
        this.exerciseName = exerciseName
        this.createdByUserId = createdByUserId
        selectionArgs = arrayOf(exerciseName, createdByUserId)
        execQuery(db)
    }

    private constructor(exerciseName: String, user: UserView, musclesWorked: List<String>, isIsometric: Boolean = false) {
        this.exerciseName = exerciseName
        this.createdByUserId = user.userId
        this.createdByUserName = user.usernameCandidates.first() ?: ""
        this.musclesWorked = musclesWorked
        this.isIsometric = isIsometric
    }

    override fun loadView(cur: Cursor) {
        if(!cur.moveToFirst())
            throw EmptyCursorException("Exercise \"$exerciseName\" by user \"$createdByUserId\" does not exist")

        createdByUserName = cur.getString(cur.getColumnIndexOrThrow("created_by"))
        notes = cur.getStringOrNull(cur.getColumnIndexOrThrow("exercise_note")) ?: ""
        val mw = mutableListOf<String>()
        do {
            mw.add(cur.getString(cur.getColumnIndexOrThrow("muscle_group_name")))
        } while (cur.moveToNext())

        musclesWorked = mw
    }

    companion object {
        val INSERT_EXERCISE_QUERY = """
            INSERT INTO exercises(exercise_name, timestamp, created_by_user_id, is_isometric)
        """.trimIndent()
        // Store initial exercise names in a JSON friendly format for future i18n
        val INITIAL_EXERCISES = listOf(
            ExerciseView("flat-barbell-bench-press", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST, MuscleGroupView.TRICEPS
            )),
            ExerciseView("incline-barbell-bench-press", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST, MuscleGroupView.TRICEPS, MuscleGroupView.SHOULDERS
            )),
            ExerciseView("flat-dumbbell-bench-press", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST, MuscleGroupView.TRICEPS
            )),
            ExerciseView("incline-dumbbell-bench-press", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST, MuscleGroupView.TRICEPS, MuscleGroupView.SHOULDERS
            )),
            ExerciseView("barbell-rows", UserView.DEV_USER, listOf(
                MuscleGroupView.LATS, MuscleGroupView.TRAPS
            )),
            ExerciseView("one-arm-dumbbell-rows", UserView.DEV_USER, listOf(
                MuscleGroupView.LATS, MuscleGroupView.TRAPS
            )),
            ExerciseView("pull-ups", UserView.DEV_USER, listOf(
                MuscleGroupView.LATS, MuscleGroupView.BICEPS
            )),
            ExerciseView("dips", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST, MuscleGroupView.TRICEPS, MuscleGroupView.SHOULDERS
            )),
            ExerciseView("preacher-curls", UserView.DEV_USER, listOf(
                MuscleGroupView.BICEPS
            )),
            ExerciseView("dumbbell-shoulder-press", UserView.DEV_USER, listOf(
                MuscleGroupView.SHOULDERS, MuscleGroupView.TRICEPS
            )),
            ExerciseView("chin-ups", UserView.DEV_USER, listOf(
                MuscleGroupView.LATS, MuscleGroupView.TRAPS
            )),
            ExerciseView("barbell-back-squat", UserView.DEV_USER, listOf(
                MuscleGroupView.GLUTES, MuscleGroupView.QUADS
            )),
            ExerciseView("deadlift", UserView.DEV_USER, listOf(
                MuscleGroupView.GLUTES, MuscleGroupView.HAMSTRINGS, MuscleGroupView.LOWER_BACK
            )),
            ExerciseView("leg-press", UserView.DEV_USER, listOf(
                MuscleGroupView.GLUTES, MuscleGroupView.QUADS
            )),
            ExerciseView("bulgarian-split-squat", UserView.DEV_USER, listOf(
                MuscleGroupView.GLUTES, MuscleGroupView.QUADS, MuscleGroupView.HIPS
            )),
            ExerciseView("leg-extension", UserView.DEV_USER, listOf(
                MuscleGroupView.QUADS
            )),
            ExerciseView("leg-curl", UserView.DEV_USER, listOf(
                MuscleGroupView.HAMSTRINGS
            )),
            ExerciseView("dumbbell-flyes", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST
            )),
            ExerciseView("machine-flyes", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST
            )),
            ExerciseView("machine-press", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST, MuscleGroupView.TRICEPS
            )),
            ExerciseView("dumbbell-one-arm-skullcrusher", UserView.DEV_USER, listOf(
                MuscleGroupView.TRICEPS
            )),
            ExerciseView("rope-triceps-pushdown", UserView.DEV_USER, listOf(
                MuscleGroupView.TRICEPS
            )),
            ExerciseView("bar-triceps-pushdown", UserView.DEV_USER, listOf(
                MuscleGroupView.TRICEPS
            )),
            ExerciseView("dumbbell-behind-the-back-extensions", UserView.DEV_USER, listOf(
                MuscleGroupView.TRICEPS
            )),
            ExerciseView("seated-machine-dips", UserView.DEV_USER, listOf(
                MuscleGroupView.CHEST, MuscleGroupView.TRICEPS
            )),
            ExerciseView("cable-lat-raise", UserView.DEV_USER, listOf(
                MuscleGroupView.SHOULDERS
            )),
            ExerciseView("dumbbell-lat-raise", UserView.DEV_USER, listOf(
                MuscleGroupView.SHOULDERS
            )),
            ExerciseView("dumbbell-rear-delt-flyes", UserView.DEV_USER, listOf(
                MuscleGroupView.TRAPS, MuscleGroupView.SHOULDERS
            )),
            ExerciseView("cable-lat-pulldowns", UserView.DEV_USER, listOf(
                MuscleGroupView.LATS,
            )),
            ExerciseView("cable-rows", UserView.DEV_USER, listOf(
                MuscleGroupView.LATS
            )),
            ExerciseView("straight-arm-lat-pulldown", UserView.DEV_USER, listOf(
                MuscleGroupView.LATS
            )),
            ExerciseView("dumbbell-shrug", UserView.DEV_USER, listOf(
                MuscleGroupView.TRAPS
            )),
            ExerciseView("barbell-shrug", UserView.DEV_USER, listOf(
                MuscleGroupView.TRAPS
            )),
            ExerciseView("face-pulls", UserView.DEV_USER, listOf(
                MuscleGroupView.SHOULDERS
            )),
            ExerciseView("cable-curls", UserView.DEV_USER, listOf(
                MuscleGroupView.BICEPS
            )),
            ExerciseView("concentration-curls", UserView.DEV_USER, listOf(
                MuscleGroupView.BICEPS
            )),
            ExerciseView("cable-wrist-curls", UserView.DEV_USER, listOf(
                MuscleGroupView.FOREARMS
            )),
            ExerciseView("romanian-deadlifts", UserView.DEV_USER, listOf(
                MuscleGroupView.GLUTES, MuscleGroupView.LOWER_BACK
            )),
            ExerciseView("calf-raises", UserView.DEV_USER, listOf(
                MuscleGroupView.CALVES
            )),
            ExerciseView("barbell-hip-thrust", UserView.DEV_USER, listOf(
                MuscleGroupView.GLUTES, MuscleGroupView.HAMSTRINGS, MuscleGroupView.QUADS
            )),
            ExerciseView("plank", UserView.DEV_USER, listOf(
                MuscleGroupView.CORE
            ), true),
        )
    }
}
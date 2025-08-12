package dev.jayhawkins.repking.database.views

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.intellij.lang.annotations.Language

class ExerciseView : DatabaseView {
    var exerciseName: String = ""
    var createdByUserId: String = ""
    var createdByUserName: String = ""

    // TODO: Add timestamp to exercise and exercise deletion table
    @Language("RoomSql") override val viewQuery: String = """
        SELECT e.exercise_name, e.is_isometric, ewg.muscle_group_name, en.exercise_note,
            un.user_name
        FROM exercises e LEFT JOIN exercise_works_group ewg ON e.exercise_name = ewg.exercise_name AND
            e.created_by_user_id = ewg.created_by_user_id
            LEFT JOIN exercise_notes en ON e.exercise_name = en.exercise_name AND
            e.created_by_user_id = en.created_by_user_id
            LEFT JOIN (${UserView.GENERIC_VIEW_QUERY}) ON u.user_id = e.created_by_user_id
        WHERE NOT EXISTS (
            SELECT *
            FROM exercise_deletions ed
            WHERE ed.exercise_name = e.exercise_name
                AND ed.created_by_user_id = e.created_by_user_id)
            AND e.exercise_name = ?
            AND e.created_by_user_id = ?
    """.trimIndent()

    constructor(exerciseName: String, createdByUserId: String) {
        selectionArgs = arrayOf(exerciseName, createdByUserId)
    }

    override fun loadView(cur: Cursor) {
        TODO("Not yet implemented")
    }
}
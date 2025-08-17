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

    private constructor(exerciseName: String, user: UserView, musclesWorked: List<String>) {
        this.exerciseName = exerciseName
        this.createdByUserId = user.userId
        this.createdByUserName = user.usernameCandidates.first() ?: ""
        this.musclesWorked = musclesWorked
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
        val INITIAL_EXERCISES = listOf(
            ExerciseView("Flat Barbell Bench Press", UserView.DEV_USER, listOf(
                // TODO: fill with muscle group constants
            )),
        )
    }
}
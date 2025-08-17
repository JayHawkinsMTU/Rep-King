package dev.jayhawkins.repking.database.views

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class MuscleGroupView : DatabaseView {
    // TODO: view query should contain muscle group name and all exercises that work the group
    override val viewQuery: String = """
        
    """.trimIndent()

    var muscleGroupName: String = ""
    var exercises: List<String> = emptyList()

    constructor(muscleGroupName: String, db: SQLiteDatabase) {
        this.muscleGroupName = muscleGroupName
        selectionArgs = arrayOf(muscleGroupName)
        execQuery(db)
    }

    override fun loadView(cur: Cursor) {
        TODO("Not yet implemented")
    }

    companion object {
        // Have all muscle group names as constants to prevent spelling mistakes
        const val BICEPS = "biceps"
        const val CALVES = "calves"
        const val CHEST = "chest"
        const val CORE = "core"
        const val FOREARMS = "forearms"
        const val GLUTES = "glutes"
        const val HAMSTRINGS = "hamstrings"
        const val HIPS = "hips"
        const val LATS = "lats"
        const val LOWER_BACK = "lower-back"
        const val SHOULDERS = "shoulders"
        const val QUADS = "quads"
        const val TRAPS = "traps"
        const val TRICEPS = "triceps"
    }
}
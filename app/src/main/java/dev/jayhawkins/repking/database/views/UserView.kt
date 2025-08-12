package dev.jayhawkins.repking.database.views

import dev.jayhawkins.repking.database.exceptions.UninitializedMutablePropertyException
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import dev.jayhawkins.repking.database.exceptions.MutablePropertyConflictException
import org.intellij.lang.annotations.Language

class UserView : DatabaseView {
    var usernameCandidates: Array<String?> = emptyArray()
    var usernameId: String = ""
    var userId: String = ""

    @Language("RoomSql") override val viewQuery: String = """
        $GENERIC_VIEW_QUERY
        AND u.user_id = ?
    """.trimIndent()

    constructor(userId: String, db: SQLiteDatabase) {
        selectionArgs = arrayOf(userId)
        execQuery(db)
    }

    override fun loadView(cur: Cursor) {
        val rowCount = cur.count

        if (rowCount == 0 || !cur.moveToFirst())
            throw UninitializedMutablePropertyException(
                "$userId has no attached username. Are they a new user?"
            )

        // Assign values
        usernameCandidates = arrayOfNulls(rowCount)
        usernameId = cur.getString(cur.getColumnIndexOrThrow("user_name_id"))
        userId = cur.getString(cur.getColumnIndexOrThrow("user_id"))
        do {
            usernameCandidates[cur.position] = cur.getString(cur.getColumnIndexOrThrow("name"))
        } while (cur.moveToNext())

        // Handle exception last so we can see candidates
        if (rowCount > 1)
            throw MutablePropertyConflictException(
                "$userId has two or more username candidates ${usernameCandidates.joinToString(",") }}"
            )
    }

    companion object {
        @Language("RoomSql") const val GENERIC_VIEW_QUERY = """
        SELECT u.user_id, name, user_name_id
        FROM users u LEFT JOIN user_names un ON u.user_id = un.user_id
        WHERE NOT EXISTS (SELECT *
                FROM user_names
                WHERE user_names.prior_user_name_id = un.user_name_id)
        """
    }
}
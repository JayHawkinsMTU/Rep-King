package dev.jayhawkins.repking.database.views

import dev.jayhawkins.repking.database.exceptions.UninitializedMutablePropertyException
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import dev.jayhawkins.repking.database.exceptions.EmptyCursorException
import dev.jayhawkins.repking.database.exceptions.MutablePropertyConflictException
import org.intellij.lang.annotations.Language

/**
 * A static view of a user from a SQL query
 */
class UserView : DatabaseView {
    // Length is 1 in most cases. 0 if uninitialized. 2 if conflict.
    var usernameCandidates: List<String?> = emptyList()
    // ID of user name property. Select first on conflict.
    var usernameId: String = ""
    var userId: String = ""

    @Language("RoomSql") override val viewQuery: String = """
        $GENERIC_VIEW_QUERY
        AND u.user_id = ?
    """.trimIndent()

    constructor(userId: String, db: SQLiteDatabase) {
        selectionArgs = arrayOf(userId)
        this.userId = userId
        execQuery(db)
    }

    // Use only for constants like DEV_USER
    private constructor(userId: String, username: String) {
        usernameCandidates = mutableListOf(username)
        this.userId = userId
    }

    override fun loadView(cur: Cursor) {
        val rowCount = cur.count

        if (!cur.moveToFirst())
            throw EmptyCursorException("User with ID \"$userId\" doesn't exist.")

        if (cur.getStringOrNull(cur.getColumnIndexOrThrow("name")) == null)
            throw UninitializedMutablePropertyException(
                "$userId has no attached username. Are they a new user?"
            )

        // Assign values
        usernameId = cur.getString(cur.getColumnIndexOrThrow("user_name_id"))
        val unc = mutableListOf<String>()
        do {
            unc.add(cur.getString(cur.getColumnIndexOrThrow("name")))
        } while (cur.moveToNext())

        usernameCandidates = unc

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

        val DEV_USER = UserView("dev", "Jay")
    }
}
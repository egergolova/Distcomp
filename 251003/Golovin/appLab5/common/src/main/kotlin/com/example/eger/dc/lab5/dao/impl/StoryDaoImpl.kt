package com.example.eger.dc.lab5.dao.impl

import com.example.eger.dc.lab5.bean.Story
import com.example.eger.dc.lab5.dao.StoryDao
import com.example.eger.dc.lab5.database.Stories
import com.example.eger.dc.lab5.id
import com.example.eger.dc.lab5.testViaRedis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Timestamp

class StoryDaoImpl(private val connection: Connection) : StoryDao {
	init {
		val statement = connection.createStatement()
		statement.executeUpdate(Stories.CREATE_TABLE_STORIES)
	}

	private fun ResultSet.getString(value: Stories): String = getString("$value")
	private fun ResultSet.getLong(value: Stories): Long = getLong("$value")
	private fun ResultSet.getTimestamp(value: Stories): Timestamp = getTimestamp("$value")

	override suspend fun create(item: Story): Long = withContext(Dispatchers.IO) {
		testViaRedis("${id++}", item.title)

		val statement = connection.prepareStatement(Stories.INSERT_STORY, Statement.RETURN_GENERATED_KEYS)
		statement.apply {
			setLong(1, item.authorId)
			setString(2, item.title)
			setString(3, item.content)
			setTimestamp(4, item.created)
			setTimestamp(5, item.modified)
			executeUpdate()
		}

		val generatedKeys = statement.generatedKeys
		if (generatedKeys.next()) {
			return@withContext generatedKeys.getLong(1)
		} else {
			throw Exception("Unable to retrieve the id of the newly inserted item.")
		}
	}

	override suspend fun deleteById(id: Long): Int = withContext(Dispatchers.IO) {
		val statement = connection.prepareStatement(Stories.DELETE_STORY)
		statement.setLong(1, id)

		return@withContext try {
			statement.executeUpdate()
		} catch (_: Exception) {
			throw Exception("Can not delete item record")
		}
	}

	override suspend fun getAll(): List<Story?> = withContext(Dispatchers.IO) {
		val result = mutableListOf<Story>()
		val statement = connection.prepareStatement(Stories.SELECT_STORIES)

		val resultSet = statement.executeQuery()
		while (resultSet.next()) {
			val id = resultSet.getLong(Stories.COLUMN_ID)
			val authorId = resultSet.getLong(Stories.COLUMN_AUTHOR_ID)
			val title = resultSet.getString(Stories.COLUMN_TITLE)
			val content = resultSet.getString(Stories.COLUMN_CONTENT)
			val created = resultSet.getTimestamp(Stories.COLUMN_CREATED)
			val modified = resultSet.getTimestamp(Stories.COLUMN_MODIFIED)
			result.add(Story(id, authorId, title, content, created, modified))
		}

		result
	}

	override suspend fun getById(id: Long): Story = withContext(Dispatchers.IO) {
		val statement = connection.prepareStatement(Stories.SELECT_STORY_BY_ID)
		statement.setLong(1, id)

		val resultSet = statement.executeQuery()
		if (resultSet.next()) {
			val authorId = resultSet.getLong(Stories.COLUMN_AUTHOR_ID)
			val title = resultSet.getString(Stories.COLUMN_TITLE)
			val content = resultSet.getString(Stories.COLUMN_CONTENT)
			val created = resultSet.getTimestamp(Stories.COLUMN_CREATED)
			val modified = resultSet.getTimestamp(Stories.COLUMN_MODIFIED)
			return@withContext Story(id, authorId, title, content, created, modified)
		} else {
			throw Exception("Item record not found.")
		}
	}

	override suspend fun update(item: Story): Int = withContext(Dispatchers.IO) {
		val statement = connection.prepareStatement(Stories.UPDATE_STORY)
		statement.apply {
			setLong(1, item.authorId)
			setString(2, item.title)
			setString(3, item.content)
			setTimestamp(4, item.created)
			setTimestamp(5, item.modified)
			item.id?.let { setLong(6, it) }
		}

		return@withContext try {
			statement.executeUpdate()
		} catch (_: Exception) {
			throw Exception("Can not modify item record")
		}
	}
}
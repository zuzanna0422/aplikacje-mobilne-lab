package pl.wsei.pam.lab06.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoTaskDao {
    @Insert
    suspend fun insertAll(vararg tasks: TodoTaskEntity)

    @Delete
    suspend fun removeById(item: TodoTaskEntity)

    @Update
    suspend fun update(item: TodoTaskEntity)

    @Query("Select * from tasks ORDER BY deadline DESC")
    fun findAll(): Flow<List<TodoTaskEntity>>

    @Query("Select * from tasks where id == :id")
    fun find(id: Int): Flow<TodoTaskEntity>
}

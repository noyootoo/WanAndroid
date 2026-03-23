package com.example.wanandroid.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    // 插入列表，冲突时替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    // 获取所有文章，返回 Flow 响应式数据，按照插入索引升序排序（保证和网络返回的顺序完全一致）
    @Query("SELECT * FROM articles ORDER BY insert_index ASC")
    fun getAllArticlesFlow(): Flow<List<ArticleEntity>>

    // 清空文章表
    @Query("DELETE FROM articles")
    suspend fun clearAll()

    // 更新某篇文章的已读状态
    @Query("UPDATE articles SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: Int, isRead: Boolean)

    // 更新某篇文章的收藏状态
    @Query("UPDATE articles SET collect = :collect WHERE id = :id")
    suspend fun updateCollectStatus(id: Int, collect: Boolean)
}

package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Project Entity
@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "Kotlin Android", "React Native", "Flutter", "HTML/CSS/JS"
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val category: String = "Mobile", // "Mobile", "Web", "AI", "Game"
    val appIconAsset: String = "ic_launcher_foreground",
    val packageId: String = "com.example.orinide"
)

// Project Code File Entity
@Entity(
    tableName = "project_files",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class ProjectFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val filePath: String, // Relative path from project root (e.g. "src/MainActivity.kt")
    val content: String,
    val language: String, // "kotlin", "javascript", "json", "xml", "html", "css", "python"
    val lastUpdated: Long = System.currentTimeMillis()
)

// Build History / APk Log Entity
@Entity(
    tableName = "build_records",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class BuildRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val status: String, // "BUILDING", "SUCCESS", "FAILED"
    val logs: String,
    val apkSizeStr: String = "",
    val apkName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 0
)

// Data Access Object
@Dao
interface OrinDao {
    // Project Operations
    @Query("SELECT * FROM projects ORDER BY lastModified DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    // File Operations
    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY filePath ASC")
    fun getFilesByProject(projectId: Long): Flow<List<ProjectFile>>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId AND filePath = :filePath LIMIT 1")
    suspend fun getFileByPath(projectId: Long, filePath: String): ProjectFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFile): Long

    @Update
    suspend fun updateFile(file: ProjectFile)

    @Delete
    suspend fun deleteFile(file: ProjectFile)

    @Query("DELETE FROM project_files WHERE projectId = :projectId AND filePath = :filePath")
    suspend fun deleteFileByPath(projectId: Long, filePath: String)

    // Build Records
    @Query("SELECT * FROM build_records WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getBuildRecords(projectId: Long): Flow<List<BuildRecord>>

    @Query("SELECT * FROM build_records ORDER BY timestamp DESC")
    fun getAllBuildRecords(): Flow<List<BuildRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildRecord(record: BuildRecord): Long

    @Query("DELETE FROM build_records WHERE projectId = :projectId")
    suspend fun clearBuildHistory(projectId: Long)
}

// Database Class
@Database(
    entities = [Project::class, ProjectFile::class, BuildRecord::class],
    version = 1,
    exportSchema = false
)
abstract class OrinDatabase : RoomDatabase() {
    abstract fun orinDao(): OrinDao

    companion object {
        @Volatile
        private var INSTANCE: OrinDatabase? = null

        fun getDatabase(context: Context): OrinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrinDatabase::class.java,
                    "orin_ide_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

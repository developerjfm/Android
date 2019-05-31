package br.iesb.android.opiniaodetudo.persistence

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import br.iesb.android.opiniaodetudo.model.Review

//Essa classe é só uma configuração, o ROM cria o database e retorna o DAO pra mim
@Database(entities = arrayOf(Review::class), version = 4, exportSchema = false)
abstract class ReviewDatabase : RoomDatabase(){
    companion object {
        private var instance: ReviewDatabase? = null
        private var migration_2_3 = object: Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_PHOTO_PATH} TEXT")
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_THUMBNAIL} BLOB")
            }
        }
        private var migration_3_4 = object: Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_LATITUDE} REAL")
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_LONGITUDE} REAL")
            }
        }
        fun getInstance(context: Context): ReviewDatabase {
            if(instance == null){
                instance = Room
                    .databaseBuilder(context, ReviewDatabase::class.java, "review_database")
                    .addMigrations(migration_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
    abstract fun reviewDao():ReviewDao
}
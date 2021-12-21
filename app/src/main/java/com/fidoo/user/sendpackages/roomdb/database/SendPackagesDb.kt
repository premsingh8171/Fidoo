package com.fidoo.user.sendpackages.roomdb.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fidoo.user.cartview.roomdb.dao.PrescriptionDao
import com.fidoo.user.sendpackages.roomdb.dao.SendPackagesDao
import com.fidoo.user.sendpackages.roomdb.entity.SendPackagesItemImage

@Database(entities = [SendPackagesItemImage::class], version = 1,exportSchema = false)
abstract class SendPackagesDb : RoomDatabase() {
    companion object{
        const val DB_NAME = "SendPackages_db"
        const val TABLE_NAME = "SendPackagesItemImg_table"
    }
    abstract fun sendPackagesDao(): SendPackagesDao?


}
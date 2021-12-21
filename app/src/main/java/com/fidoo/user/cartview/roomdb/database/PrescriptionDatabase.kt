package com.fidoo.user.cartview.roomdb.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fidoo.user.cartview.roomdb.dao.PrescriptionDao
import com.fidoo.user.cartview.roomdb.entity.PrescriptionViewEntity
import com.fidoo.user.grocery.roomdatabase.dao.ProductsDao

@Database(entities = [PrescriptionViewEntity::class], version = 3,exportSchema = false)
abstract class PrescriptionDatabase:RoomDatabase() {
    companion object{
        const val DB_NAME = "Prescription_db"
        const val TABLE_NAME = "Prescription_table"
    }
    abstract fun prescriptionDao(): PrescriptionDao?
}
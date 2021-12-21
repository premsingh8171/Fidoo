package com.fidoo.user.cartview.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fidoo.user.cartview.roomdb.database.PrescriptionDatabase
import com.fidoo.user.cartview.roomdb.entity.PrescriptionViewEntity
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase

@Dao
interface PrescriptionDao {
    @Insert
    fun insertPrescriptionView( vararg prescription: PrescriptionViewEntity)

    @Query("SELECT * FROM " + PrescriptionDatabase.TABLE_NAME)
    fun getPrescriptionView(): LiveData<List<PrescriptionViewEntity>>

    @Query("UPDATE "+ PrescriptionDatabase.TABLE_NAME +" SET Prescription_url=:image, file_path=:file_path, document_id=:document_id WHERE pres_id = :pres_id")
    fun updatePrescriptionView(pres_id: Int?, image: String,file_path: String,document_id: String)

    @Query("DELETE FROM Prescription_table "+" WHERE pres_id = :pres_id")
    fun deleteItem(pres_id:Int)

    @Query("DELETE FROM Prescription_table")
    fun deleteAllItem()
}
package com.fidoo.user.cartview.roomdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Prescription_table")

class PrescriptionViewEntity(
    @ColumnInfo(name = "pres_id") var pres_id: String = "",
    @ColumnInfo(name = "Prescription_url") var Prescription_url: String = "",
    @ColumnInfo(name = "file_path") var file_path: String = "",
    @ColumnInfo(name = "document_id") var document_id: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
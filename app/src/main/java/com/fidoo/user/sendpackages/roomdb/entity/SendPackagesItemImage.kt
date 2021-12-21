package com.fidoo.user.sendpackages.roomdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SendPackagesItemImg_table")
class SendPackagesItemImage(
    @ColumnInfo(name = "package_imgId") var package_imgId: String = "",
    @ColumnInfo(name = "package_url") var package_url: String = "",
    @ColumnInfo(name = "file_path") var file_path: String = "",
    @ColumnInfo(name = "document_id") var document_id: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
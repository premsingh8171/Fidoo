package com.fidoo.user.sendpackages.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fidoo.user.sendpackages.roomdb.database.SendPackagesDb
import com.fidoo.user.sendpackages.roomdb.entity.SendPackagesItemImage

@Dao
interface SendPackagesDao {
    @Insert
    fun insertSendPackagesView( vararg prescription: SendPackagesItemImage)

    @Query("SELECT * FROM " + SendPackagesDb.TABLE_NAME)
    fun getSendPackagesView(): LiveData<List<SendPackagesItemImage>>

    @Query("UPDATE "+ SendPackagesDb.TABLE_NAME +" SET package_url=:image, file_path=:file_path, document_id=:document_id WHERE package_imgId = :package_imgId")
    fun updateSendPackagesView(package_imgId: Int?, image: String,file_path: String,document_id: String)

    @Query("DELETE FROM SendPackagesItemImg_table "+" WHERE package_imgId = :package_imgId")
    fun deleteItem(package_imgId:Int)

    @Query("DELETE FROM SendPackagesItemImg_table")
    fun deleteAllItem()
}
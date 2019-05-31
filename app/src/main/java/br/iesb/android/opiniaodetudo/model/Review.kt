package br.iesb.android.opiniaodetudo.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity
data class Review(
    @PrimaryKey val id:String,
    val name:String,
    val review:String?,
    @ColumnInfo(name="photo_path")
    val photoPath: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val thumbnail: ByteArray?,
    @ColumnInfo(typeAffinity = ColumnInfo.REAL)
    var latitude: Double?,
    @ColumnInfo(typeAffinity = ColumnInfo.REAL)
    var longitude: Double?): Serializable {

    @Ignore
    constructor(id: String, name: String, review:String): this(id, name, review, null, null)

    @Ignore
    constructor( id: String, name: String, review:String, photoPath: String?, thumbnail: ByteArray?):
            this(id, name, review, photoPath, thumbnail, null, null)
}

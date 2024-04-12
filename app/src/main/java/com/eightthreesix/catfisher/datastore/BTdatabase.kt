package com.eightthreesix.catfisher.datastore

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.eightthreesix.catfisher.db.BluetoothDB

fun provideDatabase(context: Context): BluetoothDB {
    return BluetoothDB(
        driver = AndroidSqliteDriver(BluetoothDB.Schema, context, "btPairedDevices.db"/*,
            callback = object : AndroidSqliteDriver.Callback(BluetoothDB.Schema){
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }*/)
    )
}

package com.acnlab.altbeacon.pplntaipei.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static int database_version = 1;
    private static String database_name = "sample_db";
    private static String table_name = "ss_pos";
    private static String coloumn_id = "id";
    private static String coloumn_data_user = "data_user";
    private static String coloumn_data_pos = "data_pos";
    private static String coloumn_status = "data_status";
    private static String coloumn_timestamp = "timestamp";

    public static int DATA_SEMUA=0;
    public static int DATA_KIRIM=1;
    public static int DATA_TERIMA=2;

    //constructor
    public DatabaseHelper(Context context) {
        super(context, database_name, null, database_version);
    }

    public boolean haveDB(Context context) {
        File dbFile = context.getDatabasePath(database_name);
        return dbFile.exists();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + table_name + "(" + coloumn_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + coloumn_data_user + " TEXT,"
                + coloumn_data_pos + " TEXT,"
                + coloumn_status + " TEXT,"
                + coloumn_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(sqLiteDatabase);
    }

    public long insertData(String dataUser, String dataPos, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(coloumn_data_user,dataUser);
        cv.put(coloumn_data_pos,dataPos);
        cv.put(coloumn_status,status);
        long id = db.insert(table_name,null, cv);
        db.close();
        return id;
    }

    public int updateData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(coloumn_data_user, data.getDataUser());
        cv.put(coloumn_data_pos, data.getDataPos());
        cv.put(coloumn_status, data.getStatus());

        // updating row
        return db.update(table_name, cv, "id" + " = ?",
                new String[]{String.valueOf(data.getId())});
    }

    public Data getData(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(table_name,
                new String[]{coloumn_id, coloumn_data_user, coloumn_data_pos, coloumn_status, coloumn_timestamp},
                coloumn_id + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Data data = new Data(1,
                cursor.getInt(cursor.getColumnIndex(coloumn_id)),
                cursor.getString(cursor.getColumnIndex(coloumn_data_user)),
                cursor.getString(cursor.getColumnIndex(coloumn_data_pos)),
                cursor.getString(cursor.getColumnIndex(coloumn_status)),
                cursor.getString(cursor.getColumnIndex(coloumn_timestamp)));

        // close the db connection
        cursor.close();

        return data;
    }

    public boolean cekDataKirim(String userCode, String userPost) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorUser= db.rawQuery("SELECT "+coloumn_id+" FROM "+table_name+" WHERE "+coloumn_status+"= ? AND "+coloumn_data_user+" = ?; ", new String[] {"KIRIM",userCode});
        Cursor cursorPost= db.rawQuery("SELECT "+coloumn_id+" FROM "+table_name+" WHERE "+coloumn_status+"= ? AND "+coloumn_data_pos+" = ?; ", new String[] {"KIRIM",userPost});

        boolean yangPost=false;
        if(cursorPost!=null){
            yangPost=(cursorPost.getCount()==0 );
        }

        boolean yangUser = false;
        if(cursorUser!=null){
            yangUser=(cursorUser.getCount()==0 );
        }


        cursorPost.close();
        cursorUser.close();

        return yangPost && yangUser;
    }

    public boolean cekDataTerima(String userCode) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorUser= db.rawQuery("SELECT "+coloumn_id+" FROM "+table_name+" WHERE "+coloumn_status+"= ? AND "+coloumn_data_user+" = ?; ", new String[] {"TERIMA",userCode});



        boolean yangTerimaUser = false;
        if(cursorUser!=null){
            yangTerimaUser=(cursorUser.getCount()==0 );
        }


        cursorUser.close();

        return yangTerimaUser;
    }

    public List<Data> getAllDatas(int typeData) {
        List<Data> Datas = new ArrayList<>();
        String selectQuery=selectQuery = "SELECT  * FROM " + table_name + " ORDER BY " +
                coloumn_timestamp + " DESC";;
        if(typeData==DATA_SEMUA){
            // Select All Query
            selectQuery = "SELECT  * FROM " + table_name + " ORDER BY " +
                    coloumn_timestamp + " DESC";

        }else if(typeData==DATA_KIRIM) {
            // Select All Query
            selectQuery = "SELECT  * FROM " + table_name +" WHERE "+coloumn_status+"='KIRIM' ORDER BY " +
                    coloumn_timestamp + " DESC";
        }else if(typeData==DATA_TERIMA){
            // Select All Query
            selectQuery = "SELECT  * FROM " + table_name +" WHERE "+coloumn_status+"='TERIMA' ORDER BY " +
                    coloumn_timestamp + " DESC";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        int i=cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                Data data = new Data();
                data.setUrutan(i);
                data.setId(cursor.getInt(cursor.getColumnIndex(coloumn_id)));
                data.setDataUser(cursor.getString(cursor.getColumnIndex(coloumn_data_user)));
                data.setDataPos(cursor.getString(cursor.getColumnIndex(coloumn_data_pos)));
                data.setStatus(cursor.getString(cursor.getColumnIndex(coloumn_status)));
                data.setTimestamp(cursor.getString(cursor.getColumnIndex(coloumn_timestamp)));
                i--;
                Datas.add(data);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return Datas;
    }

    public int getDataCount(int typeData) {
        String countQuery = "SELECT  * FROM " + table_name;
        if(typeData==DATA_SEMUA){
            countQuery = "SELECT  * FROM " + table_name;
        }else if(typeData==DATA_KIRIM){
            countQuery = "SELECT  * FROM " + table_name+" WHERE "+coloumn_status+"='KIRIM' ";
        }else if(typeData==DATA_TERIMA){
            countQuery = "SELECT  * FROM " + table_name+" WHERE "+coloumn_status+"='TERIMA' ";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public Cursor raw() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT  * FROM " + table_name , new String[]{});

        return res;
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ table_name);
        db.close();
    }

    public void deleteData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, coloumn_id + " = ?",
                new String[]{String.valueOf(data.getId())});
        db.close();
    }
}
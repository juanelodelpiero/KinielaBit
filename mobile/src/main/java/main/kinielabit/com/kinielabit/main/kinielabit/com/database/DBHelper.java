package main.kinielabit.com.kinielabit.main.kinielabit.com.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by juanenrique_ramirez on 8/3/15.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "kinielabit";
    private static final int DATABASE_VERSION = 1;

    private final String CREATE_CTLGO_SERVICIO = "";
//            "create table " + Servicios.TAB_NAME +
//            "(" + Servicios._ID_ROW + " integer primary key,"+ Servicios.ID_SERVICIO + " integer,"+
//            Servicios.ID_NOMBRE + " TEXT, "+Servicios.ID_IMAGEN+" TEXT)";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db){
    db.execSQL(CREATE_CTLGO_SERVICIO);
    }
}

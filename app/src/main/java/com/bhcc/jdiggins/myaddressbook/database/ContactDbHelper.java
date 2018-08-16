package com.bhcc.jdiggins.myaddressbook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bhcc.jdiggins.myaddressbook.database.ContactDbSchema.ContactTable;

import java.util.UUID;

/**
 * Created by JCDig on 3/31/2018.
 */

public class ContactDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "addressDb.db";

    public ContactDbHelper(Context context) {super(context, DATABASE_NAME, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ContactTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                ContactTable.Cols.ID + ", " +
                ContactTable.Cols.NAME + ", " +
                ContactTable.Cols.PHONE + ", " +
                ContactTable.Cols.EMAIL + ", " +
                ContactTable.Cols.STREET + ", " +
                ContactTable.Cols.CITY + ", " +
                ContactTable.Cols.STATE + ", " +
                ContactTable.Cols.ZIP + ")"
        );
        db.execSQL("insert into " + ContactTable.NAME + " values " +
            "('1', '" + UUID.randomUUID() + "', 'Tom Brady', '555-555-1212'," +
                " 'TB12@Patriots.com', '1 Patriot Place', 'Foxboro', 'Mass.'," +
                " '01234')");
        db.execSQL("insert into " + ContactTable.NAME + " values " +
                "('2', '" + UUID.randomUUID() + "', 'David Ortiz', '617-555-1212', 'DOrtiz@RedSox.com', " +
                "'1 Landsdown Street', 'Boston', 'Mass.', '01235')");
        db.execSQL("insert into " + ContactTable.NAME + " values " +
                "('3', '" + UUID.randomUUID() + "', 'Patrice Bergeron', '978-555-1212', " +
                "'PBergeron@BostonBruins.com','1 Causeway Street', " +
                "'Boston', 'Mass.', '01236')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

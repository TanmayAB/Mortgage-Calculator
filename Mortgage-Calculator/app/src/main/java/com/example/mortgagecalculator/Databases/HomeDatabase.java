package com.example.mortgagecalculator.Databases;

import android.provider.BaseColumns;

/**
 * Created by Student on 3/15/17.
 */

public final class HomeDatabase {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private HomeDatabase() {}

    /* Inner class that defines the table contents */
        public static final String _ID = "NO";
        public static final String TABLE_NAME = "HOMEDETAILS";
        public static final String COLUMN1_NAME_TITLE = "TYPE";
        public static final String COLUMN2_NAME_TITLE = "ADDRESS";
        public static final String COLUMN3_NAME_TITLE = "CITY";
        public static final String COLUMN4_NAME_TITLE = "STATE";
        public static final String COLUMN5_NAME_TITLE = "ZIP";
        public static final String COLUMN6_NAME_TITLE = "PRICE";
        public static final String COLUMN7_NAME_TITLE = "DOWNPAYMENT";
        public static final String COLUMN8_NAME_TITLE = "RATE";
        public static final String COLUMN9_NAME_TITLE = "TERM";
        public static final String COLUMN10_NAME_TITLE = "EMI";


    public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + HomeDatabase.TABLE_NAME + " (" +
                        HomeDatabase._ID + " INTEGER PRIMARY KEY," +
                        HomeDatabase.COLUMN1_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN2_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN3_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN4_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN5_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN6_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN7_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN8_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN9_NAME_TITLE + " TEXT," +
                        HomeDatabase.COLUMN10_NAME_TITLE + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + HomeDatabase.TABLE_NAME + ";\n" +
                        "DELETE FROM sqlite_sequence where name=" + HomeDatabase.TABLE_NAME;
    }

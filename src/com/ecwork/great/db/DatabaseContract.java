package com.ecwork.great.db;

import android.provider.BaseColumns;

/**
 * User: ecsark
 * Date: 2/8/14
 * Time: 9:19 PM
 */
public final class DatabaseContract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gr.db";

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    // prevent accidentally instantiating the contract class
    private DatabaseContract() {}

    public static String[] SQL_SETUP = {
            NodeT.CREATE_TABLE,
            NodeT.CREATE_INDEX,
            EdgeT.CREATE_TABLE,
            EdgeT.CREATE_INDEX
    };

    public static String[] SQL_TEARDOWN = {
            NodeT.DELETE_TABLE,
            EdgeT.DELETE_TABLE
    };


    public static abstract class NodeT implements BaseColumns {
        public static final String TABLE_NAME = "node";
        public static final String _TYPE = "type";
        public static final String _VALUE = "value";
        public static final String _TIME_CREATED = "timecreated";
        public static final String _TIME_MODIFIED = "timemodified";

        public static final String IDX_TYPE = "idx_type";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                _TYPE + INTEGER_TYPE + COMMA_SEP +
                _VALUE + TEXT_TYPE + COMMA_SEP +
                _TIME_CREATED + INTEGER_TYPE + COMMA_SEP +
                _TIME_MODIFIED + INTEGER_TYPE + " )";

        public static final String CREATE_INDEX = "CREATE INDEX " +
                IDX_TYPE + " ON " + TABLE_NAME + "(" + _TYPE + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class EdgeT implements BaseColumns {
        public static final String TABLE_NAME = "edge";
        public static final String _START = "start";
        public static final String _END = "end";
        public static final String _TYPE = "type";
        public static final String _VALUE = "value";

        public static final String IDX_START = TABLE_NAME + "idx_start";
        public static final String IDX_END = TABLE_NAME + "idx_end";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + INTEGER_TYPE+ " PRIMARY KEY" + COMMA_SEP +
                _START + INTEGER_TYPE + COMMA_SEP +
                _END + INTEGER_TYPE + COMMA_SEP +
                _TYPE + INTEGER_TYPE + COMMA_SEP +
                _VALUE + INTEGER_TYPE +" )";

        public static final String CREATE_INDEX = "CREATE INDEX " +
                IDX_START + " ON " + TABLE_NAME + "(" + _START + ");" +
                "CREATE INDEX " + IDX_END + " ON " + TABLE_NAME + "(" + _END + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }
}

package ir.Mikado;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "pearl";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_UID = "uid";
    private static final String KEY_PICTURE_PROFILE = "PictureProfile";
    private static final String KEY_CREATED_AT = "created_at";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " INTEGER," + KEY_FIRST_NAME + " TEXT," + KEY_LAST_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_PHONE_NUMBER + " TEXT," + KEY_UID + " TEXT,"
                + KEY_PICTURE_PROFILE + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(int UserID, String FirstName, String LastName, String Email, String PhoneNumber,
                        String uid, String PictureProfile, String Created_At) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, UserID); // USER ID
        values.put(KEY_FIRST_NAME, FirstName); // FirstName
        values.put(KEY_LAST_NAME, LastName); // LastName
        values.put(KEY_EMAIL, Email); // Email
        values.put(KEY_PHONE_NUMBER, PhoneNumber); // PhoneNumber
        values.put(KEY_UID, uid); // Email
        values.put(KEY_PICTURE_PROFILE, PictureProfile); // Picture Profile
        values.put(KEY_CREATED_AT, Created_At); // Created At


        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("user_id", cursor.getString(1));
            user.put("first_name", cursor.getString(2));
            user.put("last_name", cursor.getString(3));
            user.put("email", cursor.getString(4));
            user.put("phone_number", cursor.getString(5));
            user.put("uid", cursor.getString(6));
            user.put("picture_profile", cursor.getString(7));
            user.put("created_at", cursor.getString(8));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
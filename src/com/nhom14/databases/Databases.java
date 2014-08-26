package com.nhom14.databases;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databases extends SQLiteOpenHelper{
	
	
	public static int KT = 0;
	public Databases(Context context) {
		super(context, "Weather", null	, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createTableWeather = "CREATE  TABLE IF NOT EXISTS tableWeather ("+
				" resid INTEGER PRIMARY KEY AUTOINCREMENT,"+
				" cityName text ,"+
				" date text ,"+
				" day text ,"+
				" hight text,"+
				" low text,"+
				" conditions text,"+
				" imgUrl text)";
				;
		db.beginTransaction();
		try{
			
			db.execSQL(createTableWeather);
			
			db.setTransactionSuccessful();
		}catch (Exception e){
			KT = 10;
		}finally {
			db.endTransaction();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        onCreate(db);
	}

	public void addWeatherData(String cityName, String date, String day, 
			String hight, String low, String conditions, String imgUrl) {
		SQLiteDatabase db = this.getWritableDatabase();
		// Kiểm tra sự trùng lặp dữ liệu trong 1 ngày. Chỉ lưu lại lần đầu tiên
		try {
			String qr = "select date from tableWeather where cityName = '"+cityName+"'";
			String []array = null;
		    Cursor cursor = db.rawQuery(qr, array);
			cursor.moveToFirst();
			@SuppressWarnings("unused")
			String test = cursor.getString(0);
			return;
		}
		catch (Exception e ){
			
		}
		
		ContentValues values = new ContentValues();
		values.put("cityName", cityName);
		values.put("date", date);
		values.put("day", day);
		values.put("hight", hight);
		values.put("low", low);
		values.put("conditions", conditions);
		values.put("imgUrl", imgUrl);
		
		db.insert("tableWeather", null, values);
		db.close();
		
	}

	
	public ArrayList<ArrayList<String>> getData() {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		try {
			String qr = "select * from tableWeather";
			SQLiteDatabase db = this.getReadableDatabase();
			String []array = null;
		    Cursor cursor = db.rawQuery(qr, array);
			cursor.moveToFirst();
			do {
				ArrayList<String> items = new ArrayList<String>();
				items.add(cursor.getString(1));
				items.add(cursor.getString(2));
				items.add(cursor.getString(3));
				items.add(cursor.getString(4));
				items.add(cursor.getString(5));
				items.add(cursor.getString(6));
				items.add(cursor.getString(7));
				result.add(items);
			} while (cursor.moveToNext());
			
		}
		catch (Exception e ){
			return null;
		}
		return result;
	}
	
}

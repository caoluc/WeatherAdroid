package com.nhom14.weatherandroid;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherSave extends Activity{
	@Override
	public void onCreate(Bundle mainBunder) {
		super.onCreate(mainBunder);
		setContentView(R.layout.tablesave);
		TableLayout tableLayout = (TableLayout)findViewById(R.id.tablesave);
		// Set tableRow
		ArrayList<ArrayList<String>> arrData = new ArrayList<ArrayList<String>>();
		arrData = WeatherActivity.myDB.getData();
		try {
			for (int i=0; i < arrData.size(); i++) {
				// Hiển thị thông tin dòng 1
				TableRow rowInfo = new TableRow(this);
				
				TextView name = new TextView(this);
				name.setText("\n"+arrData.get(i).get(0));
				name.setTextColor(Color.WHITE);
				name.setPadding(0, 0, 0, 1);
				TextView date = new TextView(this);
				date.setText(arrData.get(i).get(1));
				date.setTextColor(Color.WHITE);
				date.setGravity(Gravity.CENTER_HORIZONTAL);
				
				
				TextView day = new TextView(this);
				day.setText("\n"+arrData.get(i).get(2));
				day.setTextColor(Color.WHITE);
				day.setGravity(Gravity.CENTER_HORIZONTAL);
				
				TextView highs = new TextView(this);  
			    highs.setText("\n"+arrData.get(i).get(3)+"°C");
			    highs.setTextColor(Color.WHITE);
			    highs.setGravity(Gravity.CENTER_HORIZONTAL);
			    
			    TextView lows = new TextView(this);  
			    lows.setText("\n"+arrData.get(i).get(4)+"°C");  
			    lows.setTextColor(Color.WHITE);
			    lows.setGravity(Gravity.CENTER_HORIZONTAL);
			    
			    ImageView conditions = new ImageView(this);
			    Drawable imageDrawable = Drawable.createFromPath(arrData.get(i).get(6));
			    conditions.setImageDrawable(imageDrawable);
			    conditions.setMinimumHeight(50);
		        //---------Chèn view vào hàng 1----------------------
				rowInfo.addView(name);  
			    rowInfo.addView(day);  
			    rowInfo.addView(highs);  
			    rowInfo.addView(lows);  
			    rowInfo.addView(conditions);
			    
			    // Dòng 2 hiện thông tin conditions
			    TableRow row2Info = new TableRow(this);
			    TextView name2 = new TextView(this);
				TextView highs2 = new TextView(this);  
			    TextView lows2 = new TextView(this);  
			    
			    TextView conditions2 = new TextView(this);
			    conditions2.setText(arrData.get(i).get(5));
			    conditions2.setTextColor(Color.WHITE);
			    conditions2.setGravity(Gravity.CENTER_HORIZONTAL);
			    //--Chèn các view vào hàng 2
			    row2Info.addView(name2);
			    row2Info.addView(date);
			    row2Info.addView(highs2);
			    row2Info.addView(lows2);
			    row2Info.addView(conditions2);
			    
			    tableLayout.addView(rowInfo);
			    tableLayout.addView(row2Info);
			}
		} catch (NullPointerException e) {
			Toast.makeText(getBaseContext(), "Thời tiết đã xem chưa được lưu", Toast.LENGTH_SHORT).show();
		}
	}
}

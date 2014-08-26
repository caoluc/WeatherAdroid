package com.nhom14.weatherandroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;



public class WeatherSuggest extends Activity{
	Integer low, hight;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest);

		ArrayList<ArrayList<String>> arrData = new ArrayList<ArrayList<String>>();
		arrData = WeatherActivity.myDB.getData();
		
		TextView suggest = (TextView)findViewById(R.id.content);
		
		Intent i = getIntent();
		low = Integer.parseInt(i.getStringExtra("low"));
		hight = Integer.parseInt(i.getStringExtra("hight"));

		if(low < 15 ){
			suggest.setText("Với nhiệt độ vào khoảng "+ low + " vào ban đêm và "+ hight+" vào ban ngày.\n" +
					"Chúng ta nên trồng các cây có khả năng chịu lạnh tốt như khoai tây, ngô, khoai lang, đậu tương, rau xanh");
		}
		if(low >=15 && low <=25){
			suggest.setText("Với nhiệt độ vào khoảng "+ low + " vào ban đêm và "+ hight+" vào ban ngày.\n" +
					"Thời tiết mùa xuân chúng ta nên trồng các cây như đậu xanh, cải bắp, su hào và đặc biệt chuẩn bị gieo trồng lúa vụ xuân ");
		}
		if(low >25){
			suggest.setText("Với nhiệt độ vào khoảng "+ low + " vào ban đêm và "+ hight+" vào ban ngày.\n" +
					"Chúng ta nên trồng các cây như khoai tây, ngô, khoai lang, đậu tương, rau xanh");
		}
		
	}
}
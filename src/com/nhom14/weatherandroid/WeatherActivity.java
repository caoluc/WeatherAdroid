package com.nhom14.weatherandroid;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhom14.databases.Databases;
import com.nhom14.databases.MyImage;



public class WeatherActivity extends Activity {
	
	
	String yahooapisBase = "http://query.yahooapis.com/v1/public/yql?q=select*from%20geo.places%20where%20text=";
	String yahooapisFormat = "&format=xml";
	String yahooAPIsQuery;
	String queryString="http://weather.yahooapis.com/forecastrss?w=";
	String firstResult = "http://xml.weather.yahoo.com/forecastrss/";
	String endResult = "_c.xml";
	String key;
	String queryStringData;
	String cityName="";
	
	Button search;
	ListView listviewWOEID;
	LocationManager lm;
	Location location;
	Double lati, longi;
	WebView webView;
	
    TextView []tvCity = new TextView[4];
    SingleWeatherInfoView haNoi;
    SingleWeatherInfoView bacGiang;
    SingleWeatherInfoView []weatherCity = new SingleWeatherInfoView[2] ;
    String low, hight;
    public static Databases myDB;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new Databases(this);
        webView = (WebView)findViewById(R.id.webview);
        loadView("hanoi");
        
        getDataWeather( loadView("hanoi"));
        low = getDataWeather(loadView("hanoi"))[3];
        hight = getDataWeather(loadView("hanoi"))[2];
        
        
        
        haNoi = (SingleWeatherInfoView)findViewById(R.id.weather_hanoi);
        bacGiang = (SingleWeatherInfoView)findViewById(R.id.weather_bacninh);
        weatherCity[0] = (SingleWeatherInfoView)findViewById(R.id.weather_1);
        weatherCity[1] = (SingleWeatherInfoView)findViewById(R.id.weather_2);
        tvCity[0] = (TextView)findViewById(R.id.tv_1);
        tvCity[1] = (TextView)findViewById(R.id.tv_2);
        tvCity[2] = (TextView)findViewById(R.id.tv_hanoi);
        tvCity[3] = (TextView)findViewById(R.id.tv_bacgiang);
        tvCity[0].setOnClickListener(setCity(0));
        tvCity[1].setOnClickListener(setCity(1));
        
        haNoi.setOnClickListener(onClick(tvCity[2].getText().toString()));
        bacGiang.setOnClickListener(onClick(tvCity[3].getText().toString()));
     
    }
    
    public OnClickListener onClick(final String nameCity) {
    	OnClickListener click = new OnClickListener() {
    	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Document dataDocument  = loadView(nameCity);
				String []weatherData =new String[6];
				weatherData = getDataWeather(dataDocument);
				weatherData[0] = weatherData[0].substring(0, weatherData[0].length()-5);
				MyImage myImage = new MyImage(weatherData[5], weatherData[0]+nameCity+".jpg");
		     	myImage.downLoadFromUrl();
		     	weatherData[5] = myImage.getPathFile();
				myDB.addWeatherData(nameCity, weatherData[0], weatherData[1],
						weatherData[2], weatherData[3], weatherData[4], weatherData[5]);
				Toast.makeText(getBaseContext(), nameCity, Toast.LENGTH_LONG).show();
			}
		};
		return click;
    }
    
    public OnClickListener setCity(final int tv) {
    	OnClickListener setCity = new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//String tmp = cityName;
				AlertDialog setCTDialog = setCityDialog(tv);
				setCTDialog.show();
			}
		};
		return setCity;
    }
    private Document loadView(String city) {
    	Document weatherDocRes = null;
    	try {
	    	ArrayList<String> l = QueryYahooAPIs(city);
	    	String woeid = l.get(0);
	    	queryStringData=queryString+woeid+"&u=c";
	    	String weatherString = QueryYahooWeather(queryStringData);
	        weatherDocRes = convertStringToDocument(weatherString);
	        String keyCity;
	        keyCity = weatherDocRes.getElementsByTagName("guid")
	        		.item(0).getTextContent();
	        key = getKey(keyCity);
	        queryStringData = firstResult+key+endResult;
	        weatherString = QueryYahooWeather(queryStringData);
	        Document weatherDoc = convertStringToDocument(weatherString);
	        webView.clearView();
	        String weatherResult = parseWeatherDescription(weatherDoc);
	        webView.loadData(weatherResult, "text/html", "UTF-8");
    	} catch (Exception e) {
    		
    	}
    	return weatherDocRes;
    }
    
    // Lấy mã key từ guid của rss trả về
    public String getKey(String s) {
    	int index = 0;
    	int i=0;
     	while (index == 0) {
    		String tmp = s.substring(i, i+1);
    		if (tmp.equals("_")) index = i;
    		i++;
    	}
    	String result = s.substring(0, index);
    	return result;
    }
    // Convert từ Document sang String để load vào web view
    private String parseWeatherDescription(Document srcDoc){
    	String weatherDescription="";
    	
    	NodeList nodeListDescription = srcDoc.getElementsByTagName("description");
    	if(nodeListDescription.getLength()>=0){
    		for(int i=0; i<nodeListDescription.getLength(); i++){
    			weatherDescription += nodeListDescription.item(i).getTextContent()+"<br/>";
    		}
    	}else{
    		weatherDescription = ("No Description!");
    	}
    	
    	return weatherDescription;
    }
    
    // Định dạng dữ liệu lưu vào databases
    private String[] getDataWeather(Document srcDoc){
    	// TODO Auto-generated method stub
    	String []weatherData=new String[6];
    	
    	Node nodeForecast = srcDoc.getElementsByTagName("yweather:forecast").item(0);
    	weatherData[0] = nodeForecast.getAttributes()
				.getNamedItem("date")
				.getNodeValue()
				.toString(); 
    	
    	weatherData[1] = nodeForecast.getAttributes()
				.getNamedItem("day")
				.getNodeValue()
				.toString();
    	weatherData[2] = nodeForecast.getAttributes()
				.getNamedItem("high")
				.getNodeValue()
				.toString() ;
    	
    	
    	weatherData[3] = nodeForecast.getAttributes()
				.getNamedItem("low")
				.getNodeValue()
				.toString();
    	weatherData[4]=  nodeForecast.getAttributes()
				.getNamedItem("text")
				.getNodeValue()
				.toString();
    	NodeList nodeListDescription = srcDoc.getElementsByTagName("description");
    	String url  = nodeListDescription.item(1).getTextContent();
    	char s = ((char)34);
    	String res;
    	res = String.valueOf(s);
    	int index_start = 0;
    	int i=1;
     	while (index_start == 0) {
    		String tmp = url.substring(i, i+1);
    		if (tmp.endsWith(res) ) index_start = i;
    		i++;
    	}
     	int index_End = 0;
     	i=index_start+5;
     	while (index_End == 0) {
    		String tmp = url.substring(i, i+1);
    		if (tmp.equals(res)) index_End = i;
    		i++;
    	}
     	weatherData[5] = url.substring(index_start+1, index_End);
     	
    	return weatherData;
    }

    // Lấy dữ liệu tại với tham số palce convert sang dạng Document rồi trả về WOEID 
    private ArrayList<String> QueryYahooAPIs(String place){
    	
    	String uriPlace = Uri.encode(place);
    	
    	yahooAPIsQuery = yahooapisBase
    			+ "%22" + uriPlace + "%22"
    			+ yahooapisFormat;
    	
    	String woeidString = QueryYahooWeather(yahooAPIsQuery);
    	Document woeidDoc = convertStringToDocument(woeidString);
    	return  parseWOEID(woeidDoc);
    	
    }
    
    /*-------------------- Trả về danh sách mã WOEID từ 1 Document --------------------------
    ********************************************************************************/
    private ArrayList<String> parseWOEID(Document srcDoc){
    	
    	ArrayList<String> listWOEID = new ArrayList<String>();
    	
    	NodeList nodeListDescription = srcDoc.getElementsByTagName("woeid");
    	if(nodeListDescription.getLength()>=0){
    		for(int i=0; i<nodeListDescription.getLength(); i++){
    			listWOEID.add(nodeListDescription.item(i).getTextContent());
    		}
    	}else{
    		listWOEID.clear();
    	}
    	
    	return listWOEID;
    }
    
    
    
    /*------------------ Convert dữ liệu từ web về sang dạng Document để lấy thông tin----------------
      ************************************************************************************************/ 
    private Document convertStringToDocument(String src){
    	Document dest = null;
    	
    	DocumentBuilderFactory dbFactory =
    			DocumentBuilderFactory.newInstance();
    	DocumentBuilder parser;

    	try {
    		parser = dbFactory.newDocumentBuilder();
			dest = parser.parse(new ByteArrayInputStream(src.getBytes()));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			Toast.makeText(getBaseContext(), 
    				e1.toString(), Toast.LENGTH_LONG).show();
		} catch (SAXException e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), 
    				e.toString(), Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), 
    				e.toString(), Toast.LENGTH_LONG).show();
		}
    	
    	return dest;
    	
    }
    
    
    
    /*------------ Trả về 1 string là dữ liệu của link truyền vào (queryString)--------------------
     * *********************************************************************************************/
    private String QueryYahooWeather(String queryString){
    	
    	String qResult = "";
    	
    	HttpClient httpClient = new DefaultHttpClient();
    	
    	//return Uri.encode(queryString);
    	
        HttpGet httpGet = new HttpGet(queryString);
        
        try {
        	HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
        	
        	if (httpEntity != null){
        		InputStream inputStream = httpEntity.getContent();
        		Reader in = new InputStreamReader(inputStream);
        		BufferedReader bufferedreader = new BufferedReader(in);
        		StringBuilder stringBuilder = new StringBuilder();
        		
        		String stringReadLine = null;

        		while ((stringReadLine = bufferedreader.readLine()) != null) {
        			stringBuilder.append(stringReadLine + "\n");	
        		}
        		
        		qResult = stringBuilder.toString();	
        	}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
        
        return qResult;
        
    }
    	
    
    /*----------------- Tạo optionsMenu cho ứng dụng --------------------------
     **************************************************************************/
	public boolean onCreateOptionsMenu(Menu menu) {
		// only one Option menu per activity
		menu.add(0, 1, 1, "Thông tin");
		menu.add(0, 2, 2, "Tìm địa điểm khác");
		menu.add(0, 3, 3,"Thời tiết đã lưu");
		menu.add(0, 4, 4, "Khuyến Cáo");
		menu.add(0, 5, 5, "Thoát");
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (applyMenuOption(item) || super.onOptionsItemSelected(item));
	}

	private boolean applyMenuOption(MenuItem item) {
		// TODO Auto-generated method stub
		int menuItemId = item.getItemId();
		if (menuItemId == 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					WeatherActivity.this);
			builder.setMessage("	Project: Dự báo thời tiết\n		    ----------+++---------\n\n"
					+ "*Thiết kế và phát triển bởi* \n"
					+ " Lê Văn Ban\n Nguyễn Thế Thạo\n Nguyễn Hoài Nam\nNguyễn Anh Tuấn\n"
					);
			builder.setNegativeButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
			builder.show();
		} else if (menuItemId == 2) {
			AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
			LayoutInflater inflater = (LayoutInflater) WeatherActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	View layout = inflater.inflate(R.layout.search,(ViewGroup) findViewById(R.id.searchID));
	    	final EditText place = (EditText)layout.findViewById(R.id.placeID);
	    	
	    	builder.setTitle("Thông tin thời tiết.");
	    	builder.setView(layout);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				
				// TODO Auto-generated method stub
					String city = place.getText().toString();
					if (!city.equals("")) {
						ArrayList<String> l = QueryYahooAPIs(city);
				    	String woeid = l.get(0);
				    	
				    	String queryStringData=queryString+woeid+"&u=c";
				    	String weatherString = QueryYahooWeather(queryStringData);
				        Document weatherDoc = convertStringToDocument(weatherString);
				        webView.clearView();
				        String weatherResult = parseWeatherDescription(weatherDoc);
				        webView.loadData(weatherResult, "text/html", "UTF-8");
			        }
					else Toast.makeText(getBaseContext(), "Bạn phải nhập tên thành phố vào ô tìm kiếm", Toast.LENGTH_LONG).show();
				}
			});
			AlertDialog detai = builder.create();
			detai.show();
		}
		else if(menuItemId == 3)
		{
			Intent saveIntent = new Intent(this,WeatherSave.class);
			startActivity(saveIntent);
		}
		
		else if(menuItemId == 4){
			Dialog about_dialog = new Dialog(WeatherActivity.this);
			about_dialog.setTitle("Tư vấn nông vụ");
			about_dialog.setContentView(R.layout.suggest);
			about_dialog.setCancelable(true);
			TextView about_content = (TextView) about_dialog.findViewById(R.id.content);
			if(Integer.parseInt(low) <15){
				about_content.setText(" Với nhiệt độ vào khoảng "+ low + " vào ban đêm và "+ hight+" vào ban ngày.\n" +
					"Chúng ta nên trồng các cây có khả năng chịu lạnh tốt như khoai tây, ngô, khoai lang, đậu tương, rau xanh");
			}
			if(Integer.parseInt(low) >=15 && Integer.parseInt(low) <=25){
				about_content.setText(" Với nhiệt độ vào khoảng "+ low + " vào ban đêm và "+ hight+" vào ban ngày.\n" +
					"Thời tiết mùa xuân chúng ta nên trồng các cây như đậu xanh, cải bắp, su hào và đặc biệt chuẩn bị gieo trồng lúa vụ xuân ");
			}
			if(Integer.parseInt(low) >=25 ){
				about_content.setText(" Với nhiệt độ vào khoảng "+ low + " vào ban đêm và "+ hight+" vào ban ngày.\n" +
					"Thời tiết mùa hè chúng ta nên trồng các cây chịu được nóng tốt và các cây ăn quả như dưa hấu, cam, bưởi, xoài ");
			}
			about_dialog.show();
		}

		return false;
	}
	
	
	
	/*------------- Tạo dialog nhập tên thành phố tìm kiếm ----------------------------
	 **********************************************************************************/
	public AlertDialog setCityDialog( final int tv) {
		AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
		LayoutInflater inflater = (LayoutInflater) WeatherActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.search,(ViewGroup) findViewById(R.id.searchID));
    	final EditText place = (EditText)layout.findViewById(R.id.placeID);
    	place.setHint("Tên thành phố");
    	builder.setTitle("Nhập tên thành phố.");
    	builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			
			// TODO Auto-generated method stub
				String city = place.getText().toString();
				
				if (!city.equals(""))
					if (!city.equals(cityName)) {
						cityName = city;
						tvCity[tv].setText(cityName);
						weatherCity[tv].setOnClickListener(WeatherActivity.this.onClick(tvCity[tv].getText().toString()));
				        
					}
					else Toast.makeText(getBaseContext(), "Thao tác không thành công(Trùng tên)", Toast.LENGTH_LONG).show();
			}
		});
		AlertDialog detail = builder.create();
		return detail;
	}
	
}
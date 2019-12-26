package ddwu.mobile.final_project.ma02_20170966;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class InsertDiaryActivity extends Activity {

    EditText etMonth;
    EditText etDay;
    EditText etTitle;
    EditText etComment;
    EditText etFeel;
    EditText etLocation;
    ContentValues row = new ContentValues();
    String apiAddress;
    TextView tvWeather;
    TextView tvMap;
    long id;

    //MainActivity에서 받아온 일정의 월, 일을 받아옴
    Intent intent;
    String month;
    String day;

    ArrayList<Weather> resultList;

    DiaryDBHelper helper;

    //지도 정보 저장
    private final static String TAG = "InsertDiaryActivity";
    private final static int MY_PERMISSIONS_REQ_LOC = 100;

    private String bestProvider;
    AddressResultReceiver addressResultReceiver;
    private LocationManager locManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_diary);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        addressResultReceiver = new AddressResultReceiver(new Handler());

        bestProvider = LocationManager.PASSIVE_PROVIDER;
//		위치 관련 권한 확인 - 필요한 부분이 여러 곳이므로 메소드 화
        checkPermission();

        etMonth = findViewById(R.id.etMonth);
        etDay = findViewById(R.id.etDay);
        etTitle = findViewById(R.id.etTitle);
        etComment = findViewById(R.id.etComment);
        etFeel = findViewById(R.id.etFeel);
        etLocation = findViewById(R.id.etlocation);
        apiAddress = getResources().getString(R.string.server_url);
        helper = new DiaryDBHelper(this);
        resultList = new ArrayList<Weather>();
        tvWeather = (TextView) findViewById(R.id.tvWeather);
        tvMap = (TextView)findViewById(R.id.tvMap);
        resultList = new ArrayList<Weather>();
        apiAddress = getResources().getString(R.string.server_url);

        intent = getIntent();
        month = intent.getExtras().get("month").toString();
        day = intent.getExtras().get("day").toString();

        //현재 사용 중인 Provider 로부터 전달 받은 최종 위치의 주소 확인
        /**
        Location lastLocation = locManager.getLastKnownLocation(bestProvider);
        tvMap.setText("최종 실행 위치: ");

        if (lastLocation != null) {
            Intent intent = new Intent(InsertDiaryActivity.this, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, addressResultReceiver);     // 결과를 수신할 ResultReceiver 객체 저장
            intent.putExtra(Constants.LAT_DATA_EXTRA, lastLocation.getLatitude());      // 위도 저장
            intent.putExtra(Constants.LNG_DATA_EXTRA, lastLocation.getLongitude());     // 경도 저장

            startService(intent);
        }
         **/
    }

    protected void onResume() {
        super.onResume();

        //일정의 월, 일을 가져와 insertactivity의 월, 일 항목 채우기
        etMonth.setText(month);
        etDay.setText(day);
    }
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnAddNewContact:
//			DB 데이터 삽입 작업 수행
                SQLiteDatabase db = helper.getWritableDatabase();


                row.put(DiaryDBHelper.COL_MONTH, etMonth.getText().toString());
                row.put(DiaryDBHelper.COL_DAY, etDay.getText().toString());
                row.put(DiaryDBHelper.COL_TITLE, etTitle.getText().toString());
                row.put(DiaryDBHelper.COL_COMMENT, etComment.getText().toString());
                row.put(DiaryDBHelper.COL_FEEL, etFeel.getText().toString());
                row.put(DiaryDBHelper.COL_LOCATION, etLocation.getText().toString());
                row.put(DiaryDBHelper.COL_WEATHER, tvWeather.getText().toString());
                row.put(DiaryDBHelper.COL_MAP, tvMap.getText().toString());

                long result = db.insert(DiaryDBHelper.TABLE_NAME, null, row);
                helper.close();

                String msg = result > 0 ? "정보 추가 성공" : "정보 추가 실패";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btnGetWeather:
                if (!isOnline()) {
                    Toast.makeText(InsertDiaryActivity.this, "네트워크를 사용가능하게 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String target = etLocation.getText().toString();
                if (target.equals(""))
                    target = etLocation.getHint().toString();  // 입력값이 없을 경우 hint 속성의 값을 기본 값으로 설정
                new NetworkAsyncTask().execute(apiAddress + target);    // server_url 에 입력한 날짜를 결합한 후 AsyncTask 실행
                break;

            case R.id.btnLocation:
                checkPermission();	// 위치 관련 권한 확인 - 필요한 부분이 여러 곳이므로 메소드 화

//				위치 조사 시작
                locManager.requestLocationUpdates(bestProvider, 5000, 0, locListener);
                break;
            case R.id.btnAddNewContactClose:
//			DB 데이터 삽입 취소 수행
                finish();
                break;
        }


    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {

        final static String NETWORK_ERR_MSG = "Server Error!";
        public final static String TAG = "NetworkAsyncTask";

        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(InsertDiaryActivity.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) {
                cancel(true);
                return NETWORK_ERR_MSG;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDlg.dismiss();  // 진행상황 다이얼로그 종료

//          parser 생성 및 OpenAPI 수신 결과를 사용하여 parsing 수행
            MyXmlParser parser = new MyXmlParser();
            resultList = parser.parse(result);

            if (resultList == null) {       // 올바른 결과를 수신하지 못하였을 경우 안내
                Toast.makeText(InsertDiaryActivity.this, "날짜를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (!resultList.isEmpty()) {
                Log.d("aaa", resultList.get(0).getCity());
                Log.d("aaa", resultList.get(0).getWeather());

                tvWeather.setText(resultList.get(0).getWeather());
            }
        }

        @Override
        protected void onCancelled(String msg) {
            super.onCancelled();
            progressDlg.dismiss();
            Toast.makeText(InsertDiaryActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    /* 이하 네트워크 접속을 위한 메소드 */


    /* 네트워크 환경 조사 */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /* 주소(apiAddress)에 접속하여 문자열 데이터를 수신한 후 반환 */
    protected String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        Log.d("result", result);
        return result;
    }


    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }


    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    protected String readStreamToString(InputStream stream) {
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
    @Override
    protected void onPause() {
        super.onPause();
//        위치 조사 종료
        locManager.removeUpdates(locListener);
    }

    /* 위치 관련 권한 확인 메소드 - 필요한 부분이 여러 곳이므로 메소드 화
    ACCESS_FINE_LOCATION - 상세 위치 확인에 필요한 권한
    ACCESS_COARSE_LOCATION - 대략적 위치 확인에 필요한 권한 */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQ_LOC);

                return;
            }
        }
    }

    /* 위치 정보 수신 리스너 생성 */
    LocationListener locListener = new LocationListener() {
        //		위치 변경 시마다 호출
        @Override
        public void onLocationChanged(Location loc) {

            double latitude = loc.getLatitude();	// 위도 확인
            double longitude = loc.getLongitude();	// 경도 확인

//			IntentService 를 사용하여 Geocoding 수행
//			FetchAddressIntentService 는 AndroidManifest.xml 에 서비스로 등록한 상태여야 사용 가능
            Intent intent = new Intent(InsertDiaryActivity.this, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, addressResultReceiver);
            intent.putExtra(Constants.LAT_DATA_EXTRA, latitude);
            intent.putExtra(Constants.LNG_DATA_EXTRA, longitude);

            startService(intent);
        }

        //		현재 위치제공자가 사용이 불가해질 때 호출
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(TAG, provider + " is not available.");
        }

        //		현재 위치제공자가 사용가능해질 때 호출
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(TAG, provider + " is available.");
        }

        //		위치제공자의 상태가 변할 때 호출
        @Override
        public void onStatusChanged(String provider, int status, Bundle extra) {
            // TODO Auto-generated method stub
            Log.i(TAG, provider + "'s status : " + status);
        }

    };


    /* 위도/경도 → 주소 변환 ResultReceiver */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String addressOutput = null;
            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) addressOutput = "";
                //tvMap.setText(tvMap.getText() + addressOutput + System.getProperty("line.separator"));
                tvMap.setText(addressOutput );
            } else {
                tvMap.setText(getString(R.string.no_address_found));
            }
        }
    }
}

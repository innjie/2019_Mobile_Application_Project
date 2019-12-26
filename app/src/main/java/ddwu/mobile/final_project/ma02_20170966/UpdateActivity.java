package ddwu.mobile.final_project.ma02_20170966;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends Activity {
    EditText etMonth;
    EditText etDay;
    EditText etTitle;
    EditText etComment;
    EditText etFeel;
    EditText etLocation;
    TextView tvWeather;


    Intent intent;
    String month;
    String day;
    TextView tvLocation;
    DiaryDBHelper helper;

    long id;
    //지도 정보 저장
    private final static int MY_PERMISSIONS_REQ_LOC = 100;
    private final static String TAG = "UpdateActivity";

    private String bestProvider;
    UpdateActivity.AddressResultReceiver addressResultReceiver;
    private LocationManager locManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        etMonth = findViewById(R.id.etMonth);
        etDay = findViewById(R.id.etDay);
        etTitle = findViewById(R.id.etTitle);
        etComment = findViewById(R.id.etComment);
        etFeel = findViewById(R.id.etFeel);
        etLocation = findViewById(R.id.etlocation);
        tvWeather = findViewById(R.id.tvWeather);
        tvLocation = findViewById(R.id.tvMap);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        addressResultReceiver = new UpdateActivity.AddressResultReceiver(new Handler());

        bestProvider = LocationManager.PASSIVE_PROVIDER;
//		위치 관련 권한 확인 - 필요한 부분이 여러 곳이므로 메소드 화
        checkPermission();


        helper = new DiaryDBHelper(this);

        intent = getIntent();
        month = intent.getExtras().get("month").toString();
        day = intent.getExtras().get("day").toString();
    }


    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = helper.getReadableDatabase();
        String query = "select * from " + DiaryDBHelper.TABLE_NAME + " where " + DiaryDBHelper.COL_MONTH + " = " + month
                + " and " + DiaryDBHelper.COL_DAY + " =  " + day;

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(DiaryDBHelper.COL_ID));
            etMonth.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_MONTH)));
            etDay.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_DAY)));
            etTitle.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_TITLE)));
            etComment.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_COMMENT)));
            etFeel.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_FEEL)));
            etLocation.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_LOCATION)));
            tvWeather.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_WEATHER)));
            tvLocation.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_MAP)));
        }
        cursor.close();
        helper.close();

    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdateContact:

//                DB 데이터 업데이트 작업 수행
//                DBTest01 을 참고하여 작성할 것
                String month = etMonth.getText().toString();
                String day = etDay.getText().toString();
                String title = etTitle.getText().toString();
                String comment = etComment.getText().toString();
                String feel = etFeel.getText().toString();
                String locaion = etLocation.getText().toString();
                String map = tvLocation.getText().toString();

                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(helper.COL_MONTH, month);
                row.put(helper.COL_DAY, day);
                row.put(helper.COL_TITLE, title);
                row.put(helper.COL_COMMENT, comment);
                row.put(helper.COL_FEEL, feel);
                row.put(helper.COL_LOCATION, locaion);
                row.put(helper.COL_MAP, map);

                String whereClause = helper.COL_ID + "=?";
                String[] whereArgs = new String[] { String.valueOf(id) };

                long count = db.update(helper.TABLE_NAME, row, whereClause, whereArgs);
                Log.d("count", String.valueOf(id));
                if (count > 0) {
                    setResult(RESULT_OK, null);
                    helper.close();
                } else {
                    Toast.makeText(this, "항목 수정 실패!", Toast.LENGTH_SHORT).show();
                    helper.close();
                }
                finish();
                break;
            case R.id.btnLocation:
                checkPermission();	// 위치 관련 권한 확인 - 필요한 부분이 여러 곳이므로 메소드 화

//				위치 조사 시작
                locManager.requestLocationUpdates(bestProvider, 5000, 0, locListener);
                break;
            case R.id.btnUpdateContactClose:
//                DB 데이터 업데이트 작업 취소
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

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
            Intent intent = new Intent(UpdateActivity.this, FetchAddressIntentService.class);
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
                tvLocation.setText(addressOutput );
            } else {
                tvLocation.setText(getString(R.string.no_address_found));
            }
        }
    }
}

package ddwu.mobile.final_project.ma02_20170966;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SearchDiaryActivity extends Activity {
    EditText etSelectMonth;

    DiaryDBHelper helper;
    EditText etMonth;
    EditText etDay;
    String month;
    String day;
    String comment;
    String feel;
    String weather;



    String result = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_diary);

        etSelectMonth = findViewById(R.id.et_month);

        helper = new DiaryDBHelper(this);
        etMonth = findViewById(R.id.et_month);
        etDay = findViewById(R.id.et_day);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select:
//			DB 검색 작업 수행
                SQLiteDatabase db = helper.getReadableDatabase();

                month = etMonth.getText().toString();
                day = etDay.getText().toString();

                String query = "select * from " + DiaryDBHelper.TABLE_NAME + " where " + DiaryDBHelper.COL_MONTH + " = " + month
                        + " and " + DiaryDBHelper.COL_DAY + " =  " + day;

                Cursor cursor = db.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    month = cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_MONTH));
                    day = cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_DAY));
                    comment = cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_COMMENT));
                    feel = cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_FEEL));
                    weather = cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_WEATHER));

                }
                result = "날짜 : " + month + "월" + day + "일" + "\n" +
                        "내용 : " + comment + "\n" +
                        "기분 : " +  feel+ "\n" +
                        "날씨: " + weather + "\n";
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);

                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }
}

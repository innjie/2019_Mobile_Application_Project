package ddwu.mobile.final_project.ma02_20170966;

// 과제02
// 작성일 2019. 11. 10
// 작성자 : 02분반 20170966 이인지

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends Activity {

     // m : 월, day :  일을 intent로 다음 액티비에 전달
    String m;
    String day;
    Intent intent = null;
    CalendarView calendar;
    /**
     * 한줄일기 앱 구현
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 일정 표시를 위한 캘린더 뷰
         */
        calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                m = String.valueOf(month + 1);
                day = String.valueOf(dayOfMonth);
            }

        });

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpenAllContact:
                intent = new Intent(this, AllContactsActivity.class);
                break;
            case R.id.btnAddNewContact:
                try {
                    intent = new Intent(this, InsertDiaryActivity.class);
                    intent.putExtra("month", m);
                    intent.putExtra("day", day);
                } catch (RuntimeException e) {
                    Toast.makeText(this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSearchContact :
                intent = new Intent(this, SearchDiaryActivity.class);
                intent.putExtra("month", m);
                intent.putExtra("day", day);
                break;
            case R.id.btnUpdateContact:
                intent = new Intent(this, UpdateActivity.class);
                intent.putExtra("month", m);
                intent.putExtra("day", day);
        }

        if (intent != null) startActivity(intent);
    }
}

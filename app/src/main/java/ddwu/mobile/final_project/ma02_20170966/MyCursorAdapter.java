package ddwu.mobile.final_project.ma02_20170966;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {
    LayoutInflater inflater;
    Cursor cursor;


    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super (context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;
    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvContactMonth = (TextView) view.findViewById(R.id.month);
        TextView tvContactDay = (TextView) view.findViewById(R.id.day);
        TextView tvContactComment = (TextView) view.findViewById(R.id.title);
        TextView tvContactWeather = (TextView) view.findViewById(R.id.weather);

        tvContactMonth.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_MONTH)));
        tvContactDay.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_DAY)));
        tvContactComment.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_TITLE)));
        tvContactWeather.setText(cursor.getString(cursor.getColumnIndex(DiaryDBHelper.COL_WEATHER)));
    }
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        View listItemLayout = inflater.inflate(R.layout.list_layout, parent, false);
        return listItemLayout;
    }
}

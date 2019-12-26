package ddwu.mobile.final_project.ma02_20170966;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ResultActivity extends Activity {
    TextView resultText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultText = findViewById(R.id.select_result);

        Intent gIntent = getIntent();
        resultText.setText(gIntent.getStringExtra("result"));
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnShare:
                shareTwitter();
                break;
        }
    }
    public void shareTwitter() {
        String strLink = null;
        String result = resultText.getText().toString();
        try {
            strLink = String.format("http://twitter.com/intent/tweet?text=%s", URLEncoder.encode(result, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strLink));
        startActivity(intent);
    }
}

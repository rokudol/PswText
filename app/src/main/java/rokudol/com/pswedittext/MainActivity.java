package rokudol.com.pswedittext;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rokudol.com.pswtext.PswText;

public class MainActivity extends AppCompatActivity {
    private PswText pswText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pswText = (PswText) findViewById(R.id.psw);
        pswText.setPswColor(Color.parseColor("#000000"));
        pswText.setPswLength(6);
        pswText.setTextWatcher(new PswText.TextWatcher() {
            @Override
            public void textChanged(String password, boolean isFinishInput) {
                Toast.makeText(MainActivity.this, String.format("输入的密码：%s，是否输入完成：%s", password, isFinishInput), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

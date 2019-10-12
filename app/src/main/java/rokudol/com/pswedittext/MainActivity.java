package rokudol.com.pswedittext;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rokudol.com.pswtext.PwdText;
import rokudol.com.pswtext.TextWatcher;

public class MainActivity extends AppCompatActivity {
    private PwdText pswText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pswText = findViewById(R.id.psw);
        pswText.getAttrBean().setPswColor(Color.parseColor("#000000"));
        pswText.getAttrBean().setPswLength(6);
        pswText.setTextWatcher(new TextWatcher() {
            @Override
            public void textChanged(String password, boolean isFinishInput) {
                Toast.makeText(MainActivity.this, String.format("输入的密码：%s，是否输入完成：%s", password, isFinishInput), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

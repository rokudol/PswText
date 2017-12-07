package rokudol.com.pswedittext;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
	}
}

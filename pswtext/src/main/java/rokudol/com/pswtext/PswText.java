package rokudol.com.pswtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;


public class PswText  extends View {
	private InputMethodManager input;//Input method management
	private ArrayList<Integer> result;//Enter the current result to save
	private int saveResult;//Save the total number of passwords entered when the back key is pressed
	private int pswLength;//Password length
	private int borderColor;//Border color
	private int borderShadowColor;//Border shadow color
	private int pswColor;//password color
	private int pswTextSize;//Password plaintext size
	private int inputBorderColor;//The border color is entered
	private int borderImg;//Password box picture
	private int inputBorderImg;//When entering the password box picture
	private int delayTime;//Delay the drawing of the dot time
	private boolean isBorderImg;//Whether the password box is drawn using pictures
	private boolean isShowTextPsw;//Whether to draw a clear text password when you press the back key
	private boolean isShowBorderShadow;//When the password is entered, the password box color shows shadows
	private boolean clearTextPsw;//Only draw plain text password
	private boolean darkPsw;//Only draw the dot password
	private Paint pswDotPaint;//Password origin pen
	private Paint pswTextPaint;//Password clear brush
	private Paint borderPaint;//Border brush
	private Paint inputBorderPaint;//When you enter the border brush
	private RectF borderRectF;//Border rectangle
	private int borderRadius;//Border rounded
	private int borderWidth;//Border width
	private int spacingWidth;//The spacing between the borders
	private InputCallBack inputCallBack;//Enter listen

	private static boolean invalidated = false;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					invalidated = true;
					invalidate();
					break;
			}
		}
	};

	public interface InputCallBack {
		void onInputFinish(String password);
	}

	public void setInputCallBack(InputCallBack inputCallBack) {
		this.inputCallBack = inputCallBack;
	}

	public PswText(Context context) {
		super(context);
	}

	public PswText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public PswText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context, attrs);
	}

	//Initialize the values
	private void initView(Context context, AttributeSet attrs) {
		this.setOnKeyListener(new NumKeyListener());
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		result = new ArrayList<>();

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PswText);
		if (array != null) {
			pswLength = array.getInt(R.styleable.PswText_pswLength, 6);
			pswColor = array.getColor(R.styleable.PswText_pswColor, Color.parseColor("#3779e3"));
			borderColor = array.getColor(R.styleable.PswText_borderColor, Color.parseColor("#999999"));
			inputBorderColor = array.getColor(R.styleable.PswText_inputBorder_color, Color.parseColor("#3779e3"));
			borderShadowColor = array.getColor(R.styleable.PswText_borderShadow_color, Color.parseColor("#3577e2"));
			borderImg = array.getResourceId(R.styleable.PswText_borderImg, R.drawable.pic_dlzc_srk1);
			inputBorderImg = array.getResourceId(R.styleable.PswText_inputBorderImg, R.drawable.pic_dlzc_srk);
			isBorderImg = array.getBoolean(R.styleable.PswText_isDrawBorderImg, false);
			isShowTextPsw = array.getBoolean(R.styleable.PswText_isShowTextPsw, false);
			isShowBorderShadow = array.getBoolean(R.styleable.PswText_isShowBorderShadow, false);
			clearTextPsw = array.getBoolean(R.styleable.PswText_clearTextPsw, false);
			darkPsw = array.getBoolean(R.styleable.PswText_darkPsw, false);
			delayTime = array.getInt(R.styleable.PswText_delayTime, 1000);
			pswTextSize = (int) array.getDimension(R.styleable.PswText_psw_textSize,
					TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
			borderRadius = (int) array.getDimension(R.styleable.PswText_borderRadius,
					TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
		} else {
			pswLength = 6;
			pswColor = Color.parseColor("#3779e3");
			borderColor = Color.parseColor("#999999");
			inputBorderColor = Color.parseColor("#3779e3");
			borderShadowColor = Color.parseColor("#3577e2");
			borderImg = R.drawable.pic_dlzc_srk1;
			inputBorderImg = R.drawable.pic_dlzc_srk;
			delayTime = 1000;
			clearTextPsw = false;
			darkPsw = false;
			isBorderImg = false;
			isShowTextPsw = false;
			isShowBorderShadow = false;
			pswTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics());
		}
		//The initial width of the border is 40dp
		borderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
		//The spacing between frames is 10dp
		spacingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
		//The roundness of the border is 8dp
		borderRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		borderRectF = new RectF();
		initPaint();
	}

	//Initialize each brush
	private void initPaint() {
		//The password origin is initialized
		pswDotPaint = new Paint();
		pswDotPaint.setAntiAlias(true);
		pswDotPaint.setStrokeWidth(3);
		pswDotPaint.setStyle(Paint.Style.FILL);
		pswDotPaint.setColor(pswColor);

		//Password plaintext initialization
		pswTextPaint = new Paint();
		pswTextPaint.setAntiAlias(true);
		pswTextPaint.setFakeBoldText(true);
		pswTextPaint.setColor(pswColor);

		//Border brush is initialized
		borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(3);

		//Input the border brush to initialize
		inputBorderPaint = new Paint();
		inputBorderPaint.setAntiAlias(true);
		inputBorderPaint.setColor(inputBorderColor);
		inputBorderPaint.setStyle(Paint.Style.STROKE);
		inputBorderPaint.setStrokeWidth(3);
		//Password box shadow color
		if (isShowBorderShadow) {
			inputBorderPaint.setShadowLayer(6, 0, 0, borderShadowColor);
			setLayerType(LAYER_TYPE_SOFTWARE, inputBorderPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpec = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpec == MeasureSpec.AT_MOST) {
			if (heightSpec != MeasureSpec.AT_MOST) {//Height is known but when width is unknown
				spacingWidth = heightSize / 4;
				widthSize = (heightSize * pswLength) + (spacingWidth * (pswLength - 1));
				borderWidth = heightSize;
			} else {//Width, height are unknown
				widthSize = (borderWidth * pswLength) + (spacingWidth * (pswLength - 1));
				heightSize = borderWidth;
			}
		} else {
			//The width is known but the height is unknown
			if (heightSpec == MeasureSpec.AT_MOST) {
				borderWidth = (widthSize * 4) / (5 * pswLength);
				spacingWidth = borderWidth / 4;
				heightSize = borderWidth;
			}
		}
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int dotRadius = borderWidth / 6;//Circle accounts for one third of the lattice
		int height = getHeight() - 2;

		/*
		* If the text size is equal to the default value, then adjust the size of the text size, otherwise set by the actual size of the input
		* */
		if (pswTextSize == (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics())) {
			pswTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, borderWidth / 8, getResources().getDisplayMetrics());
		}
		pswTextPaint.setTextSize(pswTextSize);

		//Draw the password grid
		drawBorder(canvas, height);

		if (clearTextPsw) {
			for (int i = 0; i < result.size(); i++) {
				String num = result.get(i) + "";
				drawText(canvas, num, i);
				//Password box coordinates
				int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
				int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));

				drawBitmapOrBorder(canvas, left, right, height);
			}
		} else if (darkPsw) {
			for (int i = 0; i < result.size(); i++) {
				float circleX = (float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2)  + (0.6 * spacingWidth));
				float circleY = borderWidth / 2;
				int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
				int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));
				drawBitmapOrBorder(canvas, left, right, height);
				canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
			}
		} else {
			if (invalidated) {
				drawDelayCircle(canvas, height, dotRadius);
				return;
			}
			for (int i = 0; i < result.size(); i++) {
				//Password plaintext
				String num = result.get(i) + "";
				//Dot coordinates
				float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
				float circleY = borderWidth / 2;
				//Password box coordinates
				int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
				int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));

				drawBitmapOrBorder(canvas, left, right, height);

				drawText(canvas, num, i);

				/*
				* When the input position = input length,
				* that is to determine whether the current location of the current password is the location,
				* if it is delayed after 1s dots
				* */
				if (i + 1 == result.size()) {
					handler.sendEmptyMessageDelayed(1, delayTime);
				}
				//If you press the back key to save the password> enter the password length, only draw the dot
				//When you press the back button, do not draw plain text password
				if (!isShowTextPsw) {
					if (saveResult > result.size()) {
						canvas.drawCircle((float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2 + (0.6 * spacingWidth))), circleY, dotRadius, pswDotPaint);
					}
				}
				//When the second password is entered, the dots are drawn
				if (i >= 1) {
					canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
				}
			}
		}


	}

	//Draw a plain text password
	private void drawText(Canvas canvas, String num, int i) {
		Rect mTextBound = new Rect();
		pswTextPaint.getTextBounds(num, 0, num.length(), mTextBound);
		Paint.FontMetrics fontMetrics = pswTextPaint.getFontMetrics();
		float textX = (float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2 - mTextBound.width() / 2) + (0.5 * spacingWidth));
		float textY = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		if (saveResult != 0 || saveResult < result.size()) {
			canvas.drawText(num, textX, textY, pswTextPaint);
		}
	}

	//After 1s delay, draw the currently entered plaintext password as a dot
	private void drawDelayCircle(Canvas canvas, int height, int dotRadius) {
		invalidated = false;
		for (int i = 0; i < result.size(); i++) {
			float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
			float circleY = borderWidth / 2;
			int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
			int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));
			canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
			drawBitmapOrBorder(canvas, left, right, height);
		}
		canvas.drawCircle((float) ((float) (((result.size() - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2)) + (0.6 * spacingWidth)),
				borderWidth / 2, dotRadius, pswDotPaint);
		handler.removeMessages(1);
	}

	//The initial judgment whether to use the picture to draw the password box
	private void drawBorder(Canvas canvas, int height) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), borderImg);
		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		for (int i = 0; i < pswLength; i++) {
			int left = (int) ( (i * (borderWidth + spacingWidth)) + (0.5 * spacingWidth));
			int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));
			if (isBorderImg) {
				Rect dst = new Rect(left, 0, right, height);
				canvas.drawBitmap(bitmap, src, dst, borderPaint);
			} else {
				borderRectF.set(left, 0, right, height);
				canvas.drawRoundRect(borderRectF, borderRadius, borderRadius, borderPaint);
			}
		}
		bitmap.recycle();

	}

	//Determine whether to use the picture to draw the password box
	private void drawBitmapOrBorder(Canvas canvas, int left, int right, int height) {
		if (isBorderImg) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), inputBorderImg);
			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			Rect dst = new Rect(left, 0, right, height);
			canvas.drawBitmap(bitmap, src, dst, inputBorderPaint);
			bitmap.recycle();
		} else {
			borderRectF.set(left, 0, right, height);
			canvas.drawRoundRect(borderRectF, borderRadius, borderRadius, inputBorderPaint);
		}
	}

	//clear password
	public void clearPsw() {
		result.clear();
		invalidate();
	}

	//get password
	public String getPsw() {
		StringBuffer sb = new StringBuffer();
		for (int i : result) {
			sb.append(i);
		}
		return sb.toString();
	}

	//hide keyBord
	public void hideKeyBord() {
		input.hideSoftInputFromWindow(this.getWindowToken(), 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {//Click the controls to pop up the input keyboard
			requestFocus();
			input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (!hasWindowFocus) {
			input.hideSoftInputFromWindow(this.getWindowToken(), 0);
		}
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;//inputType is number
		outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
		return new NumInputConnection(this, false);
	}

	@Override
	public boolean onCheckIsTextEditor() {
		return true;
	}

	class NumInputConnection extends BaseInputConnection {

		public NumInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
		}

		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			//Here is to accept the input method of the text, we only deal with the number, so what operations do not do
			return super.commitText(text, newCursorPosition);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			//Soft keyboard delete key DEL can not directly monitor, send their own del event
			if (beforeLength == 1 && afterLength == 0) {
				return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
						&& super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}
			return super.deleteSurroundingText(beforeLength, afterLength);
		}
	}

	/**
	 * input listener
	 */
	class NumKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (event.isShiftPressed()) {//Handle * # and other keys
					return false;
				}
				if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {//Only deal with numbers
					if (result.size() < pswLength) {
						result.add(keyCode - 7);
						invalidate();
						ensureFinishInput();
					}
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					if (!result.isEmpty()) {//Not empty, delete the last one
						saveResult = result.size();
						result.remove(result.size() - 1);
						invalidate();
					}
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					ensureFinishInput();
					return true;
				}
			}
			return false;
		}

		/**
		 * To determine whether to complete the input, call the call after the completion of call
		 */
		void ensureFinishInput() {
			if (result.size() == pswLength && inputCallBack != null) {//The input is complete
				StringBuffer sb = new StringBuffer();
				for (int i : result) {
					sb.append(i);
				}
				InputMethodManager imm = (InputMethodManager) PswText.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(PswText.this.getWindowToken(), 0); //Force the keyboard to be hidden
				inputCallBack.onInputFinish(sb.toString());
			}
		}
	}
}

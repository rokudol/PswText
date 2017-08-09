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

/**
 * @name：
 * @author： 司马林
 * @phone： 18423134135
 * @createTime： 2017/8/8
 * @modifyTime： 2017/8/8
 * @explain：
 */
public class PswText  extends View {
	private InputMethodManager input;//输入法管理
	private ArrayList<Integer> result;//输入当前结果保存
	private int saveResult;//保存按下back键时输入的密码总数
	private int pswLength;//密码长度
	private int borderColor;//边框颜色
	private int borderShadowColor;//边框阴影颜色
	private int pswColor;//密码颜色
	private int pswTextSize;//密码明文大小
	private int inputBorderColor;//输入时边框颜色
	private int borderImg;//密码框图片
	private int inputBorderImg;//输入时密码框图片
	private int delayTime;//延迟绘制圆点时间
	private boolean isBorderImg;//密码框是否使用图片绘制
	private boolean isShowTextPsw;//按back键时是否绘制明文密码
	private boolean isShowBorderShadow;//输入密码时，密码框颜色是否显示阴影
	private boolean clearTextPsw;//只绘制明文密码
	private boolean darkPsw;//只绘制圆点密码
	private Paint pswDotPaint;//密码原点画笔
	private Paint pswTextPaint;//密码明文画笔
	private Paint borderPaint;//边框画笔
	private Paint inputBorderPaint;//输入时边框画笔
	private RectF borderRectF;//边框矩形
	private int borderRadius;//边框圆角程度
	private int borderWidth;//边框宽度
	private int spacingWidth;//边框之间的间距
	private InputCallBack inputCallBack;//输入监听

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

	//初始化各数值
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
		//边框宽度初始值为40dp
		borderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
		//边框间的间距初始值为10dp
		spacingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
		//边框的圆角程度初始值为8dp
		borderRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		borderRectF = new RectF();
		initPaint();
	}

	//初始化各画笔
	private void initPaint() {
		//密码原点初始化
		pswDotPaint = new Paint();
		pswDotPaint.setAntiAlias(true);
		pswDotPaint.setStrokeWidth(3);
		pswDotPaint.setStyle(Paint.Style.FILL);
		pswDotPaint.setColor(pswColor);

		//密码明文初始化
		pswTextPaint = new Paint();
		pswTextPaint.setAntiAlias(true);
		pswTextPaint.setFakeBoldText(true);
		pswTextPaint.setColor(pswColor);

		//边框画笔初始化
		borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(3);

		//输入时边框画笔初始化
		inputBorderPaint = new Paint();
		inputBorderPaint.setAntiAlias(true);
		inputBorderPaint.setColor(inputBorderColor);
		inputBorderPaint.setStyle(Paint.Style.STROKE);
		inputBorderPaint.setStrokeWidth(3);
		//密码框阴影颜色
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
			if (heightSpec != MeasureSpec.AT_MOST) {//高度已知但宽度未知时
				spacingWidth = heightSize / 4;
				widthSize = (heightSize * pswLength) + (spacingWidth * (pswLength - 1));
				borderWidth = heightSize;
			} else {//宽度，高度都未知时
				widthSize = (borderWidth * pswLength) + (spacingWidth * (pswLength - 1));
				heightSize = borderWidth;
			}
		} else {
			//宽度已知但高度未知
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
		int dotRadius = borderWidth / 6;//圆圈占格子的三分之一
		int height = getHeight() - 2;

		//如果文字大小等于默认值，则按比例调整文字大小，否则按实际输入的大小设置
		if (pswTextSize == (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics())) {
			pswTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, borderWidth / 8, getResources().getDisplayMetrics());
		}
		pswTextPaint.setTextSize(pswTextSize);

		//绘制密码格
		drawBorder(canvas, height);

		if (clearTextPsw) {
			for (int i = 0; i < result.size(); i++) {
				String num = result.get(i) + "";
				drawText(canvas, num, i);
				//密码框坐标
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
				//密码明文
				String num = result.get(i) + "";
				//圆点坐标
				float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
				float circleY = borderWidth / 2;
				//密码框坐标
				int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
				int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));

				drawBitmapOrBorder(canvas, left, right, height);

				drawText(canvas, num, i);

				//当输入位置 = 输入的长度时，即判断当前绘制的位置是否为当前密码位置，若是则延迟1s后绘制圆点
				if (i + 1 == result.size()) {
					handler.sendEmptyMessageDelayed(1, delayTime);
				}
				//若按下back键保存的密码 > 输入的密码长度，则只绘制圆点
				//即按下back键时，不绘制明文密码
				if (!isShowTextPsw) {
					if (saveResult > result.size()) {
						canvas.drawCircle((float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2 + (0.6 * spacingWidth))), circleY, dotRadius, pswDotPaint);
					}
				}
				//当输入第二个密码时，才开始绘制圆点
				if (i >= 1) {
					canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
				}
			}
		}


	}

	//绘制明文密码
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

	//延迟1s后，将当前输入的明文密码绘制为圆点
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

	//初始判断是否使用图片绘制密码框
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

	//判断是否使用图片绘制密码框
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

	//清除密码
	public void clearPsw() {
		result.clear();
		invalidate();
	}

	//获取输入的密码
	public String getPsw() {
		StringBuffer sb = new StringBuffer();
		for (int i : result) {
			sb.append(i);
		}
		return sb.toString();
	}

	//隐藏键盘
	public void hideKeyBord() {
		input.hideSoftInputFromWindow(this.getWindowToken(), 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {//点击控件弹出输入键盘
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
		outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;//输入类型为数字
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
			//这里是接受输入法的文本的，我们只处理数字，所以什么操作都不做
			return super.commitText(text, newCursorPosition);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			//软键盘的删除键 DEL 无法直接监听，自己发送del事件
			if (beforeLength == 1 && afterLength == 0) {
				return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
						&& super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}
			return super.deleteSurroundingText(beforeLength, afterLength);
		}
	}

	/**
	 * 按键监听器
	 */
	class NumKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (event.isShiftPressed()) {//处理*#等键
					return false;
				}
				if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {//只处理数字
					if (result.size() < pswLength) {
						result.add(keyCode - 7);
						invalidate();
						ensureFinishInput();
					}
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					if (!result.isEmpty()) {//不为空，删除最后一个
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
		}//onKey

		/**
		 * 判断是否输入完成，输入完成后调用callback
		 */
		void ensureFinishInput() {
			if (result.size() == pswLength && inputCallBack != null) {//输入完成
				StringBuffer sb = new StringBuffer();
				for (int i : result) {
					sb.append(i);
				}
				InputMethodManager imm = (InputMethodManager) PswText.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(PswText.this.getWindowToken(), 0); //强制隐藏键盘
				inputCallBack.onInputFinish(sb.toString());
			}
		}
	}
}

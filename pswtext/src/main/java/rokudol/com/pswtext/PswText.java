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


public class PswText extends View {
	private InputMethodManager input;//输入法管理
	private ArrayList<Integer> result;//保存当前输入的密码
	private int saveResult;//保存按下返回键时输入的密码总数
	private int pswLength;//密码长度
	private int borderColor;//密码框颜色
	private int borderShadowColor;//密码框阴影颜色
	private int pswColor;//明文密码颜色
	private int pswTextSize;//明文密码字体大小
	private int inputBorderColor;//输入时密码边框颜色
	private int borderImg;//边框图片
	private int inputBorderImg;//输入时边框图片
	private int delayTime;//延迟绘制圆点时间，1000 = 1s
	private boolean isBorderImg;//是否使用图片绘制边框
	private boolean isShowTextPsw;//是否在按返回键时绘制明文密码
	private boolean isShowBorderShadow;//是否绘制在输入时，密码框的阴影颜色
	private boolean clearTextPsw;//是否只绘制明文密码
	private boolean darkPsw;//是否只绘制圆点密码
	private boolean isChangeBorder;//是否在输入密码时不更改密码框颜色
	private Paint pswDotPaint;//密码圆点画笔
	private Paint pswTextPaint;//明文密码画笔
	private Paint borderPaint;//边框画笔
	private Paint inputBorderPaint;//输入时边框画笔
	private RectF borderRectF;//边框圆角矩形
	private int borderRadius;//边框圆角程度
	private int borderWidth;//边框宽度
	private int spacingWidth;//边框之间的间距宽度
	private InputCallBack inputCallBack;//输入完成时监听
	private int height;//整个view的高度

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

	//初始化
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
			isChangeBorder = array.getBoolean(R.styleable.PswText_isChangeBorder, false);
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
			isChangeBorder = false;
			//明文密码字体大小，初始化18sp
			pswTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics());
			//边框圆角程度初始化8dp
			borderRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		}
		//边框宽度初始化40dp
		borderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
		//边框之间的间距初始化10dp
		spacingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
		borderRectF = new RectF();
		initPaint();
	}

	//初始化画笔
	private void initPaint() {
		//密码圆点画笔初始化
		pswDotPaint = new Paint();
		pswDotPaint.setAntiAlias(true);
		pswDotPaint.setStrokeWidth(3);
		pswDotPaint.setStyle(Paint.Style.FILL);
		pswDotPaint.setColor(pswColor);

		//明文密码画笔初始化
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
		//是否绘制边框阴影
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
				widthSize = (heightSize * pswLength) + (spacingWidth * pswLength);
				borderWidth = heightSize;
			} else {//宽高都未知时
				widthSize = (borderWidth * pswLength) + (spacingWidth * pswLength);
				heightSize = (int) (borderWidth + ((borderPaint.getStrokeWidth()) * 2));
			}
		} else {
			//宽度已知但高度未知时
			if (heightSpec == MeasureSpec.AT_MOST) {
				borderWidth = (widthSize * 4) / (5 * pswLength);
				spacingWidth = borderWidth / 4;
				heightSize = (int) (borderWidth + ((borderPaint.getStrokeWidth()) * 2));
			}
		}
		height = heightSize;
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int dotRadius = borderWidth / 6;//密码圆点为边框宽度的六分之一

		/*
		* 如果明文密码字体大小为默认大小，则取边框宽度的八分之一，否则用自定义大小
		* */
		if (pswTextSize == (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics())) {
			pswTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, borderWidth / 8, getResources().getDisplayMetrics());
		}
		pswTextPaint.setTextSize(pswTextSize);

		//绘制密码边框
		drawBorder(canvas, height);
		if (isChangeBorder) {
			if (clearTextPsw) {
				for (int i = 0; i < result.size(); i++) {
					String num = result.get(i) + "";
					drawText(canvas, num, i);
				}
			} else if (darkPsw) {
				for (int i = 0; i < result.size(); i++) {
					float circleX = (float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.5 * spacingWidth));
					float circleY = height / 2;
					canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
				}
			} else {
				if (invalidated) {
					drawDelayCircle(canvas, height, dotRadius);
					return;
				}
				for (int i = 0; i < result.size(); i++) {
					//明文密码
					String num = result.get(i) + "";
					//圆点坐标
					float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
					float circleY = height / 2;
					//密码框坐标
					drawText(canvas, num, i);

				/*
				* 当输入位置 = 输入长度时
				* 即判断当前绘制位置是否等于当前正在输入密码的位置
				* 若是则延迟delayTime时间后绘制为圆点
				* */
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
					//当输入第二个密码时，才开始从第一个位置绘制圆点
					if (i >= 1) {
						canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
					}
				}
			}
		} else {
			if (clearTextPsw) {
				for (int i = 0; i < result.size(); i++) {
					String num = result.get(i) + "";
					drawText(canvas, num, i);
					//计算密码边框坐标
					int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
					int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.5 * spacingWidth));

					drawBitmapOrBorder(canvas, left, right, height);
				}
			} else if (darkPsw) {
				for (int i = 0; i < result.size(); i++) {
					float circleX = (float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
					float circleY = height / 2;
					int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
					int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.5 * spacingWidth));
					drawBitmapOrBorder(canvas, left, right, height);
					canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
				}
			} else {
				if (invalidated) {
					drawDelayCircle(canvas, height, dotRadius);
					return;
				}
				for (int i = 0; i < result.size(); i++) {
					//明文密码
					String num = result.get(i) + "";
					//圆点坐标
					float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
					float circleY = height / 2;
					//密码框坐标
					int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
					int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.5 * spacingWidth));

					drawBitmapOrBorder(canvas, left, right, height);

					drawText(canvas, num, i);

				/*
				* 当输入位置 = 输入长度时
				* 即判断当前绘制位置是否等于当前正在输入密码的位置
				* 若是则延迟delayTime时间后绘制为圆点
				* */
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
					//当输入第二个密码时，才开始从第一个位置绘制圆点
					if (i >= 1) {
						canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
					}
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
		float textY = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		if (saveResult != 0 || saveResult < result.size()) {
			canvas.drawText(num, textX, textY, pswTextPaint);
		}
	}

	//延迟delay时间后，将当前输入的明文密码绘制为圆点
	private void drawDelayCircle(Canvas canvas, int height, int dotRadius) {
		invalidated = false;
		if (isChangeBorder) {
			for (int i = 0; i < result.size(); i++) {
				float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
				float circleY = height / 2;
				canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
			}
			canvas.drawCircle((float) ((float) (((result.size() - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2)) + (0.6 * spacingWidth)),
					height / 2, dotRadius, pswDotPaint);
		} else {
			for (int i = 0; i < result.size(); i++) {
				float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
				float circleY = height / 2;
				int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
				int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.5 * spacingWidth));
				canvas.drawCircle(circleX, circleY, dotRadius, pswDotPaint);
				drawBitmapOrBorder(canvas, left, right, height);
			}
			canvas.drawCircle((float) ((float) (((result.size() - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2)) + (0.6 * spacingWidth)),
					height / 2, dotRadius, pswDotPaint);
		}
		handler.removeMessages(1);
	}

	//绘制初始密码框时判断是否用图片绘制密码框
	private void drawBorder(Canvas canvas, int height) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), borderImg);
		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		for (int i = 0; i < pswLength; i++) {
			int left = (int) ((i * (borderWidth + spacingWidth)) + (0.5 * spacingWidth));
			int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.5 * spacingWidth));
			if (isBorderImg) {
				Rect dst = new Rect(left, (int) borderPaint.getStrokeWidth(), right, (int) (height - (borderPaint.getStrokeWidth())));
				canvas.drawBitmap(bitmap, src, dst, borderPaint);
			} else {
				borderRectF.set(left, borderPaint.getStrokeWidth(), right, height - (borderPaint.getStrokeWidth()));
				canvas.drawRoundRect(borderRectF, borderRadius, borderRadius, borderPaint);
			}
		}
		bitmap.recycle();

	}

	//是否使用图片绘制密码框
	private void drawBitmapOrBorder(Canvas canvas, int left, int right, int height) {
		if (isBorderImg) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), inputBorderImg);
			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			Rect dst = new Rect(left, (int) (0 + ((borderPaint.getStrokeWidth()))), right, (int) (height - (borderPaint.getStrokeWidth())));
			canvas.drawBitmap(bitmap, src, dst, inputBorderPaint);
			bitmap.recycle();
		} else {
			borderRectF.set(left, 0 + (borderPaint.getStrokeWidth()), right, height - (borderPaint.getStrokeWidth()));
			canvas.drawRoundRect(borderRectF, borderRadius, borderRadius, inputBorderPaint);
		}
	}

	//清除密码
	public void clearPsw() {
		result.clear();
		invalidate();
	}

	//获取密码
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
		if (event.getAction() == MotionEvent.ACTION_DOWN) {//点击弹出键盘
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
		outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;//只允许输入数字
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
			//这里是接收文本的输入法，我们只允许输入数字，则不做任何处理
			return super.commitText(text, newCursorPosition);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			//屏蔽返回键，发送自己的删除事件
			if (beforeLength == 1 && afterLength == 0) {
				return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
						&& super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}
			return super.deleteSurroundingText(beforeLength, afterLength);
		}
	}

	/**
	 * 输入监听
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
						FinishInput();
					}
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					if (!result.isEmpty()) {//不为空时，删除最后一个数字
						saveResult = result.size();
						result.remove(result.size() - 1);
						invalidate();
					}
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					FinishInput();
					return true;
				}
			}
			return false;
		}

		/**
		 * 输入完成后调用的方法
		 */
		void FinishInput() {
			if (result.size() == pswLength && inputCallBack != null) {//输入已完成
				StringBuffer sb = new StringBuffer();
				for (int i : result) {
					sb.append(i);
				}
				inputCallBack.onInputFinish(sb.toString());
				InputMethodManager imm = (InputMethodManager) PswText.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(PswText.this.getWindowToken(), 0); //输入完成后隐藏键盘
			}
		}
	}
}
package rokudol.com.pswtext

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager

class PwdText : View {
    /**
     * 输入法管理
     */
    private var input: InputMethodManager? = null
    /**
     * 当前输入的密码
     */
    private var results: ArrayList<Int> = ArrayList()

    private lateinit var pswAttrsBean: PswAttrsBean
    private var borderWidth: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics).toInt()
    private var spacingWidth: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()

    /**
     * 密码圆点画笔
     */
    private lateinit var pswDotPaint: Paint

    /**
     * 明文密码画笔
     */
    private lateinit var pswTextPaint: Paint

    /**
     * 边框画笔
     */
    private lateinit var borderPaint: Paint

    /**
     * 输入时边框画笔
     */
    private lateinit var inputBorderPaint: Paint

    /**
     * 边框圆角矩形
     */
    private lateinit var borderRectF: RectF

    /**
     * 控件高度
     */
    private var mHeight: Int = 0

    /**
     * 保存按下返回键时输入的密码总数
     */
    private var saveResult: Int = 0

    private var textWatcher: TextWatcher? = null

    private var invalidated: Boolean = false
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    invalidated = true
                    invalidate()
                }
                else -> {
                }
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        initView(context, attr)
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        initView(context, attr)
    }

    private fun initView(context: Context, attrs: AttributeSet) {
        setOnKeyListener(NumKeyListener(object : InputStatusListener {
            override fun add(num: Int) {
                if (results.size < pswAttrsBean.getPswLength()) {
                    results.add(num)
                    invalidate()
                }
            }

            override fun remove() {
                if (results.isNotEmpty()) {//不为空时，删除最后一个数字
                    saveResult = results.size
                    results.removeAt(results.size - 1)
                    invalidate()
                }
            }

            override fun finishInput() {
                val sb = StringBuffer()
                for (i in results) {
                    sb.append(i)
                }
                if (results.size == pswAttrsBean.getPswLength()) {//输入已完成
                    if (getTextWatcher() != null) {
                        getTextWatcher()?.textChanged(sb.toString(), true)
                    }
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(windowToken, 0) //输入完成后隐藏键盘
                } else {
                    if (textWatcher != null) {
                        getTextWatcher()?.textChanged(sb.toString(), false)
                    }
                }
            }
        }))
        isFocusable = true
        isFocusableInTouchMode = true
        input = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val array: TypedArray? = context.obtainStyledAttributes(attrs, R.styleable.PwdText)
        if (array != null) {
            pswAttrsBean = PswAttrsBean(this,
                    array.getInt(R.styleable.PwdText_pswLength, PswConstants.PSW_LENGTH),
                    array.getColor(R.styleable.PwdText_borderColor, PswConstants.BORDER_COLOR),
                    array.getColor(R.styleable.PwdText_borderShadow_color, PswConstants.BORDER_SHADOW_COLOR),
                    array.getColor(R.styleable.PwdText_pswColor, PswConstants.PSW_COLOR),
                    array.getColor(R.styleable.PwdText_inputBorder_color, PswConstants.INPUT_BORDER_COLOR),
                    array.getResourceId(R.styleable.PwdText_inputBorderImg, PswConstants.INPUT_BORDER_IMG),
                    array.getResourceId(R.styleable.PwdText_borderImg, PswConstants.BORDER_IMG),
                    array.getInt(R.styleable.PwdText_delayTime, PswConstants.DELAY_TIME),
                    array.getBoolean(R.styleable.PwdText_isDrawBorderImg, PswConstants.IS_BORDER_IMG),
                    array.getBoolean(R.styleable.PwdText_isShowTextPsw, PswConstants.IS_SHOW_TEXT_PSW),
                    array.getBoolean(R.styleable.PwdText_isShowBorderShadow, PswConstants.IS_SHOW_BORDER_SHADOW),
                    array.getBoolean(R.styleable.PwdText_clearTextPsw, PswConstants.CLEAR_TEXT_PSW),
                    array.getBoolean(R.styleable.PwdText_darkPsw, PswConstants.DARK_PSW),
                    array.getBoolean(R.styleable.PwdText_isChangeBorder, PswConstants.IS_CHANGE_BORDER),
                    array.getInt(R.styleable.PwdText_psw_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics).toInt()),
                    array.getInt(R.styleable.PwdText_borderRadius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()))
            array.recycle()
        } else {
            pswAttrsBean = PswAttrsBean(this)
        }
        initPaint()
    }

    private fun initPaint() {
        borderRectF = RectF()
        //密码圆点画笔初始化
        pswDotPaint = Paint()
        pswDotPaint.isAntiAlias = true
        pswDotPaint.strokeWidth = PswConstants.DEFAULT_STORK_WIDTH
        pswDotPaint.style = Paint.Style.FILL
        pswDotPaint.color = pswAttrsBean.getPswColor()

        //明文密码画笔初始化
        pswTextPaint = Paint()
        pswTextPaint.isAntiAlias = true
        pswTextPaint.isFakeBoldText = true
        pswTextPaint.color = pswAttrsBean.getPswColor()

        //边框画笔初始化
        borderPaint = Paint()
        borderPaint.isAntiAlias = true
        borderPaint.color = pswAttrsBean.getBorderColor()
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = PswConstants.DEFAULT_STORK_WIDTH

        //输入时边框画笔初始化
        inputBorderPaint = Paint()
        inputBorderPaint.isAntiAlias = true
        inputBorderPaint.color = pswAttrsBean.getInputBorderColor()
        inputBorderPaint.style = Paint.Style.STROKE
        inputBorderPaint.strokeWidth = PswConstants.DEFAULT_STORK_WIDTH

        //是否绘制边框阴影
        if (pswAttrsBean.isShowBorderShadow()) {
            inputBorderPaint.setShadowLayer(6f, 0f, 0f, pswAttrsBean.getBorderShadowColor())
            setLayerType(LAYER_TYPE_SOFTWARE, inputBorderPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var widthSpec = MeasureSpec.getMode(widthMeasureSpec)

        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var heightSpec = MeasureSpec.getMode(heightMeasureSpec)

        if (widthSpec == MeasureSpec.AT_MOST) {
            //高度已知但宽度未知时
            if (heightSpec != MeasureSpec.AT_MOST) {
                spacingWidth = heightSize / 4
                widthSize = heightSize * pswAttrsBean.getPswLength() + spacingWidth * pswAttrsBean.getPswLength()
                borderWidth = heightSize
            } else {
                //宽高都未知时
                widthSize = borderWidth * pswAttrsBean.getPswLength() + spacingWidth * pswAttrsBean.getPswLength()
                heightSize = (borderWidth + borderPaint.strokeWidth * 2).toInt()
            }
        } else {
            //宽度已知但高度未知时
            if (heightSpec == View.MeasureSpec.AT_MOST) {
                borderWidth = widthSize * 4 / (5 * pswAttrsBean.getPswLength())
                spacingWidth = borderWidth / 4
                heightSize = (borderWidth + borderPaint.strokeWidth * 2).toInt()
            }
        }

        mHeight = heightSize
        setMeasuredDimension(widthSize, heightSize)
    }

    private fun initColor() {
        pswDotPaint.color = pswAttrsBean.getPswColor()
        pswTextPaint.color = pswAttrsBean.getPswColor()
        borderPaint.color = pswAttrsBean.getBorderColor()
        inputBorderPaint.color = pswAttrsBean.getInputBorderColor()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initColor()
        //密码圆点为边框宽度的六分之一
        val dotRadius = borderWidth / 6

        //如果明文密码字体大小为默认大小，则取边框宽度的八分之一，否则用自定义大小
        if (pswAttrsBean.getPswTextSize() == TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics).toInt()) {
            pswAttrsBean.justSetPswTextSizeValue(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (borderWidth / 8).toFloat(), resources.displayMetrics).toInt())
        }
        pswTextPaint.textSize = pswAttrsBean.getPswTextSize().toFloat()
        drawBorder(canvas, mHeight)
        if (pswAttrsBean.isChangeBorder()) {
            when {
                pswAttrsBean.isClearTextPsw() -> for ((index: Int, num: Int) in results.withIndex()) {
                    drawText(canvas, num.toString(), index)
                }
                pswAttrsBean.isDarkPsw() -> for (index in results.indices) {
                    val circleX = ((index * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                    val circleY = (mHeight / 2).toFloat()
                    canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint)
                }
                else -> {
                    if (invalidated) {
                        drawDelayCircle(canvas, height, dotRadius)
                        return
                    }
                    for ((index: Int, num: Int) in results.withIndex()) {
                        //圆点坐标
                        val circleX = (((index - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                        val circleY = (mHeight / 2).toFloat()
                        //密码框坐标
                        drawText(canvas, num.toString(), index)
                        /*
                        * 当输入位置 = 输入长度时
                        * 即判断当前绘制位置是否等于当前正在输入密码的位置
                        * 若是则延迟delayTime时间后绘制为圆点
                        * */
                        if (index + 1 == results.size) {
                            mHandler.sendEmptyMessageDelayed(1, pswAttrsBean.getDelayTime().toLong())
                        }
                        //若按下back键保存的密码 > 输入的密码长度，则只绘制圆点
                        //即按下back键时，不绘制明文密码
                        if (!pswAttrsBean.isShowTextPsw()) {
                            if (saveResult > results.size) {
                                canvas.drawCircle((index * (borderWidth + spacingWidth) + (borderWidth / 2 + 0.5 * spacingWidth)).toFloat(), circleY, dotRadius.toFloat(), pswDotPaint)
                            }
                        }
                        //当输入第二个密码时，才开始从第一个位置绘制圆点
                        if (index >= 1) {
                            canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint)
                        }
                    }
                }
            }
        } else {
            when {
                pswAttrsBean.isClearTextPsw() -> for ((index: Int, num: Int) in results.withIndex()) {
                    //计算密码边框坐标
                    val left = (index * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                    val right = (((index + 1) * borderWidth).toDouble() + (index * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()

                    drawBitmapOrBorder(canvas, left, right, height)
                    drawText(canvas, num.toString(), index)
                }
                pswAttrsBean.isDarkPsw() -> for (index in results.indices) {
                    val circleX = ((index * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                    val circleY = (height / 2).toFloat()
                    val left = (index * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                    val right = (((index + 1) * borderWidth).toDouble() + (index * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()
                    drawBitmapOrBorder(canvas, left, right, height)
                    canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint)
                }
                else -> {
                    if (invalidated) {
                        drawDelayCircle(canvas, height, dotRadius)
                        return
                    }
                    for ((index: Int, num: Int) in results.withIndex()) {
                        //圆点坐标
                        val circleX = (((index - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                        val circleY = (height / 2).toFloat()
                        //密码框坐标
                        val left = (index * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                        val right = (((index + 1) * borderWidth).toDouble() + (index * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()

                        drawBitmapOrBorder(canvas, left, right, height)

                        drawText(canvas, num.toString(), index)

                        /**
                         * 当输入位置 = 输入长度时
                         * 即判断当前绘制位置是否等于当前正在输入密码的位置
                         * 若是则延迟delayTime时间后绘制为圆点
                         */
                        if (index + 1 == results.size) {
                            mHandler.sendEmptyMessageDelayed(1, pswAttrsBean.getDelayTime().toLong())
                        }
                        //若按下back键保存的密码 > 输入的密码长度，则只绘制圆点
                        //即按下back键时，不绘制明文密码
                        if (!pswAttrsBean.isShowTextPsw()) {
                            if (saveResult > results.size) {
                                canvas.drawCircle((index * (borderWidth + spacingWidth) + (borderWidth / 2 + 0.5 * spacingWidth)).toFloat(), circleY, dotRadius.toFloat(), pswDotPaint)
                            }
                        }
                        //当输入第二个密码时，才开始从第一个位置绘制圆点
                        if (index >= 1) {
                            canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint)
                        }
                    }
                }
            }
        }
    }


    /**
     * 绘制初始密码框时判断是否用图片绘制密码框
     */
    private fun drawBorder(canvas: Canvas, height: Int) {
        val bitmap: Bitmap? = BitmapFactory.decodeResource(resources, pswAttrsBean.getBorderImg())
        if(bitmap != null) {
            val src = Rect(0, 0, bitmap.width, bitmap.height)
            for (i in 0 until pswAttrsBean.getPswLength()) {
                val left = (i * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                val right = (((i + 1) * borderWidth).toDouble() + (i * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()
                if (pswAttrsBean.isBorderImg()) {
                    val dst = Rect(left, borderPaint.strokeWidth.toInt(), right, (height - borderPaint.strokeWidth).toInt())
                    canvas.drawBitmap(bitmap, src, dst, borderPaint)
                } else {
                    borderRectF.set(left.toFloat(), borderPaint.strokeWidth, right.toFloat(), height - borderPaint.strokeWidth)
                    canvas.drawRoundRect(borderRectF, pswAttrsBean.getBorderRadius().toFloat(), pswAttrsBean.getBorderRadius().toFloat(), borderPaint)
                }
            }
            bitmap.recycle()
        }
    }

    /**
     * 绘制明文密码
     */
    private fun drawText(canvas: Canvas, num: String, i: Int) {
        val mTextBound = Rect()
        pswTextPaint.getTextBounds(num, 0, num.length, mTextBound)
        val fontMetrics = pswTextPaint.fontMetrics
        val textX = ((i * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2 - mTextBound.width() / 2).toDouble() + 0.45 * spacingWidth).toFloat()
        val textY = (mHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
        if (saveResult != 0 || saveResult < results.size) {
            canvas.drawText(num, textX, textY, pswTextPaint)
        }
    }

    /**
     * 延迟delay时间后，将当前输入的明文密码绘制为圆点
     */
    private fun drawDelayCircle(canvas: Canvas, height: Int, dotRadius: Int) {
        invalidated = false
        if (pswAttrsBean.isChangeBorder()) {
            for (i in results.indices) {
                val circleX = (((i - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                val circleY = (height / 2).toFloat()
                canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint)
            }
            canvas.drawCircle((((results.size - 1) * (borderWidth + spacingWidth) + borderWidth / 2).toFloat() + 0.5 * spacingWidth).toFloat(),
                    (height / 2).toFloat(), dotRadius.toFloat(), pswDotPaint)
        } else {
            for (i in results.indices) {
                val circleX = (((i - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                val circleY = (height / 2).toFloat()
                val left = (i * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                val right = (((i + 1) * borderWidth).toDouble() + (i * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()
                canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint)
                drawBitmapOrBorder(canvas, left, right, height)
            }
            canvas.drawCircle((((results.size - 1) * (borderWidth + spacingWidth) + borderWidth / 2).toFloat() + 0.5 * spacingWidth).toFloat(),
                    (height / 2).toFloat(), dotRadius.toFloat(), pswDotPaint)
        }
    }

    /**
     * 是否使用图片绘制密码框
     */
    private fun drawBitmapOrBorder(canvas: Canvas, left: Int, right: Int, height: Int) {
        if (pswAttrsBean.isBorderImg()) {
            val bitmap = BitmapFactory.decodeResource(resources, pswAttrsBean.getInputBorderImg())
            val src = Rect(0, 0, bitmap.width, bitmap.height)
            val dst = Rect(left, (0 + borderPaint.strokeWidth).toInt(), right, (height - borderPaint.strokeWidth).toInt())
            canvas.drawBitmap(bitmap, src, dst, inputBorderPaint)
            bitmap.recycle()
        } else {
            borderRectF.set(left.toFloat(), 0 + borderPaint.strokeWidth, right.toFloat(), height - borderPaint.strokeWidth)
            canvas.drawRoundRect(borderRectF, pswAttrsBean.getBorderRadius().toFloat(), pswAttrsBean.getBorderRadius().toFloat(), inputBorderPaint)
        }
    }

    public fun getAttrBean(): PswAttrsBean {
        return pswAttrsBean
    }

    public fun setTextWatcher(textWatcher: TextWatcher) {
        this.textWatcher = textWatcher
    }

    public fun getTextWatcher(): TextWatcher? {
        return textWatcher
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {//点击弹出键盘
            requestFocus()
            input?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) {
            input?.hideSoftInputFromWindow(this.windowToken, 0)
            mHandler.removeCallbacksAndMessages(null)
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER//只允许输入数字
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE
        return NumInputConnection(this, false)
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }
}
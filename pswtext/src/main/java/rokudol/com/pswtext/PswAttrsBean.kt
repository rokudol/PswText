package rokudol.com.pswtext

import android.util.TypedValue

/**
 * @param view 密码输入框
 * @parm pswLength 密码长度
 * @param borderColor 密码框颜色
 * @param borderShadowColor 密码框阴影颜色
 * @param pswColor 明文密码颜色
 * @param borderImg 边框图片
 * @param inputBorderColor 输入时密码边框颜色
 * @param inputBorderImg 输入时边框图片
 * @param delayTime 延迟绘制圆点时间，1000 = 1s
 * @param isBorderImg 是否使用图片绘制边框
 * @param isShowTextPsw 是否在按返回键时绘制明文密码
 * @param isShowBorderShadow 是否绘制在输入时，密码框的阴影颜色
 * @param clearTextPsw 是否只绘制明文密码
 * @param darkPsw 是否只绘制圆点密码
 * @param changeBorder 是否在输入密码时不更改密码框颜色
 * @param pswTextSize 明文密码字体大小
 * @param borderRadius 边框圆角程度
 */
data class PswAttrsBean(private val view: PwdText,
                        private var pswLength: Int = PswConstants.PSW_LENGTH,
                        private var borderColor: Int = PswConstants.BORDER_COLOR,
                        private var borderShadowColor: Int = PswConstants.BORDER_SHADOW_COLOR,
                        private var pswColor: Int = PswConstants.PSW_COLOR,
                        private var inputBorderColor: Int = PswConstants.INPUT_BORDER_COLOR,
                        private var inputBorderImg: Int = PswConstants.INPUT_BORDER_IMG,
                        private var borderImg: Int = PswConstants.BORDER_IMG,
                        private var delayTime: Int = PswConstants.DELAY_TIME,
                        private var isBorderImg: Boolean = PswConstants.IS_BORDER_IMG,
                        private var isShowTextPsw: Boolean = PswConstants.IS_SHOW_TEXT_PSW,
                        private var isShowBorderShadow: Boolean = PswConstants.IS_SHOW_BORDER_SHADOW,
                        private var clearTextPsw: Boolean = PswConstants.CLEAR_TEXT_PSW,
                        private var darkPsw: Boolean = PswConstants.DARK_PSW,
                        private var changeBorder: Boolean = PswConstants.IS_CHANGE_BORDER,
                        private var pswTextSize: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, view.resources.displayMetrics).toInt(),
                        private var borderRadius: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, view.resources.displayMetrics).toInt()) {

    fun isClearTextPsw(): Boolean {
        return clearTextPsw
    }

    fun setClearTextPsw(clearTextPsw: Boolean) {
        this.clearTextPsw = clearTextPsw
        view.invalidate()
    }

    fun isDarkPsw(): Boolean {
        return darkPsw
    }

    fun setDarkPsw(darkPsw: Boolean) {
        this.darkPsw = darkPsw
        view.invalidate()
    }

    fun getPswLength(): Int {
        return pswLength
    }

    fun setPswLength(pswLength: Int) {
        this.pswLength = pswLength
        view.invalidate()
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setBorderColor(borderColor: Int) {
        this.borderColor = borderColor
        view.invalidate()
    }

    fun getBorderShadowColor(): Int {
        return borderShadowColor
    }

    fun setBorderShadowColor(borderShadowColor: Int) {
        this.borderShadowColor = borderShadowColor
        view.invalidate()
    }

    fun getPswColor(): Int {
        return pswColor
    }

    fun setPswColor(pswColor: Int) {
        this.pswColor = pswColor
        view.invalidate()
    }

    fun getPswTextSize(): Int {
        return pswTextSize
    }

    fun setPswTextSize(pswTextSize: Int) {
        this.pswTextSize = pswTextSize
        view.invalidate()
    }

    fun justSetPswTextSizeValue(pswTextSize: Int) {
        this.pswTextSize = pswTextSize
    }

    fun getInputBorderColor(): Int {
        return inputBorderColor
    }

    fun setInputBorderColor(inputBorderColor: Int) {
        this.inputBorderColor = inputBorderColor
        view.invalidate()
    }

    fun getBorderImg(): Int {
        return borderImg
    }

    fun setBorderImg(borderImg: Int) {
        this.borderImg = borderImg
        view.invalidate()
    }

    fun getInputBorderImg(): Int {
        return inputBorderImg
    }

    fun setInputBorderImg(inputBorderImg: Int) {
        this.inputBorderImg = inputBorderImg
        view.invalidate()
    }

    fun getDelayTime(): Int {
        return delayTime
    }

    fun setDelayTime(delayTime: Int) {
        this.delayTime = delayTime
        view.invalidate()
    }

    fun isBorderImg(): Boolean {
        return isBorderImg
    }

    fun setIsBorderImg(borderImg: Boolean) {
        isBorderImg = borderImg
        view.invalidate()
    }

    fun isShowTextPsw(): Boolean {
        return isShowTextPsw
    }

    fun setShowTextPsw(showTextPsw: Boolean) {
        isShowTextPsw = showTextPsw
        view.invalidate()
    }

    fun isShowBorderShadow(): Boolean {
        return isShowBorderShadow
    }

    fun setShowBorderShadow(showBorderShadow: Boolean) {
        isShowBorderShadow = showBorderShadow
        view.invalidate()
    }

    fun isChangeBorder(): Boolean {
        return changeBorder
    }

    fun setChangeBorder(changeBorder: Boolean) {
        this.changeBorder = changeBorder
        view.invalidate()
    }

    fun getBorderRadius(): Int {
        return borderRadius
    }

    fun setBorderRadius(borderRadius: Int) {
        this.borderRadius = borderRadius
        view.invalidate()
    }
}

package rokudol.com.pswtext

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection


class NumInputConnection(val targetView: View, fullEditor: Boolean) : BaseInputConnection(targetView, fullEditor) {
    override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
        //这里是接收文本的输入法，我们只允许输入数字，则不做任何处理
        return super.commitText(text, newCursorPosition)
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        //屏蔽返回键，发送自己的删除事件
        return if (beforeLength == 1 && afterLength == 0) {
            super.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) &&
                    super.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
        } else super.deleteSurroundingText(beforeLength, afterLength)
    }
}
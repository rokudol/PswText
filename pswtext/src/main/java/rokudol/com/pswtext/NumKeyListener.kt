package rokudol.com.pswtext

import android.view.KeyEvent
import android.view.View

class NumKeyListener(private val inputStatusListener: InputStatusListener) : View.OnKeyListener {
    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                //禁止输入特殊字符
                if (event.isShiftPressed) {
                    return false
                }
                when (keyCode) {
                    //只允许输入0-9的数字
                    in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                        inputStatusListener.add(keyCode - 7)
                        inputStatusListener.finishInput()
                        return true
                    }
                    KeyEvent.KEYCODE_DEL -> {
                        inputStatusListener.remove()
                    }
                    KeyEvent.KEYCODE_ENTER -> {
                        inputStatusListener.finishInput()
                    }
                }
            }
        }
        return false
    }
}
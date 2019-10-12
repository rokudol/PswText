package rokudol.com.pswtext

interface TextWatcher {
    fun textChanged(password: String, isFinishInput: Boolean)
}
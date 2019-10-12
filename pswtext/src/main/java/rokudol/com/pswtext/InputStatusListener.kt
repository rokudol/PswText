package rokudol.com.pswtext

interface InputStatusListener {
    fun add(num: Int)

    fun remove()

    fun finishInput()
}
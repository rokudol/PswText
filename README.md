# PswText
![](/GIF.gif)


使用方法：
compile 'com.rokudoll:PswText:1.0.0'

属性：



| 属性名        |  值   |  作用  |
| --------   | -----:  | :----:  |
| pswLength     | integer |   规定密码长度，默认为6     |
| delayTime | integer | 延迟绘制密码圆点的时间 默认1000,1000=1s|
| borderColor        |   color   |   初始化密码框颜色   |
| pswColor        |    color    |  密码颜色  |
| inputBorder_color | color | 输入时密码框颜色 |
| borderShadow_color | color | 输入时密码框阴影颜色 |
| psw_textSize | sp | 明文密码大小 |
| borderRadius | dp | 不使用图片时，密码框圆角大小|
| borderImg | drawable | 密码框图片 |
| inputBorderImg | drawable | 输入时变化的密码框图片 |
| isDrawBorderImg | boolean | 是否使用图片绘制密码框，为true时设置borderImg、inputBorderImg才有效，默认为false |
| isShowTextPsw | boolean | 按下back键时是否需要绘制当前位置的明文密码，默认为false |
| isShowBorderShadow | boolean | 输入密码时是否需要绘制阴影,为true时设置borderShadow_color才有效，默认为false |
| clearTextPsw | boolean | 是否只绘制明文密码，默认为false |
| darkPsw | boolean | 是否只绘制圆点，默认为false |
# PswText
简介：
====
![](/GIF.gif)

博客地址：[强大的密码输入框][1]


使用方法：


Step 1. Add the JitPack repository to your build file

gradle
maven
sbt
leiningen
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}Copy

Step 2. Add the dependency


	dependencies {
	        compile 'com.github.rokudol:PswText:v1.0.3'
	}


属性：



| 属性名             |       值 |                             作用                             |
| ------------------ | -------: | :----------------------------------------------------------: |
| pswLength          |  integer |                    规定密码长度，默认为6                     |
| delayTime          |  integer |           延迟绘制密码圆点的时间 默认1000,1000=1s            |
| borderColor        |    color |                       初始化密码框颜色                       |
| pswColor           |    color |                           密码颜色                           |
| inputBorder_color  |    color |                       输入时密码框颜色                       |
| borderShadow_color |    color |                     输入时密码框阴影颜色                     |
| psw_textSize       |       sp |                         明文密码大小                         |
| borderRadius       |       dp |                 不使用图片时，密码框圆角大小                 |
| borderImg          | drawable |                          密码框图片                          |
| inputBorderImg     | drawable |                    输入时变化的密码框图片                    |
| isDrawBorderImg    |  boolean | 是否使用图片绘制密码框，为true时设置borderImg、inputBorderImg才有效，默认为false |
| isShowTextPsw      |  boolean |   按下back键时是否需要绘制当前位置的明文密码，默认为false    |
| isShowBorderShadow |  boolean | 输入密码时是否需要绘制阴影,为true时设置borderShadow_color才有效，默认为false |
| clearTextPsw       |  boolean |               是否只绘制明文密码，默认为false                |
| darkPsw            |  boolean |                 是否只绘制圆点，默认为false                  |
| isChangeBorder     |  boolean |        是否在输入密码时不更改密码框颜色，默认为false         |

 setInputCallBack可注册监听，在用户输入完所有密码后，会触发该监听并回调用户所输入的密码


更新说明：
======
## v1.0.1:

_修复bug：_ 

1. 重新计算高度，修复密码框上下两根线绘制不完全的问题

_新增功能：_ 

1. 可选择在输入密码时不更改密码框颜色，xml属性：isChangeBorder，为true：输入时不更改密码框颜色，为false：输入时更改密码框颜色

## v1.0.2

_修复bug：_

1. 确定高度时，宽度自适应导致宽度绘制不正确的问题

## v1.0.3

_修复bug：_

1. 修复明文密码和密文密码绘制没有居中的问题
2. 新增各个属性的getter setter方法，可在java代码中直接用setter去设置各个属性

  [1]: http://blog.rokudol.cn/%E8%87%AA%E5%AE%9A%E4%B9%89view---%E5%BC%BA%E5%A4%A7%E7%9A%84%E5%AF%86%E7%A0%81%E8%BE%93%E5%85%A5%E6%A1%86.html#more

# PswText
Introduction：
====
![](/GIF.gif)

Blog Address：[Powerful password input box][1]


[中文文档][2]

How to use：


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
	        compile 'com.github.rokudol:PswText:v1.0.4'
	}
	
attrs：



| Attribute name        |  value   |  effect  |
| --------   | -----:  | :----:  |
| pswLength     | integer |   the length of the password, the default is 6    |
| delayTime | integer | delay the time to draw the password dot default 1000,1000=1s|
| borderColor        |   color   |   initialize the password box color   |
| pswColor        |    color    |  password color  |
| inputBorder_color | color | when you enter the password box color |
| borderShadow_color | color | when you enter the password box, the shadow color |
| psw_textSize | sp | clear password textSize |
| borderRadius | dp | when the picture is not used, the password box is rounded|
| borderImg | drawable | password box picture |
| inputBorderImg | drawable | enter the password box when changing the picture |
| isDrawBorderImg | boolean | whether to use the picture to draw the password box, set the borderImg true, inputBorderImg only effective, the default is false |
| isShowTextPsw | boolean | when you press the back key, you need to draw the plain text password for the current location. The default is false |
| isShowBorderShadow | boolean | whether you need to draw a shadow when you enter a password, set borderShadow_color to true, the default is false |
| clearTextPsw | boolean | whether to draw only plain text password, the default is false |
| darkPsw | boolean | whether to draw only dots, the default is false |
| isChangeBorder | boolean | whether to change the password box color when entering the password, the default is false |

  "setTextWatcher" can trigger input listener, textChanged can get the user's current input password and whether the status has been entered, true - input is completed, false - not entered completed
  
  Developers can use pwdText.getAttrBean().setXXX() to set properties
  
Release Notes：
======
## v1.0.1:

_fix bug：_ 

1. Recalculate height,Repair the password box up and down two lines to draw the incomplete problem

_added function：_ 

1. You can choose not to change the password box color when entering a password


The corresponding attribute：isChangeBorder.

When isChangeBorder is true：do not change the password box color when typing

When isChangeBorder is false：change the password box color when typing

## v1.0.2:

_fix bug：_

1. when the height measure mode is EXACTLY and width measure mode is AT_MOST, width draw not correct


## v1.0.3:

_fix bug：_

1. clear text passwrod and ciphr text password no drawn in the middle position
2. Add getter setter methods for each property

## v1.0.4:

_added function:_
1. Discard InputCallBack, add TextWatcher, textChanged callback will return the user's current password and whether it has been entered completed

  [1]: http://blog.rokudol.cn/%E8%87%AA%E5%AE%9A%E4%B9%89view---%E5%BC%BA%E5%A4%A7%E7%9A%84%E5%AF%86%E7%A0%81%E8%BE%93%E5%85%A5%E6%A1%86.html#more
  [2]: https://github.com/rokudol/PswText/blob/master/CN-README.md

## v2.0.0:

_restructure:_
1. use kotlin restructure PswText. PswText has been discarded and can be used with PwdText
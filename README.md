# iRobotControl
该项目主要是用APP来控制iRobot扫地机器人的基本运动，控制前进方向及改变运动速度或转弯半径。

**项目原理**：手机与ESP8266接入同一个局域网，手机端作为服务器，向客户端ESP8266发送指令信息，使用TCP协议。   
**运行条件**：[iRobot Roomba 600](https://webapi.irobot.com//for-the-home/support/product-resources/roomba-600-resources.aspx)一台，ATK-ESP8266芯片一个，路由器一个，安卓手机一台  

**操作步骤**：ESP8266和iRobot的连接过程省略，具体可参看[2018-07-19_iRobot_Roomba_600_Open_Interface_Spec.pdf](https://github.com/WangPanHUST/iRobotControl/tree/master/Reference)。接着打开路由器，手机接入该路由器的网络，点击APP的**CONNECT按钮**，服务器端会持续等待客户端的接入。ESP8266通过串口调试助手发送相关指令连接服务器，具体指令请看[ATK-ESP8266 WIFI_User_manual_V1.0.pdf](https://github.com/WangPanHUST/iRobotControl/tree/master/Reference)p19-p20设置透传模式，完成接入手机服务器端的过程，接下来就可以打开iRobot进行操作。  

**注意**：iRobot的前进后退一种运动模式，指令中包含**速度**和**运动半径**，即iRobot的运动轨迹会是弧形，而左右运动是另一种模式，指令中包含**左侧速度**和**右侧速度**，而没有**运动半径**。因此，改变速度会修改四个指令中对应的速度部分，改变半径只会修改前进后退指令的半径部分，特别的，左右指令中对应的轮子速度为0，如左转时左侧轮子速度为0。

***
## 目录
- [Reference](https://github.com/WangPanHUST/iRobotControl/tree/master/Reference)：iRobot和ESP8266的参考手册
- [iRobot](https://github.com/WangPanHUST/iRobotControl/tree/master/iRobot)：项目源代码，可以直接在Android Studio中打开
- [README.md](https://github.com/WangPanHUST/iRobotControl/blob/master/README.md)：README

## 联系我 
**E-mail**:wangpanhust@qq.com

<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<style>
.message{
	font-size:15px;
	padding-top: 40px;
	text-align: left;
}

</style>

<div class="message">
            <li>发送到网关的命令: ${result.sentCommand}</li>
            <li>从网关回复的命令: ${result.receivedResponse}</li>
            <li>网关执行时间间隔: ${result.exectionTime}ms</li>
            <li>客户端得到的命令: ${result.webCommand}</li>
			<li>网关执行的错误码: ${result.errorCode}</li>
			
            
</div>
<hr/>
<div>
<pre >

        错误码 说明
        0 成功
        1 协议参数无效
        2 请求超时
        3 操作/控制失败
        4 操作/控制过于频繁
        
        100 已登陆并认证通过
        101 用户名或密码错误
        102 未登陆/认证
        
        200 无此路设备
        201 设备参数错误
        202 此路设备已存在
        203 设备控制失败
        204 密码错误
        205 未检测到SD卡
        206 设备正在场景/规则中使用
        207 设备超过上限
        208 房间无此设备
        300 防区异常
        301 安放密码错误
        302 无此场景
        500 升级异常
        501 升级文件异常
        502 设备存储异常
        503 设备硬件故障
        504 无新版本可升级
        508 正在升级
        509 升级结束
        510 因升级重启系统， 通知
        511 因回复出场设置重启， 通知
        512 恢复配置成功， 通知
        513 恢复配置失败， 通知
        514 备份配置成功， 通知
        515 备份配置失败， 通知
        600 视频超过数量限制
        601 视频播放出错
        700 无法读取房间设备数据

</pre>
</div>



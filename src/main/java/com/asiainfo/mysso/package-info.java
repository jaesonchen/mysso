/**   
 * - 单点登录的两种方式:
 * 1. 同域名共享cookie
 *  - 如果你的所有平台都是在同一个域名下，那么可以使用同域名共享cookie的方式来完成单点登录的信息共享。
 *  - 将web应用群中所有子系统的域名统一在一个顶级域名下，例如*.baidu.com，然后将它们的cookie域设置为baidu.com。
 *  - 共享 cookie 的方式存在众多局限: 
 *    a. 首先，应用群域名得统一；
 *    b. 其次，应用群各系统使用的技术（至少是web服务器）要相同，不然cookie的key值不同（tomcat为JSESSIONID），无法维持会话，
 *     - 共享cookie的方式是无法实现跨语言技术平台登录的，比如java、php、.net系统之间；
 *    c. 第三，cookie本身不安全。
 *    
 *    
 * 2. SSO认证授权登录
 *  - 要实现多个平台单点登录，前提是多个平台必须要有一个唯一的账号，如手机号，邮箱，或用户名，这样才可以判断出是哪个用户。
 *  a. 客户端：
 *   - 拦截子系统未登录用户请求，跳转至sso认证中心
 *   - 接收并存储sso认证中心发送的令牌
 *   - 与服务器端通信，校验令牌的有效性
 *   - 建立局部会话
 *   - 拦截用户注销请求，向sso认证中心发送注销请求
 *   - 接收sso认证中心发出的注销请求，销毁局部会话
 *   
 *  b. 服务器端：
 *   - 验证用户的登录信息
 *   - 创建全局会话
 *   - 创建授权令牌
 *   - 与客户端通信发送令牌
 *   - 校验客户端令牌有效性
 *   - 系统注册
 *   - 接收客户端注销请求，注销所有会话
 *   
 *  
 * @author chenzq  
 * @date 2019年5月12日 下午12:22:28
 * @version V1.0
 * @Copyright: Copyright(c) 2019 jaesonchen.com Inc. All rights reserved. 
 */
package com.asiainfo.mysso;
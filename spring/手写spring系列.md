#手写spring mvc
##spring mvc执行流程
&emsp;&emsp;spring mvc底层是servlet。执行流程基本上是：
请求到dispatcherServlet，dispatcherServlet去handlerMapping找到对应handler，
并返回对应的方法链，然后dispatcherServlet将handler传递给handlerAdapter执行，
执行完handler之后返回对应的ModelAndView给dispatcherServlet，
dispatcherServlet再将ModelAndView给到viewResolver做视图解析返回view对象给dispatcher，
dispatcherServlet拿到view在给到jsp等做视图渲染。
![](springmvc执行流程.png)
##手写spring mvc思路
&emsp;&emsp;自己用servlet实现简单的springmvc。
 * HttpServlet初始化的时候扫描配置文件对应的包
 * 将需要注入的对象创建并注入到需要的地方
 * 将controller的方法与访问路径绑定
&emsp;&emsp;主要用到的技术：自定义注解、反射

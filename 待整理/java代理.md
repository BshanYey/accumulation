
``````````````````分割线 start ``````````````````
1、UML
6个关系
 * 泛化
 * 聚合 大雁 雁群？
 * 依赖
 * 组合 大雁 翅膀？
 * 实现
 * 关联
``````````````````分割线  end  ``````````````````

``````````````````分割线 start ``````````````````
面向对象设计原则
1、单一职责原则（SRP）
    一个类只负责一个功能领域的相应职责。（高内聚，低耦合）
2、开闭原则（OCP）
    一个软件的实体应当对修改关闭，对扩展开放
3、依赖倒转原则（DIP）
    要针对接口编程，不要针对实现编程
4、迪米特法则（LOD）
    一个类指引入相关的类，无关的类不要引入
5、接口隔离原则（ISP）
    接口不要过于臃肿，通过接口拆分降低耦合
6、里氏代换原则（DP）
    任何基类可以出现的地方子类一定可以出现（子类可以拓展父类的功能，但不能改变父类的功能）
   
``````````````````分割线  end  ``````````````````

``````````````````分割线 start ``````````````````
#设计模式
##一、创建型模式
##二、结构型模式
###1、代理模式
包含抽象行为，真实对象，代理对象三个要素
真实对象和代理对象实现同一个抽象接口
主体业务以外统一的流程在代理对象实现

简单的说就是代理对象拥有真实对象的实例，
并在代理对象加强真实对象的功能
####静态代理
JDK动态代理
    针对接口编程，运行时基于反射生成类的速度快调用后续类速度慢
    实现： ····· //TODO 待完善
CDLIB动态代理
    基于继承机制，继承被代理类，底层实现基于asm第三方框架
    实现：·····  //TODO 待完善
javassist
    运行时编译库

ps：spring默认使用的JDK动态代理，但是可以通过配置来使用CDLIB动态代理

####动态代理
##三、行为型模式

``````````````````分割线  end  ``````````````````

``````````````````分割线  start  ``````````````````
消息队列

## 为什么用
    解耦、异步

缺点：

高可用：
主从
分布式

幂等性问题
    加业务主键（redis、mysql）
保证消息可靠性传输
        
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
#IO
 UNIX网络编程
 
 同步/异步IO
    数据拷贝阶段
 阻塞/非阻塞IO      
    数据准备阶段

``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
#RPC（远程过程调用）
来源
    为 服务化技术 服务
        从一个war包 -> 分布式（垂直或水平拆分）
        （垂直：按系统按模块分割，产生子系统、子模块）
        （水平：集群？）

    简单的说为分布式垂直拆分之后系统间调用服务的一种方法论

手写rpc
    三个工程 api client server
    api是规范，client和server实现api
    
    client和server之间通过socket传输
    
    duboo底层通讯默认用的netty
    netty使用的是nio
    


``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
#数据库索引
1、什么是索引

    像一本字典，根据页码找到数据
    储存引擎用于快速找到记录的一种数据结构
    索引类型有B+Tree、Hash索引、位图索引。其中B+Tree最常见
    
2、什么是B+Tree

    B-Tree （balance - tree）：
    B+Tree：
    B-Tree 和 B+Tree 的区别
        
    
    
3、B+Tree 和 B-Tree区别 -- 重要
    B+Tree是B-Tree的一个升级版本，都叫多路平衡二叉树，更充分的利用节点空间，查询速度稳定。
    
    优缺点对比：
    
    
    
    问题1：索引和表在磁盘上如何储存：
    索引和表都以文件的方式被存储在磁盘页中
        数据库与内存交互以磁盘页为单位
        页大小一般为4K 8K 16K 32K
        I/O活动基于页为单位
        一个索引行、表行必须能够存入一个页中；（一行必须在一个页中，一个页可以有多行）
        每个页会预留一定比例的空间给新的索引行、表行
    
    问题2：磁盘页是怎么载入数据库内存的
        随机I/O、顺序I/O
        
        随机I/O：慢 400K/s
        顺序I/O：快 40M/s  全表扫描、 
        
       待补充 --- 什么情况是随机I/O什么情况是顺序I/O
    
    数据库优化原则：避免随机I/O
    
    
    
4、为什么要使用B+Tree
    1、文件很大，不可能全部在内存中，需要存到磁盘上
    2、尽量减少查找过程中磁盘I/O的存取次数
    3、io读取的单位为页，磁盘页大小等于节点大小
    4、局部性原理与磁盘预读
    5、利用数据库系统内磁盘预读原理，每个节点只需要一次IO就可以完全载入
    
5、什么是聚簇索引，优缺点是什么
    叶子节点包含了完整的数据记录的一种数据储存方式

    无法同时把数据行存放在两个不同的地方，所以一个表只能有一个聚簇索引
    ·········
    innodb通过主键聚集数据
    减少了磁盘I/O

    缺点：
        插入速度严重依赖插入顺序，按照主键的顺序插入式速度最快的方式
        更新聚集索引列的代价很高，因为会将每个被更新的行移动到新的位置
        可能面临页分裂的问题，当插入到某个已满的页中时，储存引擎会将该页分裂成两个页面，导致表占用更多的磁盘空间
        可能导致全表扫描变慢，由于页分裂导致数据存储不连续
        二级索引可能比想象的更大，因为在二级索引的叶子节点包含了主键列
        二级索引访问需要两次索引查找
        
        ----- 待补充 什么是二级索引

问题：1、redis为什么那么快，怎么样持久化

    2、kafka是存储数据的数据结构


``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
#多线程
    什么是线程：
      
    线程池原理       
       
       
       冷门知识：一个线程默认最大栈大小是1M
``````````````````分割线  end  ``````````````````
#索引
##查询性能低下的根本原因是什么
    1、访问的数据太多（返回的不一定多）
        1.1访问了太多行
        1.2访问了太多列
        1.3服务器分析大量不必要的数据行
        
        

##索引解决了什么问题
    以高效、最少扫描的方式找到需要的记录
    
##创建索引

## 联合索引（多列索引）
    是B树
    最左前匹配
    只支持简单匹配：= < > like in between
    范围匹配索引列，后面的所有列无效
    索引列的顺序很重要
    
    

##哈希索引
    类似java的HashMap
    无法用于排序
    不支持范围查询，只支持等值查询 = in
    不支持部分索引列匹配
    访问非常快，除非有哈希冲突。维护的代价也会非常高。
       
##覆盖索引
    
##回表
    概念：查询索引里边没有的列，不能通过索引直接取到数据，需要重新通过api交互从存储引擎获取数据
    
##sql运行过程
    

    
    
``````````````````分割线 start  ``````````````````
#索引
    为什么范围查询后面的索引列不能起到匹配的作用：
    范围查询之后后面的字段的顺序已经发生了变化，
    不再是之前的那个顺序了，也不在是树了

##多表联查
    本质：循环嵌套
    for(){ // 外层驱动表
        for(){ // 内层表
        
        }
    }
    原则：外层数据一定是要数据少的表，能减少随机I/O（外层表是随机I/O -- ？？？为什么）
    
    表和表之间的连接谓词要有索引

## 归并排序
        
## 优化经验

    偏移量过大问题：
        分页问题： select * from table limit 200000, 20;
        会查出200020条数据然后丢弃掉前200000条留下最后20条，
        效率会很低
    优化方案：
        方案一：先使用覆盖索引，再关联返回所需列
        方案二：缓存，需要一页查出五页，缓存4页（需要注意的是，有数据更新时怎么更新缓存）
        方案三：转换为范围查询，先范围查询筛选掉一部分

#性能优化
    1、表设计简单，能用一张表就绝对不要两张、三张（尽量每次查询只有一张表）（单表在分库分表的时候也方便）
    2、字段数据类型要简单，不要用复杂类型，如ENUM、SET
    3、冗余数据：建 汇总表、中间表 （尽量不要group by）
    4、索引有代价，需要取舍平衡
    
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
#消息队列
    为什么使用消息队列：解耦、异步

``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
##手写spring mvc
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
#zookeeper
##zookeeper的四种节点类型
##zookeeper脑裂
##zookeeper watch机制
##zookeeper curator api



  


``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````

``````````````````分割线 start  ``````````````````
``````````````````分割线  end  ``````````````````


软引用在gc时不会被回收，无论多少次，只会在内存不够的情况下，才会将软引用回收；而弱引用是第一次gc不回收，第二次gc就会回收掉；虚引用只是在回收时通知一下虚拟机
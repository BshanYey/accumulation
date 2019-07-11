#一、JVM运行时数据区
&emsp;&emsp;一般来讲JAVA内存结构说的是JVM运行时数据区的结构。<br/>
&emsp;&emsp;JVM运行时数据区包含以下几个部分：虚拟机栈（VM Stack）、本地方法栈（Native Method Stack）、堆（Heap）、方法区（Method Area）、程序计数器（Program Counter Register）。<br/>
&emsp;&emsp;其中，虚拟机栈、本地方法栈、程序计数器是线程私有的，堆、方法区是所有线程共享的。<br/>
![](jvm运行时数据区.png)

##1、程序计数器
&emsp;&emsp;简单的说就是告诉虚拟机下一条执行指令是什么。<br/>
&emsp;&emsp;此内存区域是唯一一个在java虚拟机规范中没有规定任何OutOfMemoryError情况的区域。<br/>
##2、JAVA虚拟机栈
&emsp;&emsp;线程私有，生命周期与线程相同。<br/>
&emsp;&emsp;每个方法在执行的时候会创建一个栈帧，用于存储局部变量表、操作数栈、动态链接、方法出口 等信息。<br/>
&emsp;&emsp;局部变量表存放了编译期可知的各种基本类型和对象引用（并非对象）和returnAddress类型（指向了一条字节码指令的地址）<br/>
&emsp;&emsp;注：局部变量表所需的空间在编译期间完成分配，当开始执行一份方法时，这个方法在栈帧中分配的局部变量空间是完全固定了，不会再方法执行期间发生变化。<br/>
&emsp;&emsp;该区域异常状况：<br/>
 * 1、线程请求的深度大于虚拟机所允许的最大深度，将抛出StackOverFlowError异常；<br/>
 * 2、在1的情况下，如果虚拟机栈允许动态扩展（当前大部分虚拟机都允许），在拓展时无法申请到足够的内存，会抛出OutOfMemoryError异常；<br/>
##3、本地方法栈
&emsp;&emsp;虚拟机栈为虚拟机执行Java方法，本地方法栈为虚拟机执行Native方法，其他的本地方法栈与虚拟机方法栈一致，甚至有的虚拟机直接把二者合二为一（比如Sun HotSpot虚拟机）。<br/>
##4、JAVA堆
&emsp;&emsp;JAVA堆是被所有线程共享的内存区域，在虚拟机启动的时候创建。此区域存在唯一目的就是用来存放对象实例，几乎所有的对象实例都存放在JAVA堆里边（逃逸分析了解一下？）。<br/>
&emsp;&emsp;当前主流的虚拟机都是按照可扩展来实现的（通过-Xmx -Xms来控制）。当堆中没有内存完成实例分配，并且堆无法在扩展的时候，会抛出OutOfMemoryError异常。<br/>
##5、方法区
&emsp;&emsp;和JAVA堆一样被所有线程共享。<br/>
&emsp;&emsp;方法区用于存储已经被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。<br/>
&emsp;&emsp;当方法区无法满足内存分配需求时，将抛出OutOfMemoryError异常<br/>
##6、运行时常量池
&emsp;&emsp;运行时常量池是方法区的一部分。虚拟机会在加载类之后将class文件里的常量池（class文件中除了有类的版本、字段、方法、接口等描述信息外，还有一项信息时常量池（Constant Pool Table），用于存放编译期生成的各种字面量和符号引用。）的内容放入运行时常量池。<br/>
&emsp;&emsp;运行时常量池具备动态性（JAVA语言并不要求只有编译期才能产生常量），运行期间也可以将新的常量放入常量池中（String类的intern方法了解一下）。<br/>
&emsp;&emsp;当常量池无法在申请到内存是会抛出OutOfMemoryError异常。<br/>
##7、直接内存
&emsp;&emsp;直接内存（Direct）并不是虚拟机运行时数据区域的一部分。但此部分内容被频繁使用也有可能导致OutOfMemoryError异常。<br/>
&emsp;&emsp;在JDK1.4加入了NIO类（一种基于通道（Channel）和缓冲区（Buffer）的I/O方式），它可以是有Native函数库直接分配堆外内存。<br/>
&emsp;&emsp;直接内存的大小不会受到虚拟机的限制。因此有可能在管理虚拟机是忽略此部分内存，导致虚拟机各部分区域内存总和加上直接内存大于物理内存限制，导致动态扩展时出现OutOfMemoryError异常。<br/>

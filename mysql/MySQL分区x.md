#  环境

MySQL5.7
Centos7



# 查看是否支持分区

**从MySQL5.1开始引入分区功能，用如下方式查看是否支持：**

​	“老”的版本用

```
SHOW VARIABLES LIKE ‘%partition%’;
```

​	新的版本用

```
show plugins;
```

​	查看支持的存储引擎

```
show engines;
```



# 创建分区

## 	1 创建RANGE分区

```
CREATE TABLE tbl_users1 (
	uuid INT NOT NULL,
	name VARCHAR(20),
	registerTime VARCHAR(100)
)
PARTITION BY RANGE (uuid) (
	PARTITION p0 VALUES LESS THAN (5),
	PARTITION p1 VALUES LESS THAN (10),
	PARTITION p2 VALUES LESS THAN (15),
	PARTITION p3 VALUES LESS THAN MAXVALUE
);
```

### 	1.1 添加数据和测试

```
show tables;
insert into tbl_users1 values(1,'x','2001-01-01');
insert into tbl_users1 values(8,'x','2001-01-02');
insert into tbl_users1 values(80,'x','2001-01-03');
select * from tbl_users1;
select * from tbl_users1 where uuid = 8;
```

表看不出来有任何区别，而且执行插入语句的时候也没有任何影响，然后查询看出来对应用是透明的，但是没看出分区啊！那这个分区体现在哪里呢？参考最后分区位置和信息查看张姐

## 	2 创建List分区

```
CREATE TABLE tbl_users2 (
	uuid INT NOT NULL,
	name VARCHAR(20),
	registerTime VARCHAR(100)
)

PARTITION BY List (uuid) (
	PARTITION p0 VALUES in (1,2,3,5),
	PARTITION p1 VALUES in (7,9,10),
	PARTITION p2 VALUES in (11,15)
);
```

### 	2.1 添加数据和测试

```
insert into tbl_users2 values(1,'x','2001-01-02');
insert into tbl_users2 values(2,'x','2001-01-03');
insert into tbl_users2 values(7,'x','2001-01-04');

select * from tbl_users2;
select * from tbl_users2 where uuid = 1;

select * from tbl_users2 partition(p0);
select * from tbl_users2 partition(p1);
select * from tbl_users2 partition(p2);
```

**（1）如果试图操作的列值不在分区值列表中时，那么会失败并报错。要注意的是，LIST分区没有类似如“VALUES LESS THAN MAXVALUE”这样的包含其他值在内的定义，将要匹配的任何值都必须在值列表中找到。**
**（2）LIST分区除了能和RANGE分区结合起来生成一个复合的子分区，与HASH和KEY分区结合起来生成复合的子分区也是可以的。**

## 3 创建非整形的RANGE,LIST分区

```
CREATE TABLE tbl_users3 (
	uuid INT NOT NULL,
	name VARCHAR(20),
	registerTime VARCHAR(100)
)
PARTITION BY RANGE columns (name) (
	PARTITION p0 VALUES LESS THAN ('id05'),	
	PARTITION p1 VALUES LESS THAN ('id10'),
	PARTITION p2 VALUES LESS THAN ('id15')
);
```

### 	3.1 添加数据和测试

```
insert into tbl_users3 values(2,'id07','2001-01-01');
insert into tbl_users3 values(3,'id02','2001-01-01');
insert into tbl_users3 values(4,'id12','2001-01-01');

select * from tbl_users3;

select * from tbl_users3 partition(p0);
select * from tbl_users3 partition(p1);
select * from tbl_users3 partition(p2);
```



## 	4 创建Hash分区

HASH分区主要用来确保数据在预先确定数目的分区中平均分布。在RANGE和LIST分区中，必须明确指定一个给定的列值或列值集合以指定应该保存在哪个分区中；

而在HASH分区中，MySQL自动完成这些工作，要做的只是基于将要被哈希的列值指定一个表达式，以及指定被分区的表将要被分割成的分区数量，如：

```
	CREATE TABLE tbl_users4 (
	uuid INT NOT NULL,
	name VARCHAR(20),
	registerTime VARCHAR(100)
)
	PARTITION BY HASH (uuid)	//uuid可以添加表达式，比如/2,或者mod(uuid,2)，性能低，每条数据要计算之后								在hash然后再插入
	PARTITIONS 3;
```

（1）由于每次插入、更新、删除一行，这个表达式都要计算一次；这意味着非常复杂的表达式可能会引起性			能问题，尤其是在执行同时影响大量行的运算（例如批量插入）的时候。
（2）最有效率的哈希函数是只对单个表列进行计算，并且它的值随列值进行一致地增大或减小，因为这考虑了在分区范围上的“修剪”。也就是说，表达式值和它所基于的列的值变化越接近，就能越有效地使用该表达式来进行HASH分区。

### 	4.1 添加数据和测试

```
insert into tbl_users4 values(10,'id7'，'2001-01-01');
insert into tbl_users4 values(11,'id07','2001-01-01');
insert into tbl_users4 values(12,'id02','2001-01-01');

select * from tbl_users4;

select * from tbl_users4 where uuid = 10;
select * from tbl_users4 where uuid = 11;
select * from tbl_users4 where uuid = 12;

select * from tbl_users4 partition(p0);
select * from tbl_users4 partition(p1);
select * from tbl_users4 partition(p2);
```



### 			4.2 线性Hash分区

线性哈希分区在“PARTITION BY” 子句中添加“LINEAR”关键字。
线性哈希分区的优点在于增加、删除、合并和拆分分区将变得更加快捷，有利于处理含有极其大量数据的表。它的缺点在于，各个分区间数据的分布不大可能均衡。



## 	5 Key分区

类似于按照HASH分区，Hash分区允许用户自定义的表达式，而Key分区不允许使用用户自定义的表达式； Hash分区只支持整数分区，Key分区支持除了blob或text类型之外的其他数据类型分区。
与Hash分区不同，创建Key分区表的时候，可以不指定分区键，默认会选择使用主键或唯一键作为分区键，没有主键或唯一键，就必须指定分区键。

```
CREATE TABLE tbl_users5 (
	uuid INT NOT NULL,
	name VARCHAR(20),
	registerTime VARCHAR(100)
)
PARTITION BY LINEAR Key (uuid)
PARTITIONS 3;
```

### 	5.1 添加数据和测试

```
insert into tbl_users5 values(12,'id7'，'2001-01-01');
insert into tbl_users5 values(7,'id07','2001-01-01');

select * from tbl_users5;
explain partition select * from tbl_user5 where uuid = 12;	//看看是key分区是如何分的？

select * from tbl_users5 partition(p0);
select * from tbl_users5 partition(p1);
select * from tbl_users5 partition(p2);

```



## 	6 子分区(复合分区)

​	在每个分区内，子分区的名字必须是唯一的，目前在整个表中，也要保持唯一。例如：

```
CREATE TABLE tbl_users6 (
	uuid INT NOT NULL,
	name VARCHAR(20),
	registerTime Date
)
PARTITION BY RANGE(YEAR(registerTime))
	SUBPARTITION BY HASH(TO_DAYS(registerTime))
	SUBPARTITIONS 2
	(
		PARTITION p0 VALUES LESS THAN (2008),
		PARTITION p1 VALUES LESS THAN (2015),
		PARTITION p2 VALUES LESS THAN MAXVALUE
		
	);
```

### 	6.1添加数据和测试

```
insert into tbl_users6 values(1,'x','2007-01-02');
insert into tbl_users6 values(2,'x','2009-01-03');
insert into tbl_users6 values(3,'x','2016-01-04');

select * from tbl_users6;

explain partition select * from tbl_user6 \G;	//看看是子分区是如何分的？
explain partition select * from tbl_user6  where registerTime='2007-01-02' \G;

select * from tbl_users6 partition(p0);
select * from tbl_users6 partition(p1);
select * from tbl_users6 partition(p2);
```



# 分区管理

## 1 RANGE和LIST分区的管理

### 			1.1 删除

​			删除分区语句如：

```
show create table 'tbl_users1' \G;
alter table tbl_users1 drop partition p0;
select * from tbl_user1;
```

​			（1）当删除了一个分区，也同时删除了该分区中所有的数据
			（2）可以通过show create table tbl_users1;来查看新的创建表的语句
			（3）如果是List分区的话，删除的数据不能新增进来，因为这些行的列值包含在已
			经删除了的分区的值列表中

### 			1.2 添加

​			添加分区语句如：

```
alter table tbl_users1 add partition(partition p4 values less than(50));//p3是最大值，所以不能添加p4
alter table tbl_users1 drop partition p3;						//p3是最大值，所以不能添加p4
show create table 'tbl_users1' \G;								//删除之后再看下分区
alter table tbl_users1 add partition(partition p4 values less than(50));//不能用p0
show create table 'tbl_users1' \G;								//添加之后再看下分区
```

​			（1）对于RANGE分区的表，只可以添加新的分区到分区列表的高端
			（2）对于List分区的表，不能添加已经包含在现有分区值列表中的任意值

### 			1.3 重建（拆分合并）分区不丢失数据

​			如果希望能不丢失数据的条件下重新定义分区，可以使用如下语句：	

```
ALTER TABLE tbl_name REORGANIZE PARTITION partition_list INTO (partition_definitions)
```

​			（1）拆分分区如：

```
alter table tbl_users1 REORGANIZE PARTITION p1 INTO(partition s0 values less than(5), partition s1 values less than(10));
show create table 'tbl_users1' \G;	
```

​			或者如（针对LIST）：

```
alter table tbl_users2 REORGANIZE PARTITION p0 INTO(partition s0 values in(1,2,3), partition s1 values in(4,5));
show create table 'tbl_users2' \G;	
select * from tbl_users2			//查看数据是否丢失
```

​			（2）合并分区如：

```
alter table tbl_users2 REORGANIZE PARTITION s0,s1 INTO(partition p0 values in(1,2,3,4,5));
select * from tbl_users2			//查看数据是否丢失
show create table 'tbl_users2' \G;	//在查看分区信息
```



### 			1.4 删除分区不丢失数据

​			删除所有分区，但保留数据，形如： 

```
show create table 'tbl_users1' \G;			//在查看分区信息
alter table tbl_users1 remove partitioning;
show create table 'tbl_users1' \G;			//没有分区信息了
select * from tbl_users1			//查看数据是否丢失
```

## 2 HASH和KEY分区的管理

### 			2.1 减少分区数量

​			减少分区数量语句如：

```
 show create table 'tbl_users4' \G;				//先看分区数量
 alter table tbl_users4 COALESCE PARTITION 1;
 show create table 'tbl_users4' \G;				//减少再看分区数量
```

### 			2.2 添加分区数量

​			添加分区数量语句如：

```
alter table tbl_users4 add PARTITION partitions 2;
  show create table 'tbl_users4' \G;			//添加再看分区数量
```

## 3 其他分区管理

### 		3.1：重建分区

​			类似于先删除保存在分区中的所有记录，然后重新插入它们，可用于整理分区碎片。如：

```
alter table tbl_users REBUILD PARTITION p2,p3;
```

### 		3.2：优化分区

​			如果从分区中删除了大量的行，或者对一个带有可变长度的行（也就是说，有VARCHAR，BLOB，或TEXT类型的列）作了许多修改，可以使用“ALTER TABLE ... OPTIMIZE PARTITION”来收回没有使用的空间，并整理分区数据文件的碎片。如：

```
alter table tbl_users OPTIMIZE PARTITION p2,p3;
```

### 		3.3：分析分区

​			读取并保存分区的键分布，如：

```
alter table tbl_users ANALYZE PARTITION p2,p3;
```

### 		3.4：检查分区

​			检查分区中的数据或索引是否已经被破坏，如：

```
alter table tbl_users CHECK PARTITION 	p2,p3;
```

### 		3.5：修补分区

​			 修补被破坏的分区，如：

```
alter table tbl_users REPAIR PARTITION p2,p3;
```

# 分区位置查看

## 	到存放数据的地方查看文件，路经配置在

```
show global variables like "%datadir%";
cd 到数据库
```

​		frm：表结构   ibd：索引和数据文件	看到我们的苍老师了吗？看到我们的小泽玛利亚老师了吗？

## 	可以通过下面的语句来查看表的分区信息：

```
select * from information_schema.partitions where table_schema=‘xxx’ and table_name=‘xxx’ \G; 
```

​		\G：按列显示

## 	可以通过下面的语句来查看分区上的数据

```
select * from tbl_users partition(p0);
```

## 	可以使用下面的语句来查看MySQL会操作的分区

```
explain partitions select * from tbl_users where uuid=80;
```

​		explain：查询计划，分析性能用的
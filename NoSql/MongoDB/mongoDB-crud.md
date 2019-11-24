#MongoDB的CRUD操作

##常用命令
    1、show dbs; // 查看所有已有的数据库 -- 注：空的数据库不会显示
    2、use dataBaseName; // 切换数据库，在不存在该数据库时（插入数据的时候）会创建该数据库
    3、db.dropDatabase(); // 删除当前选择的数据库
    4、db.createCollection("myDemo"); // 显示的创建集合
    5、show tables; // 查看数据库下所有集合
    6、show collections; // 同上
    7、db.myDemo.drop(); // 删除 myDemo 集合

##准备测试数据
    

##insert
    db.collection.insertOne()   // 将一个文档插入到集合中
    db.collection.insertMany()  // 将多个文档插入到集合中
    db.collection.insert()      // 将一个或多个文档插入到集合中
    
    示例：
        db.inventory.insertOne(
           { item: "canvas", qty: 100, tags: ["cotton"], size: { h: 28, w: 35.5, uom: "cm" } }
        );
        db.inventory.insertMany([
           { item: "journal", qty: 25, tags: ["blank", "red"], size: { h: 14, w: 21, uom: "cm" } },
           { item: "mat", qty: 85, tags: ["gray"], size: { h: 27.9, w: 35.5, uom: "cm" } },
           { item: "mousepad", qty: 25, tags: ["gel", "blue"], size: { h: 19, w: 22.85, uom: "cm" } }
        ]);
        db.inventory.insert([
           { item: "journal", qty: 25, tags: ["blank", "red"], size: { h: 14, w: 21, uom: "cm" } },
           { item: "mat", qty: 85, tags: ["gray"], size: { h: 27.9, w: 35.5, uom: "cm" } },
           { item: "mousepad", qty: 25, tags: ["gel", "blue"], size: { h: 19, w: 22.85, uom: "cm" } }
        ]);
    
    注意：
        1、自动创建不存在的集合
        2、如果不指定，自动生成主键_id及其值
        3、写操作都是基于单个文档的原子操作
        
    insert语法：
        db.collection.insert(
            <document or array of documents>,
            {
                writeConcern: <document>,
                ordered: <boolean>
            }
        )
        documents：必填，表示需要插入的文档，可以是多个文档
        writeConcern：可选项，写策略，// TODO 这里需要补充 写策略
        ordered：可选，表示多个文档是否按照文档顺序插入
        
##query
    
##update

##delete


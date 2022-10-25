1.docker
先启动一个容器，接下来会删除
docker run --name seata-server -p 8091:8091 -p 7091:7091 seataio/seata-server
拷贝出配置文件
docker cp seata-serve:/seata-server/resources /home/seata/resources
修改配置
/home/seata/resources/application.yml

2.下载脚本
地址:https://codeload.github.com/seata/seata/zip/refs/heads/develop。
取出script文件夹，进入script -> config-center -> nacos -> nacos-config.sh，修改nacos参数，执行nacos-config.sh。
到nacos修改自动创建的service.default.grouplist为seata服务器ip。

3.如果是db模式执行以下脚本


4.正式启动容器
docker run --name seata \
-p 8091:8091 \
-p 7091:7091 \
-v /home/seata/resources:/seata-server/resources  \
-e SEATA_IP=106.14.225.91 \
-d seataio/seata-server

5.每个业务数据库都
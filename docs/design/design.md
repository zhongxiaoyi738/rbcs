# 1 需求分析

## 1.1 场景限定

```
1. Core Functionality:
Implement a service that can process financial transactions and update account balances in real-time.
Each transaction should include a unique transaction ID, source account number, destination account number, amount, and timestamp.
The service should handle concurrent transactions and update balances accordingly.
```

- 根据这段需求描述，本模块聚焦于底层的账户余额处理：根据交易数据实时进行账户余额计算。

  - 其他业务场景如客户开卡、挂失、补卡等归属到其他领域
  - 账户余额计算服务，不直接对外。因此可以弱化身份认证、权限认证等功能
  - 账户余额计算为底层服务，它前面应该有直接对外的前置服务（如手续费扣减）、后面也应该有直接对外的后置服务（如短息通知等）。因此可以弱化前置服务与交易服务之间的中间人攻击等安全场景

- 与账户交易直接关联的业务场景如存钱、取钱、转账等由另外模块进行预处理之后，然后调用该模块完成账户余额变更。

  - 存钱：【source account number】为null
  - 取钱：【destination account number】为null
  - 转账：【source account number】为转出账户，【destination account number】为转入账户

- 关于amount，本模块也限定为单一币种，多币种之间的账户交易、amount统一换算成币种最小面值的10000倍也由另外模块进行预处理。

  - 假设币种=人民币，往账户存1元，则amount=1 * 100（转换成最小面值 分） * 10000
  - 跨币种转账：美金账号A往人民币账号B转账1美金，则amount=1 * 7.14（汇率）* 100（转入账户的最小面值） * 10000

  

## 1.2 功能需求

1. 接收交易请求

2. 验证交易信息

   - 交易去重：根据transaction ID判断当前交易是否为重复交易
   - 账号有效性验证：“source account number、destination account number”是否合法，且状态是否正常

3. 账户余额判断

   - 检查source account number余额是否足够，这里需要判断锁

4. 余额变更：加锁处理

   - 存钱：【destination account number】 += amount
   - 取钱：【source account number】 -= amount
   - 转账：【source account number】 -= amount，【destination account number】 += amount

   

## 1.3 非功能需求

```
2. High Availability and Resilience:
Deploy the service on a Kubernetes cluster (AWS EKS, GCP GKE, Alibaba ACK). Use Kubernetes features like Deployment, Service, and Horizontal Pod
Autoscaler(HPA) to ensure high availability and scalability. Implement a retry mechanism for failed transactions.
Use a managed database service(e.g., AWS RDS, GCP Cloud SQL, Alibaba RDS) to store account and transaction data.
Ensure data consistency and integrity using database transactions and locks where necessary.
3. Performance:
Optimize the service to handle high-frequency transactions.
Implement caching using a distributed caching service (e.g., AWS ElastiCache, GCP Memorystore, Alibaba Cloud ApsaraDB for Redis).
```

1. 性能要求

   - 系统响应延迟 < 1s
   - tps >= 1000
   - 资源利用率：cpu <= 70%，内存 <= 70%

2. 可用性

   - 失败重试
   - 故障自动恢复

3. 扩展性

   - 支持动态扩缩容
   - 支持新业务快速接入

4. 安全性

   - 认证（公用认证服务）
   - 参数验证（公用参数验签服务）
   - 来源验证

5. 可观测性

   - 服务器监控：cpu、内存、I/O、网络带宽
   - 服务监控：服务健康监控、服务资源监控
   - 资源监控：mysql、redis、nacos、kafka
   - 业务监控：接口访问量、

   

   



# 2 架构设计

1. 功能模块图、系统模块、部署架构，见《docs\design\rbcs-架构设计.pdf》







# 3 详细设计

## 3.1 DB设计

见《docs\design\PhysicalDataModel_hsbc.pdm》

1. 用户&schema

   docs\sql\10-schema_user-ddl.sql

2. 表ddl

   docs\sql\20-rbcs-ddl.sql

   <!--说明：账户account表应该是隶属于账户schema的、手工sql日志表隶属于系统schema-->

3. mock数据

   docs\sql\30-rbcs-dml.sql

## 3.2 接口设计

http://localhost:8031/rbcp/balance/doc.html

见《docs\design\接口.png》、《docs\design\接口调用示例.png》

```curl
curl --location --request POST 'http://47.96.102.213:30261/rbcp/balance/accountBalance/addTrade' \
--header 'Content-Type: application/json' \
--data-raw '{
    "uuid": 2,
    "uuno": "2",
    "srcAccountUuno": "2025030114525900005",
    "descAccountUuno": "2025030114525900006",
    "amount": 100000000
}'
```



## 3.3参数设计

见《docs\nacos_config_export.zip》

导入nacos之后，如图docs\design\nacos配置中心.png

导入之后，需要修改如下参数：

1. application.yml，修改为redis服务器地址（请使用redis单机、或者是通过代理）

   ```
   spring:
     redis:
       redisson:
         config: |
           singleServerConfig:
             address: "redis://r-bp1ij6ue7mbntroon5.redis.rds.aliyuncs.com:6379"
   ```

2. balance-app-datasource.yml

   ```
   spring:
     datasource:
       driver-class-name: com.p6spy.engine.spy.P6SpyDriver
       url: jdbc:p6spy:mysql://rm-cn-rno45pl87000p7do.rwlb.rds.aliyuncs.com:3306/rbcs?useTimezone=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true
       username: rbcs_admin
       password: Rbcs@1234
   ```

   

## 3.4 业务流程设计

见《docs\design\rbcs-架构设计.pdf》

processon文件数有限，所以所有图画在一个文件中





## 3.5 测试用例设计

见《docs\test》

### 3.5.1 单元测试

由于时间关系，只写了controller层有限几个用例

### 3.5.2 压力测试

见《docs\test\jmeter》

先存款后取款、转账，这样可以有效减少因为源账号余额不足导致的失败

存款参数文件：deposit.csv

取款参数文件：withdraw.csv

转账参数文件：transfer.csv

因为使用了redis stream做消息队列，（代码中自动修剪stream长度的代码注释了）因此可以考虑一次只压测10000条

### 3.5.2 弹性测试

通过开放优雅下线接口(/rbcs/{SVC}/actuator/deregister) + nacos配置中心，实现pod的扩缩容以及滚动发版







# 4 部署

1. 环境准备

   - nacos2.4.2
   - redis，版本大于6
   - mysql8
   - K8S

2. 修改参数

   - 见《3.3参数设计》

   - 修改启动参数中nacos地址

     rbcs\rbcs-public-app\rbcs-gateway-app\src\main\resources\bootstrap-dev.yml

     rbcs\rbcs-app\rbcs-balance-app\rbcs-balance-app-biz\src\main\resources\bootstrap-dev.yml

     ```yml
     spring:
       cloud:
         nacos:
           server-addr: mse-8ad681a0-nacos-ans.mse.aliyuncs.com:8848
           contextPath: nacos
           discovery:
             namespace: ${spring.profiles.active}
             group: DEFAULT_GROUP
     ```

   
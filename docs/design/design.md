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
  - 账户余额计算为底层服务，它前面应该有直接对外的前置服务、后面也应该有直接对外的后置服务。因此可以弱化前置服务与交易服务之间的中间人攻击等安全场景

- 与账户交易直接关联的业务场景如存钱、取钱、转账等由另外模块进行预处理之后，然后调用该模块完成账户余额变更

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
2. 可用性
   - 失败重试
   - 故障自动恢复
3. 



# 2 架构设计
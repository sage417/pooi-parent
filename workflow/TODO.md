- [x] 事件推送
- [x] shardingsphere接入
- [x] 动态加签 app.pooi.workflow.application.UserTaskAddSignApplication.addSignTask
- [x] 分布式缓存 app.pooi.workflow.configuration.flowable.engine.ProcessDefinitionDeploymentCache
- [ ] 分享/传阅 app.pooi.workflow.application.UserTaskOperationApplication.addCirculate
- [ ] 审批历史 app.pooi.workflow.applicationsupport.workflowcomment.CommentSupport
- [x] 任务回退/跳转 app.pooi.workflow.application.ProcessInstanceMoveApplication.rollback
- [x] 任务挂起 app.pooi.workflow.application.UserTaskSuspendApplication
- [ ] 审批全权委托/协助
- [ ] 任务条件自动审批
- [ ] 审批人异常处理
- [ ] 任务超时处理
- [ ] 办理人员规则/审批人对接人员/组织关系
- [ ] 流程流转显示
- [ ] ~~标题自动计算~~
- [ ] 多语言
- [ ] 测试
- [x] 对接iam

### 代理/委托/协助/加签 定义

代理： 允许他人已自己的名义进行审批，审批人显示是自己，非代理人

委托： 全权委托他人审批，自己看不到审批，也无法处理审批
协助： 邀请他人协助审批，自己和协助人可共同审批

委托、协作 无论指定多少人，仅需一人处理审批即完成

| 类型        | 显示审批人        | 可审批范围 | 穿透  |
|-----------|--------------|-------|-----|
| 显示委托(常见)  | B (由 A 委托)   | B     | 不穿透 |
| 隐式委托(较常见) | B            | B     | 不穿透 |
| 代理委托(不常见) | A            | B     | 不穿透 |
| 显示协助(常见)  | A & B (协助 A) | A+B   | 不穿透 |
| 代理协助(不常见) | A            | A+B   | 不穿透 |

（动态）前/后加签： 邀请他人协助共同审批，需要所有人都处理完毕
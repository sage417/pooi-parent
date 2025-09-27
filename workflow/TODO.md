- [x] 事件推送
- [x] shardingsphere接入
- [x] 动态加签 app.pooi.workflow.application.UserTaskAddSignApplication.addSignTask
- [x] 分布式缓存 app.pooi.workflow.configuration.flowable.engine.ProcessDefinitionDeploymentCache
- [ ] 分享/传阅 app.pooi.workflow.application.UserTaskOperationApplication.addCirculate
- [ ] 审批历史 app.pooi.workflow.applicationsupport.workflowcomment.CommentSupport
- [x] 任务回退/跳转 app.pooi.workflow.application.ProcessInstanceMoveApplication.rollback
- [x] 任务挂起 app.pooi.workflow.application.UserTaskSuspendApplication
- [ ] 审批全权代理/共享
- [ ] 任务条件自动审批
- [ ] 审批人异常处理
- [ ] 任务超时处理
- [ ] 办理人员规则/审批人对接人员/组织关系
- [ ] 流程流转显示
- [ ] ~~标题自动计算~~
- [ ] 多语言
- [ ] 测试
- [x] 对接iam

### 代操作/代理/共享/加签 定义

任务代办(Task Agency) 为 代操作/代理/共享 统称

代操作(On Behalf Of)： 允许他人以自声名义进行审批，审批人显示为自身，非操作人

代理(Delegation)： 全权委托他人审批，自己看不到审批，也无法处理审批

共享(Share)： 邀请他人协助审批，自己和协助人可共同审批

代理、共享 无论指定多少人，仅需一人处理审批即完成 ，**代理规则优先于共享规则**

| 类型         | 显示审批人           | 可审批范围 | 穿透  |
|------------|-----------------|-------|-----|
| 显示代理(常见)   | B (由 A 委托审批)    | B     | 不穿透 |
| 隐式代理(较常见)  | B               | B     | 不穿透 |
| 代操作代理(不常见) | A               | B     | 不穿透 |
| 显示共享(常见)   | A & B (协助 A 审批) | A+B   | 不穿透 |
| 代理共享(不常见)  | A               | A+B   | 不穿透 |

(动态)前/后加签(Add Sign)： 邀请他人协助共同审批，需要所有人都处理完毕
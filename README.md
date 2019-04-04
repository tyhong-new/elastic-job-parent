# elastic-job-parent


- 基于spring注解做的集成

- 模仿org.springframework.context.event.EventListenerMethodProcessor#afterSingletonsInstantiated写的。

- 优点：

	1. 使得elastic-job可以方便的使用注解，同时job不需要实现官方的SimpleJob接口，且注解实体为方法。
	2. 添加了LocalJob注解，使得每个实例job都可以同时运行，适用于处理本地的任务，如清理本地缓存等。


- 不足：
	1. 目前只支持SimpleJob，不支持ScriptJob和DataflowJob。
    2. 只处理添加注解的方法，不会处理其他方式添加的job。
    3. spring扫描的时候，要从com扫起。从而避免没扫到helper的情况
    

- helper是集成,example是例子


## spring-starter 


- 在原来helper的基础上改成了spring-starter，使得和spring-boot无缝集成。


- 解决了spring指定扫描包的情况。

-  不足，之前的不足还在：
	1. 目前只支持SimpleJob，不支持ScriptJob和DataflowJob。
    2. 只处理添加注解的方法，不会处理其他方式添加的job
 
- elastic-job-helper-spring-boot-starter是集成,elastic-job-starter-example是例子
# elastic-job-parent
基于spring注解做的集成
模仿org.springframework.context.event.EventListenerMethodProcessor#afterSingletonsInstantiated写的。
使得elastic-job可以方便的使用注解，同时job不需要实现官方的SimpleJob接口，且注解实体为方法。
添加了LocalJob注解，使得每个实例job都可以同时运行，适用于处理本地的任务，如清理本地缓存等。

不足：目前只支持SimpleJob，不支持ScriptJob和DataflowJob。
    只处理添加注解的方法，不会处理其他方式添加的job。
    spring扫描的时候，要从com扫起。
    

helper是集成
example是例子
    

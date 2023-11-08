package io.github.hpsocket.soa.starter.kafka.consumer.aspect;

import org.apache.logging.log4j.core.config.Order;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/** <b>Kafka Listener Tracing 拦截器</b><br>
 * 用于注入 traceId
 */
@Aspect
@Order(Integer.MIN_VALUE)
public class KafkaListenerTracingInspector
{
    @Pointcut(KafkaListenerMdcInspector.INSPECTOR_POINTCUT_PATTERN)
    protected void aroundMethod() {}
    
    @Trace
    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        return joinPoint.proceed();
    }
}

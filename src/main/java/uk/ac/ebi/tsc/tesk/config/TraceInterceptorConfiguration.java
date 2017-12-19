package uk.ac.ebi.tsc.tesk.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Ania Niewielska <aniewielska@ebi.ac.uk>
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Aspect
public class TraceInterceptorConfiguration {

    @Pointcut("execution(public * uk.ac.ebi.tsc.tesk.util.component.KubernetesClientWrapper+.create*(..))")
    public void createMethods() {
    }

    @Pointcut("execution(public * uk.ac.ebi.tsc.tesk.util.component.KubernetesClientWrapper+.list*(..))")
    public void listMethods() {
    }

    @Pointcut("execution(public * uk.ac.ebi.tsc.tesk.util.component.KubernetesClientWrapper+.read*(..))")
    public void readMethods() {
    }

    @Pointcut("listMethods() || readMethods()")
    public void getMethods() {
    }

    @Bean
    public CustomizableTraceInterceptor createTaskInterceptor() {
        CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
        customizableTraceInterceptor.setUseDynamicLogger(true);
        customizableTraceInterceptor.setEnterMessage("START: $[methodName], ARGUMENTS: $[arguments]");
        customizableTraceInterceptor.setExitMessage("END:  $[methodName](), RESULT: $[returnValue], TIME: $[invocationTime]");
        customizableTraceInterceptor.setExceptionMessage("END: $[methodName] thrown $[exception]");
        customizableTraceInterceptor.setLogExceptionStackTrace(false);
        return customizableTraceInterceptor;
    }

    @Bean
    public CustomizableTraceInterceptor getTaskInterceptor() {
        CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
        customizableTraceInterceptor.setUseDynamicLogger(true);
        customizableTraceInterceptor.setEnterMessage("START: $[methodName], ARGUMENTS $[arguments]");
        customizableTraceInterceptor.setExitMessage("END:  $[methodName], TIME: $[invocationTime]");
        customizableTraceInterceptor.setExceptionMessage("END: $[methodName] thrown $[exception]");
        customizableTraceInterceptor.setLogExceptionStackTrace(false);
        return customizableTraceInterceptor;
    }

    @Bean
    public Advisor createTaskTraceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("uk.ac.ebi.tsc.tesk.config.TraceInterceptorConfiguration.createMethods()");
        return new DefaultPointcutAdvisor(pointcut, createTaskInterceptor());
    }

    @Bean
    public Advisor getTaskTraceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("uk.ac.ebi.tsc.tesk.config.TraceInterceptorConfiguration.getMethods()");
        return new DefaultPointcutAdvisor(pointcut, createTaskInterceptor());
    }
}
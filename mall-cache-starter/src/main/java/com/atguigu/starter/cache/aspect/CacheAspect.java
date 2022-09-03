package com.atguigu.starter.cache.aspect;

import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpService;
import com.atguigu.starter.cache.annotation.GmallCache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author tkwrite
 * @create 2022-09-01-19:52
 */
@Aspect  //声明这是一个切面
@Component
public class CacheAspect {
    @Autowired
    CacheOpService cacheOpService;
    //创建一个表达式解析器，这个是线程安全的
    SpelExpressionParser parser = new SpelExpressionParser();
     ParserContext context=new  TemplateParserContext();

    // @Before("@annotation(com.atguigu.starter.cache.annotation.GmallCache)")
    // public void haha(){
    //     System.out.println("触发前置通知....");
    // }

    /**
     * 目标方法：public SkuDetailTo getSkuDetailWithCache(Long skuId)
     * 连接点：所有的目标方法信息都在连接点
     * try{
     *    前置通知
     *    目标方法.invoke(args)
     *    返回通知
     * }catch(Exception e){
     *     异常通知
     * }finally{
     *     后置通知
     * }
     *
     */
    @Around("@annotation(com.atguigu.starter.cache.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result=null;

        //TODO key不同方法可能不一样
        String cacheKey= determinCacheKey(joinPoint);

        //1.先查缓存  //TODO 不同方法返回数据不一样
        Type returnType=getMethodGenericReturnType(joinPoint);
        // SkuDetailTo cacheData = cacheOpService.getCacheData(cacheKey, SkuDetailTo.class);
        Object  cacheData = cacheOpService.getCacheData(cacheKey, returnType);
        //2.缓存
        if (cacheData==null){
        //    3.准备回源
        //    4.先问布隆   有些场景并不一定需要布隆，比如：三级分类（只有一个大数据）
        //     boolean contains = cacheOpService.bloomContains(arg);
        //     boolean contains =cacheOpService.bloomContains(bloomName,arg);
            String bloomName=determinBloomName(joinPoint);
            if (!StringUtils.isEmpty(bloomName)){
            //    指定开启了布隆
                Object bVal=determinBloomValue(joinPoint);
            boolean contains = cacheOpService.bloomContains(bloomName, bVal);
            if (!contains){
                return null;
            }
            }

        //    5.布隆说有，可能有。但有击穿风险
            boolean lock = false;
            String lockName="";
            try {
            //    不同场景用自己的锁
                lockName=determinLockName(joinPoint);
            lock=cacheOpService.tryLock(lockName);
            if (lock){
            //    6.获取到锁，开始回源
                result = joinPoint.proceed(joinPoint.getArgs());
                long ttl=determinTtl(joinPoint);
            //    7.调用成功，重新保存到缓存
                cacheOpService.saveData(cacheKey,result,ttl);
                return result;
            //
            }else {
                Thread.sleep(1000);
                return cacheOpService.getCacheData(cacheKey, returnType);
            }
            }finally {
                if (lock)cacheOpService.unlock(lockName);
            }
        }
        //缓存中有 直接返回
        return cacheData;

    //
    // //    1.获取签名，将要执行的目标方法的签名
    //     MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    // //    2.获取当时调用者调用目标方法时传递的所有参数
    //     Object[] args = joinPoint.getArgs();
    //
    //     System.out.println(joinPoint.getTarget());
    //     System.out.println(joinPoint.getThis());
    //
    // //    3.放行目标方法
    //     Method method = signature.getMethod();
    //     //前置通知
    //     Object result=null;
    //     try {
    //     //目标方法执行,并返回返回值   修改参数
    //         result = method.invoke(joinPoint.getTarget(),args);
    //     //    返回通知
    //
    //     } catch (Exception e) {
    //         //异常通知
    //         // e.printStackTrace();
    //         throw new RuntimeException(e);
    //     }finally {
    //     //    后置通知
    //     }
    //     //修改返回值
    //     return result;
    }

    private long determinTtl(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        long ttl = cacheAnnotation.ttl();

        return ttl;
    }

    /**
     * 根据表达式计算出要用的锁的名字
     * @param joinPoint
     * @return
     */
    private String determinLockName(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        //3.拿到锁表达式
        String lockName = cacheAnnotation.lockName(); //lock-方法名
        if (StringUtils.isEmpty(lockName)){
            //没指定锁用指定方法锁
            return SysRedisConst.LOCK_PREFIX+method.getName();
        }
        //4.计算锁值
        String exception = evaluationException(lockName, joinPoint, String.class);
        return exception;
    }

    /**
     * 根据布隆过滤器值表达式计算出布隆需要判定的值
     * @param joinPoint
     * @return
     */
    private Object determinBloomValue(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        //3.拿到布隆值表达式
        String bloomValue = cacheAnnotation.bloomValue();
        Object exception = evaluationException(bloomValue, joinPoint, Object.class);
        return exception;
    }

    /**
     * 获取布隆过滤器的名字
     * @param joinPoint
     * @return
     */
    private String determinBloomName(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        String bloomName = cacheAnnotation.bloomName();
        return bloomName;

    }

    /**
     * 获取目标方法的精确返回值类型
     * @param joinPoint
     * @return
     */
    private Type getMethodGenericReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Type type = signature.getMethod().getGenericReturnType();
        return type;
    }

    /**
     * 根据当前整个连接点的执行信息，确定缓存用什么key
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        String expression = cacheAnnotation.cacheKey();
        //3.根据表达式计算缓存键
        String cacheKey=evaluationException(expression,joinPoint,String.class);
        return cacheKey;
    }

    private<T> T evaluationException(String expression, ProceedingJoinPoint joinPoint, Class<T> cla) {
        //1.表达式解析器
        Expression exp = parser.parseExpression(expression, context);
        //2.sku:info:#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        //3.取出所有参数，绑定到上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params",args);
        //4.得到表达式的值
        T expValue = exp.getValue(evaluationContext, cla);
        return expValue;
    }

}

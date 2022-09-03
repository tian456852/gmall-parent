package com.atguigu.gmall.common.retry;

import feign.RetryableException;
import feign.Retryer;

/**
 * @author tkwrite
 * @create 2022-09-03-0:16
 */
public class MyRetryer implements Retryer {

    private int cur = 0;
    private int max = 2;

    public MyRetryer(){
        cur = 0;
        max = 2;
    }

    /**
     * 继续重试还是中断重试
     * @param e
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
//        throw e;
        if(cur++ > max){
            throw  e;
        }
    }

    @Override
    public Retryer clone() {
        return new MyRetryer();
    }
}

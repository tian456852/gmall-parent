package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.UUID;

/**
 * @author tkwrite
 * @create 2022-09-01-23:12
 */
public class SpelTest {
    @Test
    void test04(){

        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("xixi-#{T(java.util.UUID).randomUUID().toString()}",new TemplateParserContext());
        System.out.println(expression.getValue());

    }
    @Test
    void test03(){

        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("new int[]{1,2,3,1}");
        int i[]= (int[]) expression.getValue();
        for (int i1 : i) {
            System.out.println(i1);
        }
    }
    @Test
    void test02(){

        Object[] params = {49, 60};
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("sku:info:#{#params[0]}",new TemplateParserContext());
        //1.准备一个计算上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //2。变量和上下文环境绑定 #xxx 代表从上下文中取出一个变量
        context.setVariable("params",params);
        // Object value = expression.getValue(context);
        // System.out.println(value);
        String value = expression.getValue(context, String.class);
        System.out.println(value);
    }
    @Test
    void test01(){
    //    1.创建一个表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

    //    2.准备一个表达式 'Hello #{1+1}'
    //    告诉spring遇见定界符(#{、})内部所有东西都需要动态计算
        String myExpression="Hello #{1+1}";
        //3.得到一个表达式
        Expression expression = parser.parseExpression(myExpression, new TemplateParserContext());
        Object value = expression.getValue();
        System.out.println(value);


    }

}

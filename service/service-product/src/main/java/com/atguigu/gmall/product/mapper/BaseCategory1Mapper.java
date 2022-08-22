package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author tkwrite
 * @create 2022-08-22-21:36
 */
/**
 * 为了让操作数据库的Mapper组件放在容器中
 * 1、每个组件标注@Mapper注解
 * 2、@MapperScan("com.atguigu.gmall.product.mapper") //自动扫描这个包下的所有Mapper接口
 */
// @Mapper
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {
}

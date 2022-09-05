package com.atguigu.gmall.model.vo.search;

import lombok.Data;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-05-17:14
 */
@Data
public class AttrVo {
    // 平台属性Id
    private Long attrId;
    // 平台属性值名称
    private String attrName;
    //每个属性涉及到的所有值集合
    // 平台属性名
    private List<String> attrValueList;

}

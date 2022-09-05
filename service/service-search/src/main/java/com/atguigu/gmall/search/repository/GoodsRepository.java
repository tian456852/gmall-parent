package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tkwrite
 * @create 2022-09-03-21:46
 */
@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}

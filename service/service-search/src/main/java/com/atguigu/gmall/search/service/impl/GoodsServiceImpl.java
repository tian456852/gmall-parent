package com.atguigu.gmall.search.service.impl;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.vo.search.*;
import com.google.common.collect.Lists;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author tkwrite
 * @create 2022-09-05-15:34
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchRestTemplate esRestTemplate;

    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void deleteGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 去ES检索商品
     * @param searchParamVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        //1.动态构建出搜索条件
        Query query=buildQueryDsl(searchParamVo);

        //2.搜索
        SearchHits<Goods> goods = esRestTemplate
                .search(query,
                        Goods.class,
                        IndexCoordinates.of("goods"));
        //3.将搜索条件进行转换
        SearchResponseVo responseVo=buildSearchResponseResult(goods,searchParamVo);

        return responseVo;
    }

    @Override
    public void updateHotScore(Long skuId, Long score) {
        //1.找到商品
        Goods goods = goodsRepository.findById(skuId).get();

        //2.更新得分
        goods.setHotScore(score);
        //3.同步到es
        goodsRepository.save(goods);

        //ES可以发送修改DSL，只更新hotScore字段
    }

    /**
     * 根据检索到的记录 构建响应结果
     * @param goods
     * @return
     */
    private SearchResponseVo buildSearchResponseResult(SearchHits<Goods> goods,
                                                       SearchParamVo searchParamVo) {

        SearchResponseVo responseVo = new SearchResponseVo();
        //1、当时检索前端传来的所有参数
        responseVo.setSearchParam(searchParamVo);
        //2.构建品牌面包屑  trademark=1:小米
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())){
            responseVo.setTrademarkParam("品牌："+searchParamVo.getTrademark().split(":")[1]);
        }
        if(searchParamVo.getProps()!=null && searchParamVo.getProps().length>0){
            List<SearchAttr> propsParamList=new ArrayList<>();
            for (String prop : searchParamVo.getProps()) {
                //23:8G:运行内存
                String[] split = prop.split(":");
                //一个SearchAttr 代表一个属性面包屑
                SearchAttr searchAttr = new SearchAttr();
                //平台属性Id
                searchAttr.setAttrId(Long.parseLong(split[0]));
                //平台属性值
                searchAttr.setAttrValue(split[1]);
                //平台属性名
                searchAttr.setAttrName(split[2]);
                propsParamList.add(searchAttr);
            }
            responseVo.setPropsParamList(propsParamList);
        }

        //TODO 4、所有品牌列表 。需要ES聚合分析
        List<TrademarkVo> trademarkList=buildTrademarkList(goods);
        responseVo.setTrademarkList(trademarkList);

        //TODO 5、所有属性列表 。需要ES聚合分析
        List<AttrVo> attrsList=buildAttrsList(goods);
        responseVo.setAttrsList(attrsList);

        //为了回显
        //6、返回排序信息  order=1:desc
        if (!StringUtils.isEmpty(searchParamVo.getOrder())){
           String order= searchParamVo.getOrder();
           OrderMapVo mapVo=new OrderMapVo();
           mapVo.setType(order.split(":")[0]);
           mapVo.setSort(order.split(":")[1]);
           responseVo.setOrderMap(mapVo);
        }
        //7、所有搜索到的商品列表
        List<Goods> goodsList = new ArrayList<>();
        List<SearchHit<Goods>> hits = goods.getSearchHits();
        for (SearchHit<Goods> hit : hits) {
            //这条命中记录的商品
            Goods content = hit.getContent();
            //如果模糊检索了，会有高亮标题
            if(!StringUtils.isEmpty(searchParamVo.getKeyword())){
                String highlightTitle = hit.getHighlightField("title").get(0);
                //设置高亮标题
                content.setTitle(highlightTitle);
            }
            goodsList.add(content);
        }

        responseVo.setGoodsList(goodsList);

        //8、页码
        responseVo.setPageNo(searchParamVo.getPageNo());
        //9、总页码？
        long totalHits = goods.getTotalHits();
        long ps = totalHits%SysRedisConst.SEARCH_PAGE_SIZE == 0?
                totalHits/SysRedisConst.SEARCH_PAGE_SIZE:
                (totalHits/SysRedisConst.SEARCH_PAGE_SIZE+1);
        responseVo.setTotalPages(new Integer(ps+""));

        //10、老连接。。。   /list.html?category2Id=13
        String url = makeUrlParam(searchParamVo);
        responseVo.setUrlParam(url);

        return responseVo;

    }

    /**
     * 分析得到 当前检索的结果中，所有商品涉及了多少种平台属性
     * @param goods
     * @return
     */
    private List<AttrVo> buildAttrsList(SearchHits<Goods> goods) {
        List<AttrVo> attrVos=new ArrayList<>();
        //1.拿到整个属性的聚合结果
        ParsedNested attrAgg = goods.getAggregations().get("attrAgg");

        //2.拿到属性id的聚合结果
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        //3.遍历所有属性id
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            AttrVo attrVo=new AttrVo();
            //3.1属性id
            Long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            //3.2属性名
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //3.3所有属性值
            List<String> attrValues=new ArrayList<>();
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            for (Terms.Bucket ValueBucket : attrValueAgg.getBuckets()) {
                String value = ValueBucket.getKeyAsString();
                attrValues.add(value);
            }
            attrVo.setAttrValueList(attrValues);
            attrVos.add(attrVo);
        }
        return attrVos;
    }

    /**
     * 分析得到 当前检索的结果中，所有商品涉及了多少种品牌
     * @param goods
     * @return
     */
    private List<TrademarkVo> buildTrademarkList(SearchHits<Goods> goods) {
        List<TrademarkVo> trademarkVos=new ArrayList<>();
        //拿到tmIdAgg 聚合
        ParsedLongTerms tmIdAgg = goods.getAggregations().get("tmIdAgg");
        // 拿到品牌id桶聚合中的每个数据
        for (Terms.Bucket bucket : tmIdAgg.getBuckets()) {
            TrademarkVo trademarkVo = new TrademarkVo();

            //    1.获取品牌id
            Long tmId = bucket.getKeyAsNumber().longValue();
            trademarkVo.setTmId(tmId);


        //    2.获取品牌名
            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmName(tmName);
            //3.获取品牌logo
            ParsedStringTerms tmLogoUrlAgg = bucket.getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmLogoUrl(tmLogoUrl);

            trademarkVos.add(trademarkVo);
        }

        return trademarkVos;
    }

    /**
     * 制造老连接
     * @param searchParamVo
     * @return
     */
    private String makeUrlParam(SearchParamVo searchParamVo) {
        // list.html?&k=v
        StringBuilder builder = new StringBuilder("list.html?");
        //1、拼三级分类所有参数
        if(searchParamVo.getCategory1Id()!=null){
            builder.append("&category1Id="+searchParamVo.getCategory1Id());
        }
        if(searchParamVo.getCategory2Id()!=null){
            builder.append("&category2Id="+searchParamVo.getCategory2Id());
        }
        if(searchParamVo.getCategory3Id()!=null){
            builder.append("&category3Id="+searchParamVo.getCategory3Id());
        }

        //2、拼关键字
        if(!StringUtils.isEmpty(searchParamVo.getKeyword())){
            builder.append("&keyword="+searchParamVo.getKeyword());
        }

        //3、拼品牌
        if(!StringUtils.isEmpty(searchParamVo.getTrademark())){
            builder.append("&trademark="+searchParamVo.getTrademark());
        }

        //4、拼属性
        if(searchParamVo.getProps()!=null && searchParamVo.getProps().length >0){
            for (String prop : searchParamVo.getProps()) {
                //props=23:8G:运行内存
                builder.append("&props="+prop);
            }
        }

//        //5、拼排序
//        builder.append("&order="+searchParamVo.getOrder());
//
//        //6、拼页码
//        builder.append("&pageNo="+searchParamVo.getPageNo());


        //拿到最终字符串
        String url = builder.toString();
        return url;
    }

    /**
     * 根据前端传递来的所有请求参数构建检索条件
     * DSL：
     * 1、查询条件【分类、关键字、品牌、属性】
     * 2、排序分页【排序、分页】
     * 3、高亮
     * @param searchParamVo
     * @return
     */
    //根据前端传来的所有请求参数构建搜索条件
    private Query buildQueryDsl(SearchParamVo searchParamVo) {
        //1.准备bool条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //2.给bool中准备must的各个条件
        //2.1）前端传了Category1Id
        if(searchParamVo.getCategory1Id()!= null){
            boolQuery
                    .must(QueryBuilders
                                  .termQuery("category1Id",
                                             searchParamVo.getCategory1Id()));
        }
        if(searchParamVo.getCategory2Id()!=null){
            boolQuery.must(QueryBuilders.termQuery("category2Id",searchParamVo.getCategory2Id()));
        }
        if(searchParamVo.getCategory3Id()!=null){
            boolQuery.must(QueryBuilders.termQuery("category3Id",searchParamVo.getCategory3Id()));
        }
        //2.2）、前端传了 keyword。要进行全文检索
        if(!StringUtils.isEmpty(searchParamVo.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("title",searchParamVo.getKeyword()));
        }

        //2.3）、前端传了品牌 trademark=4:小米
        if(!StringUtils.isEmpty(searchParamVo.getTrademark())){
            long tmId = Long.parseLong(searchParamVo.getTrademark().split(":")[0]);
            boolQuery.must(QueryBuilders.termQuery("tmId",tmId));
        }
        //2.4）、前端传了属性 props=4:128GB:机身存储&props=5:骁龙730:CPU型号
        String[] props = searchParamVo.getProps();
        if(props!=null && props.length > 0){
            for (String prop : props) {
                //4:128GB:机身存储 得到属性id和值
                String[] split = prop.split(":");
                Long attrId = Long.parseLong(split[0]);
                String attrValue = split[1];

                //构造boolQuery
                BoolQueryBuilder nestedBool = QueryBuilders.boolQuery();
                nestedBool.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBool.must(QueryBuilders.termQuery("attrs.attrValue",attrValue));

                NestedQueryBuilder nestedQuery =
                        QueryBuilders.nestedQuery("attrs", nestedBool, ScoreMode.None);

                //给最大的boolQuery里面放 嵌入式查询 nestedQuery
                boolQuery.must(nestedQuery);
            }
        }


        //===========检索条件结束=====================
        //0.准备一个原生检索条件【原生的dsl】
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);
        //2.5）、前端传了排序 order=2:asc
        if(!StringUtils.isEmpty(searchParamVo.getOrder())){
            String[] split = searchParamVo.getOrder().split(":");
            //分析排序用哪个字段
            String orderField = "hotScore";
            switch (split[0]){
                case "1": orderField = "hotScore";break;
                case "2": orderField = "price";break;
                case "3": orderField = "createTime";break;
                default: orderField = "hotScore";
            }
            Sort sort = Sort.by(orderField);
            if(split[1].equals("asc")) {
                sort = sort.ascending();
            }else {
                sort = sort.descending();
            }
            query.addSort(sort);
        }

        //2.6）、前端传了页码
        //页码在Spring底层是从0开始，自己要计算 前端页码-1 后的结果
        PageRequest request = PageRequest.of(searchParamVo.getPageNo()-1, SysRedisConst.SEARCH_PAGE_SIZE);
        query.setPageable(request);
        //=============排序分页结束=====================


        //2.7）、高亮
        if(!StringUtils.isEmpty(searchParamVo.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");

            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
            query.setHighlightQuery(highlightQuery);
        }
        //===========模糊查询高亮功能结束=================


        //=========聚合分析上面DSL检索到的所有商品涉及了多少种品牌和多少种平台属性
        //TODO
        //3品牌聚合 - 品牌聚合条件分析
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId").size(1000);
        //3.1品牌聚合 - 品牌名字聚合
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg").field("tmName").size(1000);
        //3.2品牌聚合 - 品牌logo聚合
        TermsAggregationBuilder tmLogoUrlAgg = AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl").size(1000);
        //名字、logo拼装进id聚合中
        tmIdAgg.subAggregation(tmNameAgg);
        tmIdAgg.subAggregation(tmLogoUrlAgg);

        query.addAggregation(tmIdAgg);

        //4.平台属性聚合
        //4.1属性的嵌入式nested
        NestedAggregationBuilder attrAgg = AggregationBuilders
                .nested("attrAgg", "attrs");
        //4.2attrId聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);
        //4.3attrName聚合
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1000);
        //4.4attrValue聚合
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(1000);
        //4.5 attrName子聚合进attrId
        attrIdAgg.subAggregation(attrNameAgg);
        //4.6 attrValue子聚合进attrId
        attrIdAgg.subAggregation(attrValueAgg);
        //4.7attrId子聚合进attr
        attrAgg.subAggregation(attrIdAgg);

        //4.8添加整个属性的聚合条件
        query.addAggregation(attrAgg);

        return query;
    }
}

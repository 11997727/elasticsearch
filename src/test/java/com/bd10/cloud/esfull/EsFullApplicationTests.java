package com.bd10.cloud.esfull;

import com.bd10.cloud.esfull.dao.ItemDao;
import com.bd10.cloud.esfull.model.Item;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsFullApplicationTests {

    //springboot集成了es，所以直接用操作对象
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ItemDao itemDao;

    //创建索引和映射
    @Test
    public void create(){
        //创建索引，会根据实体类的@Document注解信息来创建
        elasticsearchTemplate.createIndex(Item.class);
        //配置映射，根据id，field声明来自动完成映射
        elasticsearchTemplate.putMapping(Item.class);
    }

    //删除索引
    @Test
    public void delete(){
//        elasticsearchTemplate.deleteIndex("product");
        elasticsearchTemplate.deleteIndex(Item.class);
    }

    //新增文档（往库中添加数据）
    @Test
    public void save(){
        Item item=new Item();
        item.setId(1L);
        item.setTitle("好吃的xx厨师做的舒芙蕾");
        item.setCategory("蛋糕");
        item.setBrand("xx");
        item.setPrice(123.4);
        item.setImage("234");
        itemDao.save(item);
    }

    /**
     * 存集合
     */
    @Test
    public void saveList(){

        List<Item> list=new ArrayList<>();
        list.add(new Item(2L,"不好吃的xx蛋糕1","xx","蛋糕","21211",123.0));
        list.add(new Item(3L,"不好吃的xx蛋糕2","xx","蛋糕","21212",123.1));
        list.add(new Item(4L,"不好吃的xx蛋糕3","xx","蛋糕","21213",123.2));
        list.add(new Item(5L,"不好吃的xx蛋糕4","xx","蛋糕","21214",123.3));
        list.add(new Item(6L,"不好吃的xx蛋糕5","xx","蛋糕","21215",123.4));
        list.add(new Item(7L,"不好吃的xx蛋糕6","渣渣","蛋糕","21211",12.0));
        list.add(new Item(8L,"uishdfhdif","渣渣","口红","21212",123.1));
        list.add(new Item(9L,"渣渣牌卫生纸","渣渣","纸巾","21213",1230.2));
        list.add(new Item(10L,"AVB功能蛋糕","AVB","蛋糕","21214",200.3));
        list.add(new Item(11L,"阿卓纸巾","AVB","纸巾","21215",5.0));
        itemDao.saveAll(list);
    }

    /**
     * 通过id查询
     */
    @Test
    public void testFindById(){
        Optional<Item> o = itemDao.findById(2L);
        System.out.println(o.get());
    }

    /**
     * 价格降序排列
     */
    @Test
    public void testFindAll(){
        Iterable<Item> items = itemDao.findAll(Sort.by(Sort.Direction.DESC, "price"));
        items.forEach(System.out::println);
    }

    /**
     * 价格区间查询
     */
    @Test
    public void testFind(){
        List<Item> items=itemDao.findByPriceBetween(123.1,123.3);

        items.forEach(System.out::println);
    }

    /**
     * 条件查询并且关系
     */
    @Test
    public void testFind1(){
        List<Item> items=itemDao.findByBrandAndPrice("AVB",5.0);
        items.forEach(System.out::println);
    }

    /**
     * 条件查询或者关系
     */
    @Test
    public void testFind2(){
        List<Item> items=itemDao.findByBrandOrPrice("AVB",12.0);
        items.forEach(System.out::println);
    }

    /**
     * 模糊查询title包含的    中文需要分词器
     */
    @Test
    public void testFind3(){
        List<Item> items=itemDao.findByTitleLike("蛋糕");
        items.forEach(System.out::println);
    }

    //高级查询，方式不同
    @Test
    public void testQuery1(){
        //词条查询
        MatchQueryBuilder queryBuilder=QueryBuilders.matchQuery("brand","AVB");
        //执行
        Iterable<Item> items=itemDao.search(queryBuilder);
        items.forEach(System.out::println);
    }


    @Test
    public void testQuery2(){
        //加上分页效果
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //构建查询条件
        queryBuilder.withQuery(QueryBuilders.termQuery("brand","xx"));
        //初始化分页参数
        int page=0;
        int size=3;
        //设置分页参数
        queryBuilder.withPageable(PageRequest.of(page,size));
        //搜索
        Page<Item> items=itemDao.search(queryBuilder.build());
        //总条数
        System.out.println("zongtiaoshu:"+items.getTotalElements());
        //总页数
        System.out.println("yeshu:"+items.getTotalPages());
        //每页大小
        System.out.println("daxiao:"+items.getSize());
        //当前页
        System.out.println("dangqian:"+items.getNumber());
        System.out.println("-----------");
        items.forEach(System.out::println);
    }

    @Test
    public void testQuery3(){
        //查蛋糕这个分类，所有的信息，按照价格倒叙
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //构建查询条件
        queryBuilder.withQuery(QueryBuilders.termQuery("category","蛋糕"));

        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        //搜索
        Page<Item> items=itemDao.search(queryBuilder.build());
        //总条数
        System.out.println("zongtiaoshu:"+items.getTotalElements());

        System.out.println("-----------");
        items.forEach(System.out::println);
    }
    @Test
    public void testQuery4(){
        //聚合
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();

        //定义一个聚合
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        //查询
        AggregatedPage<Item> page = (AggregatedPage<Item>)itemDao.search(queryBuilder.build());
        //解析
        //
        StringTerms ag = (StringTerms)page.getAggregation("brands");
        //获取桶
        List<StringTerms.Bucket> buckets=ag.getBuckets();
        //操作
        for (StringTerms.Bucket bucket:buckets){
            System.out.println(bucket.getKeyAsString()+" : "+bucket.getDocCount());
        }
    }

    @Test
    public void testQuery5() {
        //按品牌聚合，再同时获取平均价格
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //定义一个聚合
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand").subAggregation(AggregationBuilders.avg("avgPrice").field("price")));
        //查询
        AggregatedPage<Item> page = (AggregatedPage<Item>) itemDao.search(queryBuilder.build());
        //解析
        StringTerms ag = (StringTerms) page.getAggregation("brands");
        //获取桶
        List<StringTerms.Bucket> buckets = ag.getBuckets();
        //操作
        for (StringTerms.Bucket bucket : buckets) {
            System.out.println(bucket.getKeyAsString() + " : " + bucket.getDocCount());
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("avgPrice");
            System.out.println("平均价格：" + avg.getValue());
        }
    }

}

package com.ihltx.utility.es;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.es.entity.StockBase;
import com.ihltx.utility.es.config.ElasticSearchIndexMappings;
import com.ihltx.utility.es.config.ElasticSearchIndexProperty;
import com.ihltx.utility.es.config.ElasticSearchIndexSettings;
import com.ihltx.utility.es.service.ElasticSearchUtil;
import com.ihltx.utility.util.DateUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class ElasticSearchUtilTest {
	
	private ElasticSearchUtil elasticSearchUtil;

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeEach
	public void beforeEach(){
		try{
			elasticSearchUtil = applicationContext.getBean(ElasticSearchUtil.class);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_01_createIndex() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		String indexName = "test";
		if(elasticSearchUtil.existsIndex(indexName)) {
			rs = elasticSearchUtil.deleteIndex(indexName);
			assertEquals(rs , true);
		}
		rs = elasticSearchUtil.createIndex("test");
		assertEquals(rs , true);
		
	}

	@Test
	public void test_02_createIndex() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		String indexName = "test";
		if(elasticSearchUtil.existsIndex(indexName)) {
			rs = elasticSearchUtil.deleteIndex(indexName);
			assertEquals(rs , true);
		}
		String settings = "{\"index\": {\"number_of_shards\":1,\"number_of_replicas\":1}}";
		String mappings = "{\"properties\":{\"realname\": {\"type\": \"text\",\"analyzer\": \"ik_max_word\"}}}";
		rs = elasticSearchUtil.createIndex("test",settings , mappings);
		assertEquals(rs , true);
		
	}
	
	@Test
	public void test_03_createIndex() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		String indexName = "test";
		if(elasticSearchUtil.existsIndex(indexName)) {
			rs = elasticSearchUtil.deleteIndex(indexName);
			assertEquals(rs , true);
		}
		ElasticSearchIndexSettings settings = new ElasticSearchIndexSettings();
		settings.setNumber_of_shards(1);
		settings.setNumber_of_replicas(1);
		ElasticSearchIndexMappings mappings = new ElasticSearchIndexMappings();
		List<ElasticSearchIndexProperty> fields = new ArrayList<ElasticSearchIndexProperty>();
		ElasticSearchIndexProperty field =new ElasticSearchIndexProperty();
		field.setName("realname");
		field.setType("text");
		field.setAnalyzer("ik_max_word");
		fields.add(field);
		mappings.setProperties(fields);
		
		rs = elasticSearchUtil.createIndex("test",settings , mappings);
		assertEquals(rs , true);
		
	}
	
	
	@Test
	public void test_04_createIndex() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		String indexName = elasticSearchUtil.getIndexNameByClass(StockBase.class);
		System.out.println(indexName);
		if(elasticSearchUtil.existsIndex(indexName)) {
			rs = elasticSearchUtil.deleteIndex(indexName);
			assertEquals(rs, true);
		}
		rs = elasticSearchUtil.createIndex(StockBase.class);
		assertEquals(rs , true);
		
	}
	
	
	@Test
	public void test_05_insertIndex() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		StockBase model = new StockBase();
		model.setStock_id(100022);
		model.setStock_code("601718");
		model.setStock_name("际华集团");
		model.setStock_type("sh");
		model.setStock_total_number(43.92);
		model.setStock_floating_number(43.92);
		model.setStock_trade("服装家纺");
		model.setStock_sub_trade("其他服装");
		model.setStock_order(100);
		model.setStock_created(DateUtil.getTime());
		model.setStock_updated(DateUtil.getTime());
		model.setStock_deleted(0);
		
		rs = elasticSearchUtil.insertIndex(model);
		assertEquals(rs , true);
		
		
		model = new StockBase();
		model.setStock_id(100023);
		model.setStock_code("601719");
		model.setStock_name("际华集团1");
		model.setStock_type("sh2");
		model.setStock_total_number(43.92);
		model.setStock_floating_number(43.92);
		model.setStock_trade("服装家纺3");
		model.setStock_sub_trade("其他服装4");
		model.setStock_order(105);
		model.setStock_created(DateUtil.getTime());
		model.setStock_updated(DateUtil.getTime());
		model.setStock_deleted(0);
		
		rs = elasticSearchUtil.insertIndex(model);
		assertEquals(rs , true);
		try {
			Thread.sleep(1000);
		}catch (Exception e) {

		}
	}
	

	@Test
	public void test_06_queryIndex() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		
		//1、构造termQuery对象进行字符的精确匹配查询
		//2、通过termsQuery实现指定多个值进行精确匹配查询
		//3、boolQuery查询可进行嵌套查询
		//4、rangeQuery范围的过滤
		//   在构造rangeQuery对象时，相应搜索的field不能是String类型的，需要的field是数值类型。
		//5、wildcardQuery对象进行模糊查询，类似sql中的like  *--任意多个字符   ?--一个字符，模糊搜索非常慢，最好不要使用 *或?做开头字符会更慢
		//   模糊匹配查询有两种匹配符，分别是" * " 以及 " ? "， 用" * "来匹配任何字符，包括空字符串。
		//   用" ? "来匹配任意的单个字符。当文档对象很多时，它需要遍历查询，用模糊搜索的查询速度会 很慢 。最好不要用* 或者 ？ 当做查询的开头字母，这种情况下速度会更加慢。
		//   注意全文检索的字段，不能用模糊搜索去匹配，测试时全文搜索字段用模糊查询查询不到结果
		//6、matchQuery用于文本类型字段的搜索
		//   matchQuery会将搜索条件按照标准分词器的规则分词，分完词之后分别搜索匹配项。(注意:  测试中全文检索字段如果用termQuery 或者 wildcardQuery 将不能查询成功。因为全文索引字段建立索引时已经被分词工具分成单个单词了)
		//   注意：测试中全文检索字段如果使用termQuery或wildcardQuery进行查询将不能查询成功，因为全文索引字段建立索引时，已经被分词工具分成单个词语了。
        //7、fuzzyQuery用于文本类型字段的搜索
		//   fuzzy query 是基于Levenshtein Edit Distance﻿（莱温斯坦编辑距离）基础上，对索引文档进行模糊搜索。当用户输入有错误时，使用这个功能能在一定程度上召回一些和输入相近的文档
		//   fuzzyQuery还有两个构造函数，来限制模糊匹配的程度, 在FuzzyQuery中，默认的匹配度是0.5，当这个值越小时，通过模糊查找出的文档的匹配程度就越低，查出的文档量就越多,反之亦然 
		
		
		String indexName = elasticSearchUtil.getIndexNameByClass(StockBase.class);
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		
		List<Map<String,Object>> result = elasticSearchUtil.query(indexName, sourceBuilder);

		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);

		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.wildcardQuery("stock_code", "*0172*"));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);


		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("stock_id", 100023));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);

	
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("stock_id", 100026));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);

		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0173*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);

		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100026));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);

		
		
		sourceBuilder = new SearchSourceBuilder();
		
		
		
		
		//设置高亮
        String preTags = "<b>";
        String postTags = "</b>";
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags(preTags);//设置前缀
        highlightBuilder.postTags(postTags);//设置后缀
      
        highlightBuilder.field("stock_name");//设置高亮字段
        highlightBuilder.field("stock_code");//设置高亮字段
		
		sourceBuilder.highlighter(highlightBuilder);
		
		
		
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.matchQuery("stock_name", "华集团"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);

		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.matchQuery("stock_name", "际华"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		
		//stock_name已经分词，使用模糊查询将无法查找到相应数据
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*际华*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*集团*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("stock_name", "际1华"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		

		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "际1华"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);
		
		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "际1华"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		

		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", "100023"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "际1华"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		


		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", "100026"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "际1华"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		
		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*际华*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		

		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*集团*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);		
		
		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name.keyword", "*际华*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		

	}

	
	
	@Test
	public void test_07_querySearchSourceBuilder() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		String indexName = "lib5";//elasticSearchUtil.getIndexNameByClass(StockBase.class);	
		
		if(elasticSearchUtil.existsIndex(indexName)) {
			rs = elasticSearchUtil.deleteIndex(indexName);
			assertEquals(rs , true);
		}
		
		ElasticSearchIndexSettings settings=new ElasticSearchIndexSettings();
		settings.setNumber_of_replicas(1);
		settings.setNumber_of_shards(1);
		
		ElasticSearchIndexMappings mappings = new ElasticSearchIndexMappings();
		List<ElasticSearchIndexProperty> properties =new ArrayList<ElasticSearchIndexProperty>();
		
		ElasticSearchIndexProperty property= new ElasticSearchIndexProperty();
		property.setName("id");
		property.setType("long");
		property.setAnalyzer("");
		properties.add(property);

		
		property= new ElasticSearchIndexProperty();
		property.setName("price");
		property.setType("double");
		property.setAnalyzer("");
		properties.add(property);
		
		property= new ElasticSearchIndexProperty();
		property.setName("itemID");
		property.setType("text");
		properties.add(property);
		
		mappings.setProperties(properties);
		
		rs = elasticSearchUtil.createIndex(indexName, settings, mappings);
		assertEquals(rs, true);		
		
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("price", 40);
		data.put("itemID", "ID100123");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("price", 50);
		data.put("itemID", "ID100124");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 3);
		data.put("price", 25);
		data.put("itemID", "ID100125");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 4);
		data.put("price", 30);
		data.put("itemID", "ID100126");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 5);
		data.put("price", null);
		data.put("itemID", "ID100127");
		datas.add(data);
		
		rs = elasticSearchUtil.insertBulk(indexName, datas);
		assertEquals(rs, true);	
		
		
		
		datas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("price", 405);
		data.put("itemID", "ID1001235");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("price", 505);
		data.put("itemID", "ID1001245");
		datas.add(data);
		
		rs = elasticSearchUtil.updateBulk(indexName, datas);
		assertEquals(rs, true);
		
		datas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("price", 40);
		data.put("itemID", "ID100123");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("price", 50);
		data.put("itemID", "ID100124");
		datas.add(data);
		
		rs = elasticSearchUtil.updateBulk(indexName, datas);
		assertEquals(rs, true);
		
		datas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 1);
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		datas.add(data);
		
		rs = elasticSearchUtil.deleteBulk(indexName, datas);
		assertEquals(rs, true);
		
		
		datas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("price", 40);
		data.put("itemID", "ID100123");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("price", 50);
		data.put("itemID", "ID100124");
		datas.add(data);
		
		rs = elasticSearchUtil.insertBulk(indexName, datas);
		assertEquals(rs, true);
		
		
		
		List<Map<String,Object>> insertDatas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 11);
		data.put("price", 40);
		data.put("itemID", "ID1001211");
		insertDatas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 12);
		data.put("price", 50);
		data.put("itemID", "ID1001212");
		insertDatas.add(data);
		
		List<Map<String,Object>> updateDatas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("price", 405);
		data.put("itemID", "ID1001235");
		updateDatas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("price", 505);
		data.put("itemID", "ID1001245");
		updateDatas.add(data);
		
		List<Map<String,Object>> deleteDatas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 3);
		deleteDatas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 4);
		deleteDatas.add(data);
		
		
		rs = elasticSearchUtil.bulk(indexName, insertDatas,updateDatas,deleteDatas);
		assertEquals(rs, true);
		
		
		insertDatas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 3);
		data.put("price", 25);
		data.put("itemID", "ID100125");
		insertDatas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 4);
		data.put("price", 30);
		data.put("itemID", "ID100126");
		insertDatas.add(data);
		
		
		updateDatas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("price", 40);
		data.put("itemID", "ID100123");
		updateDatas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("price", 50);
		data.put("itemID", "ID100124");
		updateDatas.add(data);
		
		
		deleteDatas = new ArrayList<Map<String,Object>>();
		data = new HashMap<String, Object>();
		data.put("id", 11);
		deleteDatas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 12);
		deleteDatas.add(data);
		
		
		rs = elasticSearchUtil.bulk(indexName, insertDatas,updateDatas,deleteDatas);
		assertEquals(rs, true);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String id= "1";
		data = elasticSearchUtil.getById(indexName, id);
		System.out.println(data);
		assertEquals(data!=null && data.get("id").toString().equals(id), true);
		
		List<Map<String,Object>> result = null;
		
		result = elasticSearchUtil.getByIds(indexName, "1","2");
		System.out.println(result);
		assertEquals(result!=null &&  result.size()==2, true);
		
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);		

		
		sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.termQuery("price", 40));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);		

		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.termsQuery("price", new int[]{25,40}));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.termsQuery("price", new int[]{25,40}));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.termQuery("itemID", "ID100123"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result==null || result.size()<=0, true);	
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.termQuery("itemID", "id100123"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should(QueryBuilders.termQuery("price", 25));
		boolQueryBuilder.should(QueryBuilders.termQuery("itemID", "id100123"));
		boolQueryBuilder.mustNot(QueryBuilders.termQuery("price", 30));
		
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should(QueryBuilders.termQuery("itemID", "id100123"));
		BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
		subBoolQueryBuilder.must(QueryBuilders.termQuery("itemID", "id100124"));
		subBoolQueryBuilder.must(QueryBuilders.termQuery("price", 40));
		boolQueryBuilder.should(subBoolQueryBuilder);
		
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(25).lt(50));
		boolQueryBuilder.should(subBoolQueryBuilder);
		
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.existsQuery("price"));
		boolQueryBuilder.should(subBoolQueryBuilder);
		
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.existsQuery("price"));
		boolQueryBuilder.should(subBoolQueryBuilder);
		
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
	}
	
	
	
	@Test
	public void test_08_querySearchSourceBuilder() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		String indexName = "lib3";//elasticSearchUtil.getIndexNameByClass(StockBase.class);
		
		if(elasticSearchUtil.existsIndex(indexName)) {
			rs = elasticSearchUtil.deleteIndex(indexName);
			assertEquals(rs , true);
		}
		
		ElasticSearchIndexSettings settings=new ElasticSearchIndexSettings();
		settings.setNumber_of_replicas(1);
		settings.setNumber_of_shards(1);
		
		ElasticSearchIndexMappings mappings = new ElasticSearchIndexMappings();
		List<ElasticSearchIndexProperty> properties =new ArrayList<ElasticSearchIndexProperty>();
		
		ElasticSearchIndexProperty property= new ElasticSearchIndexProperty();
		property.setName("id");
		property.setType("long");
		properties.add(property);

		
		property= new ElasticSearchIndexProperty();
		property.setName("name");
		property.setType("text");
		properties.add(property);
		
		property= new ElasticSearchIndexProperty();
		property.setName("address");
		property.setType("text");
		properties.add(property);
		

		property= new ElasticSearchIndexProperty();
		property.setName("age");
		property.setType("integer");
		properties.add(property);
		
		property= new ElasticSearchIndexProperty();
		property.setName("interests");
		property.setType("text");
		properties.add(property);
		
		property= new ElasticSearchIndexProperty();
		property.setName("birthday");
		property.setType("date");
		properties.add(property);

		mappings.setProperties(properties);
		
		rs = elasticSearchUtil.createIndex(indexName, settings, mappings);
		assertEquals(rs, true);		
		
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("name", "zhaoliu");
		data.put("address", "cheng du");
		data.put("age", 50);
		data.put("interests", "football,basketball");
		data.put("birthday", "1997-04-11");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("name", "zhaoming");
		data.put("address", "bei jing");
		data.put("age", 52);
		data.put("interests", "football,dance");
		data.put("birthday", "1999-06-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 3);
		data.put("name", "lisi");
		data.put("address", "shen zhen");
		data.put("age", 51);
		data.put("interests", "basketball,dance");
		data.put("birthday", "1989-07-22");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 4);
		data.put("name", "wangwu");
		data.put("address", "cheng qin");
		data.put("age", 51);
		data.put("interests", "football,basketball,dance");
		data.put("birthday", "1987-03-14");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 5);
		data.put("name", "zhangsan");
		data.put("address", "cheng qin");
		data.put("age", 51);
		data.put("interests", "football,basketball,dance");
		data.put("birthday", "1981-02-14");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 6);
		data.put("name", "dance");
		data.put("address", "cheng qin");
		data.put("age", 52);
		data.put("interests", "football,basketball");
		data.put("birthday", "1981-02-14");
		datas.add(data);
		
		rs = elasticSearchUtil.insertBulk(indexName, datas);
		assertEquals(rs, true);	
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		List<Map<String,Object>> result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);		

		
		BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder.must(QueryBuilders.matchQuery("interests", "dance"));
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("interests", "basketball"));
		boolQueryBuilder.should(QueryBuilders.matchQuery("address", "bei jing"));
		boolQueryBuilder.should(QueryBuilders.rangeQuery("birthday").gte("1996-01-01"));
		
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);		

		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		 
		boolQueryBuilder.must(QueryBuilders.matchQuery("interests", "dance"));
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("interests", "basketball"));
		boolQueryBuilder.should(QueryBuilders.matchQuery("address", "bei jing"));
		
		subBoolQueryBuilder.must(QueryBuilders.rangeQuery("birthday").gte("1999-01-01"));
		subBoolQueryBuilder.must(QueryBuilders.rangeQuery("age").lte(52));
		subBoolQueryBuilder.mustNot(QueryBuilders.termQuery("age",29));
		
		
		boolQueryBuilder.filter(subBoolQueryBuilder);
		
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);		

		
		
		sourceBuilder = new SearchSourceBuilder();
		ConstantScoreQueryBuilder constantScoreQueryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("interests", "dance"));
		
		sourceBuilder.query(constantScoreQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);		

		
	}
	
	
	
	@Test
	public void test_09_querySearchSourceBuilder() {
		if(elasticSearchUtil==null)
			{assertEquals(false , true);return;}
		Boolean rs = false;
		String indexName = "lib4";//elasticSearchUtil.getIndexNameByClass(StockBase.class);		

		if(elasticSearchUtil.existsIndex(indexName)) {
			rs = elasticSearchUtil.deleteIndex(indexName);
			assertEquals(rs , true);
		}
		
		ElasticSearchIndexSettings settings=new ElasticSearchIndexSettings();
		settings.setNumber_of_shards(3);
		settings.setNumber_of_replicas(0);
		
		ElasticSearchIndexMappings mappings = new ElasticSearchIndexMappings();
		List<ElasticSearchIndexProperty> properties =new ArrayList<ElasticSearchIndexProperty>();
		
		ElasticSearchIndexProperty property= new ElasticSearchIndexProperty();
		property.setName("id");
		property.setType("long");
		properties.add(property);

		
		property= new ElasticSearchIndexProperty();
		property.setName("name");
		property.setType("text");
		property.setAnalyzer("ik_max_word");
		properties.add(property);
		
		property= new ElasticSearchIndexProperty();
		property.setName("address");
		property.setType("text");
		property.setAnalyzer("ik_max_word");
		properties.add(property);
		

		property= new ElasticSearchIndexProperty();
		property.setName("age");
		property.setType("integer");
		properties.add(property);
		
		property= new ElasticSearchIndexProperty();
		property.setName("interests");
		property.setType("text");
		property.setAnalyzer("ik_max_word");
		properties.add(property);
		
		property= new ElasticSearchIndexProperty();
		property.setName("birthday");
		property.setType("date");
		properties.add(property);

		mappings.setProperties(properties);
		
		rs = elasticSearchUtil.createIndex(indexName, settings, mappings);
		assertEquals(rs, true);		
		
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("id", 1);
		data.put("name", "赵六");
		data.put("address", "黑龙江省铁岭");
		data.put("age", 50);
		data.put("interests", "喜欢喝酒，锻炼，说相声");
		data.put("birthday", "1970-12-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("name", "赵明");
		data.put("address", "北京海淀区清河");
		data.put("age", 20);
		data.put("interests", "喜欢喝酒，锻炼，唱歌");
		data.put("birthday", "1998-10-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 3);
		data.put("name", "lisi");
		data.put("address", "北京海淀区清河");
		data.put("age", 23);
		data.put("interests", "喜欢喝酒，锻炼，唱歌");
		data.put("birthday", "1998-10-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 4);
		data.put("name", "王五");
		data.put("address", "北京海淀区清河");
		data.put("age", 26);
		data.put("interests", "喜欢编程，听音乐，旅游");
		data.put("birthday", "1995-10-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 5);
		data.put("name", "张三");
		data.put("address", "北京海淀区清河");
		data.put("age", 29);
		data.put("interests", "喜欢摄影，听音乐，跳舞");
		data.put("birthday", "1988-10-12");
		datas.add(data);
		
		
		rs = elasticSearchUtil.insertBulk(indexName, datas);
		assertEquals(rs, true);	
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		List<Map<String,Object>> result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);		

		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query( QueryBuilders.termQuery("name", "赵"));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	

		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query( QueryBuilders.termsQuery("interests", new String[] {"喝酒","唱歌"}));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query( QueryBuilders.termsQuery("interests", new String[] {"喝酒","唱歌"}));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query( QueryBuilders.termsQuery("interests", new String[] {"喝酒","唱歌"}));
		sourceBuilder.from(2);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);


		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.query(QueryBuilders.termsQuery("interests", new String[] {"喝酒","唱歌"}));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);

		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.query(QueryBuilders.matchQuery("name", "赵六"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.query(QueryBuilders.matchQuery("age", 20));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.query(QueryBuilders.multiMatchQuery("唱歌", new String[] {"name","interests"}));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(new String[] {"name" , "address"}, null);
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","锻炼，唱歌"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource("name", null);
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","锻炼，唱歌"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(null, new String[] {"age"});
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","锻炼，唱歌"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(null, "age");
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","锻炼,唱歌"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(new String[] {"name" , "age"}, null);
		sourceBuilder.query(QueryBuilders.matchAllQuery());
		sourceBuilder.sort("age",SortOrder.DESC);
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		

	
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(new String[] {"name" , "age"}, null);
		sourceBuilder.query(QueryBuilders.matchAllQuery());
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.ASC));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		////???????????????????????????
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("name", "赵"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.ASC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);

		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.rangeQuery("birthday").from("1990-10-10").to("2018-05-01"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.rangeQuery("age").from(20).to(25).includeLower(true).includeUpper(true));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.wildcardQuery("name", "赵*"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.wildcardQuery("name", "li?i"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.fuzzyQuery("name", "赵"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.fuzzyQuery("interests", "喝酒"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
	}
	
}

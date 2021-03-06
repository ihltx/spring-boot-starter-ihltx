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
		model.setStock_name("????????????");
		model.setStock_type("sh");
		model.setStock_total_number(43.92);
		model.setStock_floating_number(43.92);
		model.setStock_trade("????????????");
		model.setStock_sub_trade("????????????");
		model.setStock_order(100);
		model.setStock_created(DateUtil.getTime());
		model.setStock_updated(DateUtil.getTime());
		model.setStock_deleted(0);
		
		rs = elasticSearchUtil.insertIndex(model);
		assertEquals(rs , true);
		
		
		model = new StockBase();
		model.setStock_id(100023);
		model.setStock_code("601719");
		model.setStock_name("????????????1");
		model.setStock_type("sh2");
		model.setStock_total_number(43.92);
		model.setStock_floating_number(43.92);
		model.setStock_trade("????????????3");
		model.setStock_sub_trade("????????????4");
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
		
		//1?????????termQuery???????????????????????????????????????
		//2?????????termsQuery?????????????????????????????????????????????
		//3???boolQuery???????????????????????????
		//4???rangeQuery???????????????
		//   ?????????rangeQuery???????????????????????????field?????????String?????????????????????field??????????????????
		//5???wildcardQuery?????????????????????????????????sql??????like  *--??????????????????   ?--????????????????????????????????????????????????????????? *????????????????????????????
		//   ????????????????????????????????????????????????" * " ?????? " ? "??? ???" * "?????????????????????????????????????????????
		//   ???" ? "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ?????? ??????????????????* ?????? ??? ??????????????????????????????????????????????????????????????????
		//   ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		//6???matchQuery?????????????????????????????????
		//   matchQuery????????????????????????????????????????????????????????????????????????????????????????????????(??????:  ????????????????????????????????????termQuery ?????? wildcardQuery ?????????????????????????????????????????????????????????????????????????????????????????????????????????)
		//   ????????????????????????????????????????????????termQuery???wildcardQuery???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //7???fuzzyQuery?????????????????????????????????
		//   fuzzy query ?????????Levenshtein Edit Distance??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		//   fuzzyQuery?????????????????????????????????????????????????????????, ???FuzzyQuery???????????????????????????0.5???????????????????????????????????????????????????????????????????????????????????????????????????????????????,???????????? 
		
		
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
		
		
		
		
		//????????????
        String preTags = "<b>";
        String postTags = "</b>";
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags(preTags);//????????????
        highlightBuilder.postTags(postTags);//????????????
      
        highlightBuilder.field("stock_name");//??????????????????
        highlightBuilder.field("stock_code");//??????????????????
		
		sourceBuilder.highlighter(highlightBuilder);
		
		
		
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.matchQuery("stock_name", "?????????"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);

		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.matchQuery("stock_name", "??????"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		
		//stock_name???????????????????????????????????????????????????????????????
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*??????*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*??????*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("stock_name", "???1???"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);
		

		sourceBuilder = new SearchSourceBuilder();
		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "???1???"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);
		
		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "???1???"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		

		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", "100023"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "???1???"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		


		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", "100026"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_name", "???1???"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		
		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*??????*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result==null || result.size()<=0, true);		
		

		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name", "*??????*"));
		sourceBuilder.query(boolQueryBuilder);
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("=======================");
		assertEquals(result!=null && result.size()>0, true);		
		
		
		
		sourceBuilder = new SearchSourceBuilder();

		boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_code", "*0171*"));
		boolQueryBuilder.must(QueryBuilders.termQuery("stock_id", 100023));
		boolQueryBuilder.must(QueryBuilders.wildcardQuery("stock_name.keyword", "*??????*"));
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
		data.put("name", "??????");
		data.put("address", "??????????????????");
		data.put("age", 50);
		data.put("interests", "?????????????????????????????????");
		data.put("birthday", "1970-12-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 2);
		data.put("name", "??????");
		data.put("address", "?????????????????????");
		data.put("age", 20);
		data.put("interests", "??????????????????????????????");
		data.put("birthday", "1998-10-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 3);
		data.put("name", "lisi");
		data.put("address", "?????????????????????");
		data.put("age", 23);
		data.put("interests", "??????????????????????????????");
		data.put("birthday", "1998-10-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 4);
		data.put("name", "??????");
		data.put("address", "?????????????????????");
		data.put("age", 26);
		data.put("interests", "?????????????????????????????????");
		data.put("birthday", "1995-10-12");
		datas.add(data);
		
		data = new HashMap<String, Object>();
		data.put("id", 5);
		data.put("name", "??????");
		data.put("address", "?????????????????????");
		data.put("age", 29);
		data.put("interests", "?????????????????????????????????");
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
		sourceBuilder.query( QueryBuilders.termQuery("name", "???"));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	

		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query( QueryBuilders.termsQuery("interests", new String[] {"??????","??????"}));
		
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query( QueryBuilders.termsQuery("interests", new String[] {"??????","??????"}));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);	
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query( QueryBuilders.termsQuery("interests", new String[] {"??????","??????"}));
		sourceBuilder.from(2);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);


		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.query(QueryBuilders.termsQuery("interests", new String[] {"??????","??????"}));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);

		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.query(QueryBuilders.matchQuery("name", "??????"));
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
		sourceBuilder.query(QueryBuilders.multiMatchQuery("??????", new String[] {"name","interests"}));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(new String[] {"name" , "address"}, null);
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","???????????????"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource("name", null);
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","???????????????"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(null, new String[] {"age"});
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","???????????????"));
		sourceBuilder.from(0);
		sourceBuilder.size(2);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.version(true);
		sourceBuilder.fetchSource(null, "age");
		sourceBuilder.query(QueryBuilders.matchPhraseQuery("interests","??????,??????"));
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
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("name", "???"));
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
		sourceBuilder.query(QueryBuilders.wildcardQuery("name", "???*"));
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
		sourceBuilder.query(QueryBuilders.fuzzyQuery("name", "???"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
		
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.fuzzyQuery("interests", "??????"));
		sourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		sourceBuilder.from(0);
		sourceBuilder.size(20);
		result = elasticSearchUtil.query(indexName, sourceBuilder);
		System.out.println(result);
		System.out.println("===============================");
		assertEquals(result!=null && result.size()>0, true);
		
	}
	
}

package com.ihltx.utility.es.service.impl;

import com.ihltx.utility.es.annotations.ElasticSearchIndexAnnotation;
import com.ihltx.utility.es.annotations.ElasticSearchIndexFieldAnnotation;
import com.ihltx.utility.es.config.ElasticSearchConfig;
import com.ihltx.utility.es.config.ElasticSearchIndexMappings;
import com.ihltx.utility.es.config.ElasticSearchIndexProperty;
import com.ihltx.utility.es.config.ElasticSearchIndexSettings;
import com.ihltx.utility.es.service.ElasticSearchUtil;
import com.ihltx.utility.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Strings;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ElasticSearchUtilImpl implements ElasticSearchUtil {

	private ElasticSearchConfig elasticSearchConfig;
	public ElasticSearchConfig getElasticSearchConfig() {
		return elasticSearchConfig;
	}

	public void setElasticSearchConfig(ElasticSearchConfig elasticSearchConfig) {
		this.elasticSearchConfig = elasticSearchConfig;
	}


	private RestHighLevelClient restHighLevelClient;

	public RestHighLevelClient getRestHighLevelClient() {
		return restHighLevelClient;
	}

	public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}

	/**
	 * ?????? this.restHighLevelClient ????????????
	 * 
	 * @return true--?????? false--??????
	 */
	private Boolean checkRestHighLevelClient() {
		return this.restHighLevelClient != null;
	}

	/**
	 * ??????????????????????????????????????????1????????????1
	 * 
	 * @param indexName ????????????
	 * @return true--?????? false--??????
	 */
	public Boolean createIndex(String indexName) {
		if (!checkRestHighLevelClient())
			return false;
		try {
			CreateIndexRequest request = new CreateIndexRequest(indexName);
			// CreateIndexResponse createIndexResponse
			// =this.restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
			CreateIndexResponse createIndexResponse = this.restHighLevelClient.indices().create(request,
					RequestOptions.DEFAULT);

			return createIndexResponse.isAcknowledged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ????????????????????????settings??????mappings
	 * 
	 * 
	 * @param indexName ????????????
	 * @param settings  ????????????json { "index": {
	 *                  "number_of_shards":1,"number_of_replicas":1} }
	 * @param mappings  ????????????json { "properties": { "?????????": { "type":
	 *                  "byte|short|integer|long|float|double|boolean|binary|text|date|keyword",
	 *                  --text???????????????????????? "store": "true|false",
	 *                  --???????????????????????????false??????????????????????????????????????????,true????????????????????????????????????false
	 *                  "index": "true|false", --???????????? true--??? false--??? "analyzer":
	 *                  "ik", --???????????????????????????standard
	 *                  analyzer(???????????????????????????????????????????????????),ik_max_word--?????????????????? } } }
	 * @return true--?????? false--??????
	 */

	public Boolean createIndex(String indexName, String settings, String mappings) {
		if (!checkRestHighLevelClient())
			return false;
		try {
			CreateIndexRequest request = new CreateIndexRequest(indexName);
			request.settings(settings, XContentType.JSON);
			request.mapping(mappings, XContentType.JSON);
			CreateIndexResponse createIndexResponse = this.restHighLevelClient.indices().create(request,
					RequestOptions.DEFAULT);
			return createIndexResponse.isAcknowledged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ????????????????????????settings??????mappings
	 * 
	 * 
	 * @param indexName ????????????
	 * @param settings  ????????????
	 * @param mappings  ????????????
	 * @return true--?????? false--??????
	 */

	public Boolean createIndex(String indexName, ElasticSearchIndexSettings settings,
			ElasticSearchIndexMappings mappings) {
		if (!checkRestHighLevelClient())
			return false;
		try {
			CreateIndexRequest request = new CreateIndexRequest(indexName);
			request.settings(JSON.toJSONString(settings, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames), XContentType.JSON);
			request.mapping(elasticSearchIndexMappings2JsonString(mappings), XContentType.JSON);
			CreateIndexResponse createIndexResponse = this.restHighLevelClient.indices().create(request,
					RequestOptions.DEFAULT);
			return createIndexResponse.isAcknowledged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ???????????????????????????????????????????????????????????????ElasticSearchIndexAnnotation????????????settings????????????????????????????????????ElasticSearchIndexFieldAnnotation??????????????????
	 * 
	 * 
	 * @param clazz ????????????
	 * @return true--?????? false--??????
	 */

	public Boolean createIndex(Class<?> clazz) {

		if (!checkRestHighLevelClient())
			return false;
		try {
			String indexName = getIndexNameByClass(clazz);
			CreateIndexRequest request = new CreateIndexRequest(indexName);
			String settings = getSettingsByClass(clazz);
			request.settings(settings, XContentType.JSON);
			String mappings = getMappingsByClass(clazz);
			if (!Strings.isNullOrEmpty(mappings)) {
				request.mapping(mappings, XContentType.JSON);
			}
			CreateIndexResponse createIndexResponse = this.restHighLevelClient.indices().create(request,
					RequestOptions.DEFAULT);
			return createIndexResponse.isAcknowledged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * ??? ElasticSearchIndexMappings ?????????????????? json String
	 * 
	 * @param mappings ????????????????????????
	 * @return String
	 */
	private String elasticSearchIndexMappings2JsonString(ElasticSearchIndexMappings mappings) {
		List<String> noIndexTypes = Arrays.asList("byte","short","integer","long","float","double","boolean","binary","date","keyword");
		if (mappings == null)
			return "";
		Map<String, Map<String, Object>> properties = new HashMap<String, Map<String, Object>>();
		for (ElasticSearchIndexProperty property : mappings.getProperties()) {
			Map<String, Object> fields = new HashMap<String, Object>();
			fields.put("type", property.getType());
			fields.put("store", property.getStore());
			Boolean index = property.getIndex();
			String analyzer = property.getAnalyzer();
			if(noIndexTypes.contains(property.getType())) {
				index = false;
				analyzer ="";
			}
			if(index) {
				fields.put("index", index);	
			}
			
			
			if(!Strings.isNullOrEmpty(analyzer)) {
				fields.put("analyzer", analyzer);
			}			
			
			properties.put(property.getName(), fields);
		}
		Map<String, Map<String, Map<String, Object>>> map = new HashMap<String, Map<String, Map<String, Object>>>();
		map.put("properties", properties);
		return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames);
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param clazz ????????????
	 * @return String ????????????
	 */
	public String getIndexNameByClass(Class<?> clazz) {
		ElasticSearchIndexAnnotation elasticSearchIndexAnnotation = clazz
				.getAnnotation(ElasticSearchIndexAnnotation.class);
		if (elasticSearchIndexAnnotation == null)
			return clazz.getName().toLowerCase();
		String name = elasticSearchIndexAnnotation.name();
		if (Strings.isNullOrEmpty(name)) {
			return clazz.getName().toLowerCase();
		}
		return name;
	}

	private String getSettingsByClass(Class<?> clazz) {
		ElasticSearchIndexAnnotation elasticSearchIndexAnnotation = clazz
				.getAnnotation(ElasticSearchIndexAnnotation.class);
		Map<String, Object> settings = new HashMap<String, Object>();
		if (elasticSearchIndexAnnotation == null) {
			settings.put("number_of_shards", 1);
			settings.put("number_of_replicas", 1);
		} else {
			settings.put("number_of_shards", elasticSearchIndexAnnotation.number_of_shards());
			settings.put("number_of_replicas", elasticSearchIndexAnnotation.number_of_replicas());
		}
		return JSON.toJSONString(settings, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames);
	}

	private String getMappingsByClass(Class<?> clazz) {
		Map<String, Map<String, Object>> properties = new HashMap<String, Map<String, Object>>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			ElasticSearchIndexFieldAnnotation elasticSearchIndexFieldAnnotation = field
					.getAnnotation(ElasticSearchIndexFieldAnnotation.class);
			if (elasticSearchIndexFieldAnnotation != null) {
				Map<String, Object> f = new HashMap<String, Object>();
				f.put("type", elasticSearchIndexFieldAnnotation.type());
				f.put("store", elasticSearchIndexFieldAnnotation.store());
				f.put("index", elasticSearchIndexFieldAnnotation.index());
				if (!Strings.isNullOrEmpty(elasticSearchIndexFieldAnnotation.analyzer())) {
					f.put("analyzer", elasticSearchIndexFieldAnnotation.analyzer());
				}
				if (Strings.isNullOrEmpty(elasticSearchIndexFieldAnnotation.name())) {
					properties.put(field.getName(), f);
				} else {
					properties.put(elasticSearchIndexFieldAnnotation.name(), f);
				}

			}
		}
		Map<String, Map<String, Map<String, Object>>> map = new HashMap<String, Map<String, Map<String, Object>>>();
		map.put("properties", properties);
		return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames);
	}

	/**
	 * ????????????????????????
	 * 
	 * @param indexName ????????????
	 * @return true--?????? false--??????
	 */
	public Boolean existsIndex(String indexName) {
		if (!checkRestHighLevelClient())
			return false;
		try {
			GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
			return this.restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ????????????
	 * 
	 * @param indexName ????????????
	 * @return true--?????? false--??????
	 */
	public Boolean deleteIndex(String indexName) {
		if (!checkRestHighLevelClient())
			return false;
		try {

			DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
			AcknowledgedResponse delete = this.restHighLevelClient.indices().delete(deleteIndexRequest,
					RequestOptions.DEFAULT);
			return delete.isAcknowledged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * ???????????????????????????????????????????????????
	 * 
	 * @param indexName ????????????
	 * @param model ????????????
	 * @return true--?????? false--??????
	 */
	public Boolean insertIndex(String indexName,Object model) {
		if (!checkRestHighLevelClient())
			return false;
		try {
			IndexRequest request = new IndexRequest("post");
			request.index(indexName).source(JSON.toJSONString(model, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames), XContentType.JSON);
			IndexResponse response = this.restHighLevelClient.index(request, RequestOptions.DEFAULT);
			return response.getResult() == Result.CREATED;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ?????? ????????????????????????
	 * 
	 * @param model ????????????
	 * @return true--?????? false--??????
	 */
	public Boolean insertIndex(Object model) {
		if (!checkRestHighLevelClient())
			return false;
		try {
			String indexName = getIndexNameByClass(model.getClass());
			IndexRequest request = new IndexRequest("post");
			request.index(indexName).source(JSON.toJSONString(model, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames), XContentType.JSON);
			IndexResponse response = this.restHighLevelClient.index(request, RequestOptions.DEFAULT);
			return response.getResult() == Result.CREATED;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * ?????????????????????json????????????????????????
	 * 
	 * @param indexName ????????????
	 * @param json ?????????????????? json
	 * @return true--?????? false--??????
	 */
	public Boolean insertIndex(String indexName,String json) {
		if (!checkRestHighLevelClient())
			return false;
		try {
			IndexRequest request = new IndexRequest("post");
			request.index(indexName).source(json, XContentType.JSON);
			IndexResponse response = this.restHighLevelClient.index(request, RequestOptions.DEFAULT);
			return response.getResult() == Result.CREATED;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @param index ????????????
	 * @param where  ??????  Map<String, Object>
	 *   Object Map<String, Object>
	 * 
	 * @return List<Map<String,Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> query(String index, Map<String, Object> where) {
		List<String> numericDataTypes = Arrays.asList("java.lang.Byte" , "java.lang.Short" , "java.lang.Integer", "java.lang.Long", "java.lang.Float" , "java.lang.Double");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (!checkRestHighLevelClient())
			return null;
		try {
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			SearchRequest rq = new SearchRequest();

			// ??????
			if (where != null && !where.isEmpty()) {
				BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
				where.forEach((k, v) -> {
					if (v instanceof Map) {
						// ????????????map ????????????
						Map<String, Date> mapV = (Map<String, Date>) v;
						if (mapV != null) {
							boolQueryBuilder.must(QueryBuilders.rangeQuery(k).gte(format.format(mapV.get("start")))
									.lt(format.format(mapV.get("end"))));
						}
					}else if(numericDataTypes.contains(v.getClass().getTypeName())) {
						boolQueryBuilder.must(QueryBuilders.termQuery(k, v));
					} else {
						// ??????????????????
						if(StringUtil.endsWith(k, "*")) {
							boolQueryBuilder.must(QueryBuilders.wildcardQuery(StringUtil.rtrim(k, "*"), "*"+v.toString()+"*"));	
						}else {
							boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(k , v));
						}						
					}
				});
				sourceBuilder.query(boolQueryBuilder);
			}

			
			 //??????
			sourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));
			
			// ??????
			rq.indices(index);

			rq.source(sourceBuilder);
			SearchResponse searchResponse =this.restHighLevelClient.search(rq, RequestOptions.DEFAULT);
			if(searchResponse.status()==RestStatus.OK && (searchResponse.getHits().getTotalHits().value>0)) {
				return Arrays.stream(searchResponse.getHits().getHits()).map(b->{return b.getSourceAsMap();}).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ????????????????????????sourceBuilder????????????
	 * 
	 * @param index ????????????
	 * @param sourceBuilder  sourceBuilder
	 * 
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String,Object>> query(String index, SearchSourceBuilder sourceBuilder) {
		List<Map<String,Object>> rs = new ArrayList<Map<String,Object>>();
		if (!checkRestHighLevelClient())
			return null;
		try {
			SearchRequest rq = new SearchRequest();
			 //??????
			sourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));
			// ??????
			rq.indices(index);
			rq.source(sourceBuilder);
			SearchResponse searchResponse =this.restHighLevelClient.search(rq, RequestOptions.DEFAULT);
			for (SearchHit hit : searchResponse.getHits()) {
				Map<String,Object> row = hit.getSourceAsMap();
				if(sourceBuilder.version()!=null &&  sourceBuilder.version().booleanValue()) {
					row.put("score", hit.getScore());
					row.put("id", hit.getId());
					row.put("version", hit.getVersion());
				}
				for(String k : row.keySet()) {
					if(hit.getHighlightFields()!=null && hit.getHighlightFields().containsKey(k) && hit.getHighlightFields().get(k)!=null) {
						row.put(k, hit.getHighlightFields().get(k).fragments()[0]);
					}
				}
				rs.add(row);
			}
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * ??????????????????
	 * @param indexName  ????????????
	 * @param datas  ???????????? List<T> ????????????List<Map<String,Object>>
	 * @return  true--??????  false--??????
	 */
	@SuppressWarnings("unchecked")
	public <T> Boolean insertBulk(String indexName , List<T> datas) {
		if (!checkRestHighLevelClient())
			return false;
		BulkRequest bulkRequest= Requests.bulkRequest();
		try {
			for(T object : datas) {
				IndexRequest indexRequest =new IndexRequest(indexName);
				if(object instanceof Map) {
					Map<String,Object> m = (Map<String,Object>)object;
					if(m.containsKey("id") && m.get("id")!=null) {
						indexRequest.id(m.get("id").toString());
					}					
				}else {
					if(object.getClass().getDeclaredField("id")!=null && object.getClass().getField("id").get(object)!=null) {
						indexRequest.id(object.getClass().getField("id").get(object).toString());
					}
				}
				indexRequest.source(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames), XContentType.JSON);		
				bulkRequest.add(indexRequest);
			}
			BulkResponse bulkResponse = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
			return bulkResponse.hasFailures()==false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 * ??????????????????
	 * @param indexName  ????????????
	 * @param datas  ???????????? List<T> ????????????List<Map<String,Object>>
	 * @return  true--??????  false--??????
	 */
	@SuppressWarnings("unchecked")
	public <T> Boolean updateBulk(String indexName , List<T> datas) {
		if (!checkRestHighLevelClient())
			return false;
		BulkRequest bulkRequest= Requests.bulkRequest();
		try {
			for(T object : datas) {
				UpdateRequest updateRequest  =new UpdateRequest();
				updateRequest.index(indexName);
				if(object instanceof Map) {
					Map<String,Object> m = (Map<String,Object>)object;
					if(m.containsKey("id") && m.get("id")!=null) {
						updateRequest.id(m.get("id").toString());
					}					
				}else {
					if(object.getClass().getDeclaredField("id")!=null && object.getClass().getField("id").get(object)!=null) {
						updateRequest.id(object.getClass().getField("id").get(object).toString());
					}
				}
				updateRequest.doc(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames), XContentType.JSON);		
				bulkRequest.add(updateRequest);
			}
			BulkResponse bulkResponse = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
			return bulkResponse.hasFailures()==false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 * ??????????????????
	 * @param indexName  ????????????
	 * @param datas  ???????????? List<T> ????????????List<Map<String,Object>>
	 * @return  true--??????  false--??????
	 */
	@SuppressWarnings("unchecked")
	public <T> Boolean deleteBulk(String indexName , List<T> datas) {
		if (!checkRestHighLevelClient())
			return false;
		BulkRequest bulkRequest= Requests.bulkRequest();
		try {
			for(T object : datas) {
				String id = "";
				if(object instanceof Map) {
					Map<String,Object> m = (Map<String,Object>)object;
					if(m.containsKey("id") && m.get("id")!=null) {
						id =m.get("id").toString();
					}					
				}else {
					if(object.getClass().getDeclaredField("id")!=null && object.getClass().getField("id").get(object)!=null) {
						id =object.getClass().getField("id").get(object).toString();
					}
				}
				if(Strings.isNullOrEmpty(id)) {
					throw new Exception("id of delete doc not null");
				}
				
				DeleteRequest deleteRequest  =new DeleteRequest();
				deleteRequest.index(indexName);
				deleteRequest.id(id);
				bulkRequest.add(deleteRequest);
			}
			BulkResponse bulkResponse = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
			return bulkResponse.hasFailures()==false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 * ??????????????????(???????????????????????????????????????)
	 * @param indexName  ????????????
	 * @param insertDatas  ?????????????????? List<T> ????????????List<Map<String,Object>>
	 * @param updateDatas  ?????????????????? List<T> ????????????List<Map<String,Object>>
	 * @param deleteDatas  ?????????????????? List<T> ????????????List<Map<String,Object>>
	 * @return  true--??????  false--??????
	 */
	@SuppressWarnings("unchecked")
	public <T> Boolean bulk(String indexName , List<T> insertDatas ,List<T> updateDatas ,List<T> deleteDatas ) {
		if (!checkRestHighLevelClient())
			return false;
		BulkRequest bulkRequest= Requests.bulkRequest();
		IndexRequest indexRequest = null;
		UpdateRequest updateRequest = null;
		DeleteRequest deleteRequest = null;
		Map<String,Object> m = null;
		try {
			if(insertDatas!=null) {
				for(T object : insertDatas) {
					indexRequest =new IndexRequest(indexName);
					if(object instanceof Map) {
						m = (Map<String,Object>)object;
						if(m.containsKey("id") && m.get("id")!=null) {
							indexRequest.id(m.get("id").toString());
						}					
					}else {
						if(object.getClass().getDeclaredField("id")!=null && object.getClass().getField("id").get(object)!=null) {
							indexRequest.id(object.getClass().getField("id").get(object).toString());
						}
					}
					indexRequest.source(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames), XContentType.JSON);		
					bulkRequest.add(indexRequest);
				}
			}
			if(updateDatas!=null) {
				for(T object : updateDatas) {
					updateRequest  =new UpdateRequest();
					updateRequest.index(indexName);
					if(object instanceof Map) {
						m = (Map<String,Object>)object;
						if(m.containsKey("id") && m.get("id")!=null) {
							updateRequest.id(m.get("id").toString());
						}					
					}else {
						if(object.getClass().getDeclaredField("id")!=null && object.getClass().getField("id").get(object)!=null) {
							updateRequest.id(object.getClass().getField("id").get(object).toString());
						}
					}
					updateRequest.doc(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames), XContentType.JSON);		
					bulkRequest.add(updateRequest);
				}
			}
			if(deleteDatas!=null) {
				for(T object : deleteDatas) {
					String id = "";
					if(object instanceof Map) {
						m = (Map<String,Object>)object;
						if(m.containsKey("id") && m.get("id")!=null) {
							id =m.get("id").toString();
						}					
					}else {
						if(object.getClass().getDeclaredField("id")!=null && object.getClass().getField("id").get(object)!=null) {
							id =object.getClass().getField("id").get(object).toString();
						}
					}
					if(Strings.isNullOrEmpty(id)) {
						throw new Exception("id of delete doc not null");
					}
					deleteRequest  =new DeleteRequest();
					deleteRequest.index(indexName);
					deleteRequest.id(id);
					bulkRequest.add(deleteRequest);
				}
			}
			
			BulkResponse bulkResponse = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
			return bulkResponse.hasFailures()==false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * ??????id????????????
	 * @param indexName  ????????????
	 * @param id         id???
	 * @return null--?????????
	 */
	public Map<String,Object> getById(String indexName,String id){
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("_id", id));
		List<Map<String,Object>> result = query(indexName, sourceBuilder);
		if(result!=null  && result.size()>0) {
			return result.get(0);
		}
		return null;
	}
	
	
	/**
	 * ??????ids??????????????????
	 * @param indexName  ????????????
	 * @param ids        id??????
	 * @return null--?????????
	 */
	public List<Map<String,Object>> getByIds(String indexName,String... ids){
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termsQuery("_id", ids));
		return query(indexName, sourceBuilder);
	}
	
}

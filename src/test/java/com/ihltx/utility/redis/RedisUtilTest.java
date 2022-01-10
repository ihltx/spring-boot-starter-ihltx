package com.ihltx.utility.redis;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.RedisUtil;
import com.ihltx.utility.util.entity.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class RedisUtilTest {

	private RedisFactory redisFactory;

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeEach
	public void beforeEach(){
		try{
			redisFactory = applicationContext.getBean(RedisFactory.class);
			if(redisFactory!=null){
				RedisUtil redisUtil = redisFactory.openSession();
				if(redisUtil!=null){
					Boolean rs = redisUtil.set("test", 12);
					if(rs == false){
						redisFactory = null;
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			redisFactory = null;
		}
	}

	@Test
	public void test_10_select() {
		if(redisFactory == null) return;
		RedisUtil redisUtil;
		try {
			redisUtil = redisFactory.openSession();
			assertEquals(redisUtil !=null, true);

		} catch (RedisException e) {

			e.printStackTrace();
			assertEquals(false, true);
		}


	}


	@Test
	public void test_11_set() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = false;

			byte byte1 = 1 , byte2=0;
			rs = redisUtil.set("byte", byte1);
			assertEquals(rs, true);
			byte2= redisUtil.get("byte", Byte.class);
			assertEquals(byte1 == byte2, true);

			Byte byte11 = 1 , byte12=0;
			rs = redisUtil.set("byte1", byte11);
			assertEquals(rs, true);
			byte12= redisUtil.get("byte1", Byte.class);
			assertEquals(byte11.equals(byte12), true);


			short short1 = 1 , short2=0;
			rs = redisUtil.set("short", short1);
			assertEquals(rs, true);
			short2= redisUtil.get("short", Short.class);
			assertEquals(short1 == short2, true);

			Short short11 = 1 , short12=0;
			rs = redisUtil.set("short1", short11);
			assertEquals(rs, true);
			short12= redisUtil.get("short1", Short.class);
			assertEquals(short11.equals(short12), true);

			int int1 = 1 , int2=0;
			rs = redisUtil.set("int", int1);
			assertEquals(rs, true);
			int2= redisUtil.get("int", Integer.class);
			assertEquals(int1 == int2, true);

			Integer int11 = 1 , int12=0;
			rs = redisUtil.set("int1", int11);
			assertEquals(rs, true);
			int12= redisUtil.get("int1", Integer.class);
			assertEquals(int11.equals(int12), true);

			long long1 = 1234567890123456L , long2=0L;
			rs = redisUtil.set("long", long1);
			assertEquals(rs, true);
			long2= redisUtil.get("long", Long.class);
			assertEquals(long1 == long2, true);

			Long long11 = 1L , long12=0L;
			rs = redisUtil.set("long1", long11);
			assertEquals(rs, true);
			long12= redisUtil.get("long1", Long.class);
			assertEquals(long11.equals(long12), true);


			float float1 = 1f , float2=0f;
			rs = redisUtil.set("float", float1);
			assertEquals(rs, true);
			float2= redisUtil.get("float", Float.class);
			assertEquals(float1 == float2, true);

			Float float11 = 1f , float12=0f;
			rs = redisUtil.set("float1", float11);
			assertEquals(rs, true);
			float12= redisUtil.get("float1", Float.class);
			System.out.println("=" + float11 + "=");
			System.out.println("=" + float12 + "=");
			assertEquals(float11.equals(float12), true);


			double double1 = 1d , double2=0d;
			rs = redisUtil.set("double", double1);
			assertEquals(rs, true);
			double2= redisUtil.get("double", Double.class);
			assertEquals(double1 == double2, true);

			Double double11 = 1d , double12=0d;
			rs = redisUtil.set("double1", double11);
			assertEquals(rs, true);
			double12= redisUtil.get("double1", Double.class);
			assertEquals(double11.equals(double12), true);


			char char1 = 'a' , char2='\0';
			rs = redisUtil.set("char", char1);
			assertEquals(rs, true);
			char2= redisUtil.get("char", Character.class);
			assertEquals(char1 == char2, true);

			char1 = '\0';
			rs = redisUtil.set("char", char1);
			assertEquals(rs, true);
			char2= redisUtil.get("char", Character.class);
			assertEquals(char1 == char2, true);

			Character char11 = 'a' , char12='\0';
			rs = redisUtil.set("char1", char11);
			assertEquals(rs, true);
			char12= redisUtil.get("char1", Character.class);
			assertEquals(char11.equals(char12), true);

			char11 = '\0';
			rs = redisUtil.set("char1", char11);
			assertEquals(rs, true);
			char12= redisUtil.get("char1", Character.class);
			assertEquals(char11.equals(char12), true);


			boolean boolean1 = true , boolean2= false;
			rs = redisUtil.set("boolean", boolean1);
			assertEquals(rs, true);
			boolean2= redisUtil.get("boolean", Boolean.class);
			assertEquals(boolean1 == boolean2, true);

			Boolean boolean11 = true , boolean12= false;
			rs = redisUtil.set("boolean1", boolean11);
			assertEquals(rs, true);
			boolean12= redisUtil.get("boolean1", Boolean.class);
			assertEquals(boolean11.equals(boolean12), true);

			String string1 = "你好abc" , string2= null;
			rs = redisUtil.set("string", string1);
			assertEquals(rs, true);
			string2= redisUtil.get("string");
			assertEquals(string1.equals(string2), true);

			String string11 = "你好abc" , string12= null;
			rs = redisUtil.set("string", string11);
			assertEquals(rs, true);
			string12= redisUtil.get("string" , String.class);
			assertEquals(string11.equals(string12), true);

			Result result1 = new Result("0", "你好abc") , result2=null;
			rs = redisUtil.set("object", result1);
			assertEquals(rs, true);
			result2= redisUtil.get("object" , Result.class);
			assertEquals(string11.equals(string12), true);
			assertEquals(result1.getStatus().equals(result2.getStatus()), true);
			assertEquals(result1.getMsg().equals(result2.getMsg()), true);



			double number = 0 , number1 =0 ;

			redisUtil =redisFactory.openSession("default");

			number = 1.2;
			rs = redisUtil.set("number", number);
			assertEquals(rs, true);

			number1= redisUtil.get("number", Double.class);
			assertEquals(number1 == number, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

	@Test
	public void test_11_set1() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("second");
			assertEquals(redisUtil!=null, true);

			Boolean rs = false;

			byte byte1 = 1 , byte2=0;
			rs = redisUtil.set("byte", byte1);
			assertEquals(rs, true);
			byte2= redisUtil.get("byte", Byte.class);
			assertEquals(byte1 == byte2, true);

			Byte byte11 = 1 , byte12=0;
			rs = redisUtil.set("byte1", byte11);
			assertEquals(rs, true);
			byte12= redisUtil.get("byte1", Byte.class);
			assertEquals(byte11.equals(byte12), true);


			short short1 = 1 , short2=0;
			rs = redisUtil.set("short", short1);
			assertEquals(rs, true);
			short2= redisUtil.get("short", Short.class);
			assertEquals(short1 == short2, true);

			Short short11 = 1 , short12=0;
			rs = redisUtil.set("short1", short11);
			assertEquals(rs, true);
			short12= redisUtil.get("short1", Short.class);
			assertEquals(short11.equals(short12), true);

			int int1 = 1 , int2=0;
			rs = redisUtil.set("int", int1);
			assertEquals(rs, true);
			int2= redisUtil.get("int", Integer.class);
			assertEquals(int1 == int2, true);

			Integer int11 = 1 , int12=0;
			rs = redisUtil.set("int1", int11);
			assertEquals(rs, true);
			int12= redisUtil.get("int1", Integer.class);
			assertEquals(int11.equals(int12), true);

			long long1 = 1L , long2=0L;
			rs = redisUtil.set("long", long1);
			assertEquals(rs, true);
			long2= redisUtil.get("long", Long.class);
			assertEquals(long1 == long2, true);

			Long long11 = 1L , long12=0L;
			rs = redisUtil.set("long1", long11);
			assertEquals(rs, true);
			long12= redisUtil.get("long1", Long.class);
			assertEquals(long11.equals(long12), true);


			float float1 = 1f , float2=0f;
			rs = redisUtil.set("float", float1);
			assertEquals(rs, true);
			float2= redisUtil.get("float", Float.class);
			assertEquals(float1 == float2, true);

			Float float11 = 1f , float12=0f;
			rs = redisUtil.set("float1", float11);
			assertEquals(rs, true);
			float12= redisUtil.get("float1", Float.class);
			System.out.println("=" + float11 + "=");
			System.out.println("=" + float12 + "=");
			assertEquals(float11.equals(float12), true);


			double double1 = 1d , double2=0d;
			rs = redisUtil.set("double", double1);
			assertEquals(rs, true);
			double2= redisUtil.get("double", Double.class);
			assertEquals(double1 == double2, true);

			Double double11 = 1d , double12=0d;
			rs = redisUtil.set("double1", double11);
			assertEquals(rs, true);
			double12= redisUtil.get("double1", Double.class);
			assertEquals(double11.equals(double12), true);


			char char1 = 'a' , char2='\0';
			rs = redisUtil.set("char", char1);
			assertEquals(rs, true);
			char2= redisUtil.get("char", Character.class);
			assertEquals(char1 == char2, true);

			Character char11 = 'a' , char12='\0';
			rs = redisUtil.set("char1", char11);
			assertEquals(rs, true);
			char12= redisUtil.get("char1", Character.class);
			assertEquals(char11.equals(char12), true);


			boolean boolean1 = true , boolean2= false;
			rs = redisUtil.set("boolean", boolean1);
			assertEquals(rs, true);
			boolean2= redisUtil.get("boolean", Boolean.class);
			assertEquals(boolean1 == boolean2, true);

			Boolean boolean11 = true , boolean12= false;
			rs = redisUtil.set("boolean1", boolean11);
			assertEquals(rs, true);
			boolean12= redisUtil.get("boolean1", Boolean.class);
			assertEquals(boolean11.equals(boolean12), true);

			String string1 = "你好abc" , string2= null;
			rs = redisUtil.set("string", string1);
			assertEquals(rs, true);
			string2= redisUtil.get("string");
			assertEquals(string1.equals(string2), true);

			String string11 = "你好abc" , string12= null;
			rs = redisUtil.set("string", string11);
			assertEquals(rs, true);
			string12= redisUtil.get("string" , String.class);
			assertEquals(string11.equals(string12), true);

			Result result1 = new Result("0", "你好abc") , result2=null;
			rs = redisUtil.set("object", result1);
			assertEquals(rs, true);
			result2= redisUtil.get("object" , Result.class);
			assertEquals(string11.equals(string12), true);
			assertEquals(result1.getStatus().equals(result2.getStatus()), true);
			assertEquals(result1.getMsg().equals(result2.getMsg()), true);



			double number = 0 , number1 =0 ;

			redisUtil =redisFactory.openSession("default");

			number = 1.2;
			rs = redisUtil.set("number", number);
			assertEquals(rs, true);

			number1= redisUtil.get("number", Double.class);
			assertEquals(number1 == number, true);

			List<Result> list = new ArrayList<>();
			Result r =new Result("11","abcd");
			list.add(r);
			r =new Result("12","abcd");
			list.add(r);
			r =new Result("13","李四");
			list.add(r);
			redisUtil.set("abcdef" , list);

			List list1= redisUtil.get("abcdef" , List.class);
			System.out.println(list1);

			List<Result> list2 = (List<Result>)list1;
			System.out.println(list2);
			for(Result r1 : list2){
				System.out.println(r1);
			}


		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

	@Test
	public void test_12_setByExpire() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = redisUtil.set("double_expire", 1.5d, 3L);
			double number = redisUtil.get("double_expire", Double.class);
			assertEquals(number == 1.5d, true);


			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			rs = redisUtil.exists("double_expire");
			assertEquals(rs, false);


			rs = redisUtil.set("double_expire1", 1.6d, 0L);
			number = redisUtil.get("double_expire1", Double.class);
			assertEquals(number == 1.6d, true);


			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			rs = redisUtil.exists("double_expire1");
			assertEquals(rs, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}


	@Test
	public void test_13_Expire() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = redisUtil.set("double_expire", 1.5d);
			double number = redisUtil.get("double_expire", Double.class);
			assertEquals(number == 1.5d, true);

			rs = redisUtil.expire("double_expire", 3L);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			rs = redisUtil.exists("double_expire");
			assertEquals(rs, false);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}

	@Test
	public void test_14_getExpire() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = redisUtil.set("double_expire1", 1.5d);
			System.out.println(rs);
			double number = redisUtil.get("double_expire1", Double.class);
			assertEquals(number == 1.5d, true);

			long num = 0;

			num = redisUtil.remove("double_expire2");
			assertEquals(num >= 0, true);

			long expire = redisUtil.getExpire("double_expire2");
			System.out.println(expire);
			assertEquals(expire == -2, true);

			expire = redisUtil.getExpire("double_expire1");
			System.out.println(expire);
			assertEquals(expire == -1, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}


	@Test
	public void test_15_exists() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = redisUtil.set("double_expire1", 1.5d);
			double number = redisUtil.get("double_expire1", Double.class);
			assertEquals(number == 1.5d, true);

			rs = redisUtil.exists("double_expire1");
			assertEquals(rs, true);

			long num = 0;

			num = redisUtil.remove("double_expire1");
			assertEquals(num >= 0, true);

			rs = redisUtil.exists("double_expire1");
			assertEquals(rs, false);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}


	@Test
	public void test_16_remove() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = redisUtil.set("double_expire1", 1.5d);
			double number = redisUtil.get("double_expire1", Double.class);
			assertEquals(number == 1.5d, true);

			rs = redisUtil.exists("double_expire1");
			assertEquals(rs, true);

			long num = 0;

			num = redisUtil.remove("double_expire1");
			assertEquals(num >= 0, true);

			rs = redisUtil.exists("double_expire1");
			assertEquals(rs, false);

			num = redisUtil.remove("double_expire1","double_expire2","double_expire3");
			System.out.println(num);
			assertEquals(num >= 0, true);
		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}

	@Test
	public void test_16_setBit() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = redisUtil.setBit("double_expire1", 0L,true);
			assertEquals(rs, true);

			rs = redisUtil.exists("double_expire1");
			assertEquals(rs, true);



			Boolean b = redisUtil.getBit("double_expire1",0L);
			assertEquals(b, true);

			rs = redisUtil.setBit("double_expire1", 1L,true);
			assertEquals(rs, true);

			b = redisUtil.getBit("double_expire1",1L);
			assertEquals(b, true);

			rs = redisUtil.setBit("double_expire1", 2L,false);
			assertEquals(rs, true);

			b = redisUtil.getBit("double_expire1",0L);
			assertEquals(b, true);
			b = redisUtil.getBit("double_expire1",1L);
			assertEquals(b, true);

			b = redisUtil.getBit("double_expire1",2L);
			assertEquals(b, false);
		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}



	}



	@Test
	public void test_16_incr() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

//			Boolean rs =  true;

			assertEquals(redisUtil.remove("incr1") >= 0, true);

			long num = redisUtil.incr("incr1");
			System.out.println(num);
			assertEquals(num == 1, true);

			num = redisUtil.incr("incr1");
			System.out.println(num);
			assertEquals(num == 2, true);

			num = redisUtil.incr("incr1");
			System.out.println(num);
			assertEquals(num == 3, true);

			num = redisUtil.incr("incr1", 6L);
			System.out.println(num);
			assertEquals(num == 9, true);
		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}

	@Test
	public void test_17_decr() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

//			Boolean rs =  true;

			assertEquals(redisUtil.remove("decr1") >= 0, true);

			long num = redisUtil.decr("decr1");
			System.out.println(num);
			assertEquals(num == -1, true);

			num = redisUtil.decr("decr1");
			System.out.println(num);
			assertEquals(num == -2, true);

			num = redisUtil.decr("decr1");
			System.out.println(num);
			assertEquals(num == -3, true);

			num = redisUtil.decr("decr1", 6L);
			System.out.println(num);
			assertEquals(num == -9, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}



	@Test
	public void test_18_hSet() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = true;
			String value = "127.0.0.1", value1 = null;

			assertEquals(redisUtil.remove("hashtable") >= 0, true);

			rs = redisUtil.hSet("hashtable", "host", value);
			assertEquals(rs, true);

			value1 = redisUtil.hGet("hashtable", "host");
			assertEquals(value.equals(value1), true);

			value1 = redisUtil.hGet("hashtable", "host1");
			System.out.println(value1);
			assertEquals(value1==null, true);


			rs = redisUtil.hSet("hashtable", "host2", value, 3L);
			assertEquals(rs, true);

			value1 = redisUtil.hGet("hashtable", "host2");
			assertEquals(value.equals(value1), true);


			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			value1 = redisUtil.hGet("hashtable", "host2");
			assertEquals(value1==null, true);

			Result obj =new Result("0", "中国") , obj1=null;
			rs = redisUtil.hSet("hashtable", "obj", obj);
			assertEquals(rs, true);

			obj1 = redisUtil.hGet("hashtable", "obj" , Result.class);
			assertEquals(obj1!=null, true);
			assertEquals(obj.getStatus().equals(obj1.getStatus()), true);
			assertEquals(obj.getMsg().equals(obj1.getMsg()), true);
		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}


	@Test
	public void test_20_hDel() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;

			rs = redisUtil.hDel("hashtable" , "key1" , "key2");
			assertEquals(rs, true);

			rs = redisUtil.hSet("hashtable" , "byte" , 1);
			assertEquals(rs, true);

			rs = redisUtil.hSet("hashtable" , "short" , 1);
			assertEquals(rs, true);

			rs = redisUtil.hSet("hashtable" , "string" , "中国a中");
			assertEquals(rs, true);


			Result obj  =new Result("0", "中国d是");
			rs = redisUtil.hSet("hashtable" , "object" , obj);
			assertEquals(rs, true);

			rs = redisUtil.hDel("hashtable" , "byte" , "short");
			assertEquals(rs, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}



	}


	@Test
	public void test_21_hashKey() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;

			rs = redisUtil.hHasKey("hashtable" , "key1");
			assertEquals(rs, false);

			rs = redisUtil.hSet("hashtable" , "byte" , 1);
			assertEquals(rs, true);

			rs = redisUtil.hSet("hashtable" , "short" , 1);
			assertEquals(rs, true);

			rs = redisUtil.hSet("hashtable" , "string" , "中国a中");
			assertEquals(rs, true);


			Result obj  =new Result("0", "中国d是");
			rs = redisUtil.hSet("hashtable" , "object" , obj);
			assertEquals(rs, true);

			rs = redisUtil.hHasKey("hashtable" , "short");
			assertEquals(rs, true);
		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}


	@Test
	public void test_22_hIncr() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;

			rs = redisUtil.hDel("hashtable" , "hIncr");
			assertEquals(rs, true);

			rs = redisUtil.hHasKey("hashtable" , "hIncr");
			assertEquals(rs, false);

			double num = redisUtil.hIncr("hashtable", "hIncr");
			assertEquals(num==1, true);

			num = redisUtil.hIncr("hashtable", "hIncr");
			assertEquals(num==2, true);

			num = redisUtil.hIncr("hashtable", "hIncr" , 1.6);
			assertEquals(num==3.6, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}



	@Test
	public void test_23_hDecr() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);


			Boolean rs =  true;

			rs = redisUtil.hDel("hashtable" , "hDecr");
			assertEquals(rs, true);

			rs = redisUtil.hHasKey("hashtable" , "hDecr");
			assertEquals(rs, false);

			double num = redisUtil.hDecr("hashtable", "hDecr");
			System.out.println(num);
			assertEquals(num==-1, true);

			num = redisUtil.hDecr("hashtable", "hDecr");
			System.out.println(num);
			assertEquals(num==-2, true);

			num = redisUtil.hDecr("hashtable", "hDecr" , 1.6);
			System.out.println(num);
			assertEquals(num==-3.6, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}



	@Test
	public void test_24_hDecr() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;

			rs = redisUtil.hDel("hashtable" , "hDecr");
			assertEquals(rs, true);

			rs = redisUtil.hHasKey("hashtable" , "hDecr");
			assertEquals(rs, false);

			double num = redisUtil.hDecr("hashtable", "hDecr");
			System.out.println(num);
			assertEquals(num==-1, true);

			num = redisUtil.hDecr("hashtable", "hDecr");
			System.out.println(num);
			assertEquals(num==-2, true);

			num = redisUtil.hDecr("hashtable", "hDecr" , 1.6);
			System.out.println(num);
			assertEquals(num==-3.6, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}


	}

	@Test
	public void test_25_sSet() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs = true;

			long num = redisUtil.remove("set");
			assertEquals( num >= 0, true);

			num = redisUtil.remove("set0");
			assertEquals( num >= 0, true);

			String[]  arr1 = null;

			num = redisUtil.sSet("set00" , arr1);
			assertEquals(num == -1, true);

			num = redisUtil.sSet("set01" , new String[] {});
			assertEquals(num == -1, true);



			num = redisUtil.sSet("set" , "a" ,"b" ,"c" ,"c");
			assertEquals(num ==3, true);

			Set<String> set = redisUtil.sGet("set");
			assertEquals(set.size()==3, true);

			assertEquals(set.contains("a"), true);
			assertEquals(set.contains("b"), true);
			assertEquals(set.contains("c"), true);


			num = redisUtil.sSet("set1" , 3L , new String[] {"a" ,"b" ,"c" ,"c"});
			System.out.println(num);
			assertEquals(num >=0, true);

			Set<String> set1 = redisUtil.sGet("set1");
			System.out.println(set1.size());
			assertEquals(set1.size()==3, true);

			assertEquals(set1.contains("a"), true);
			assertEquals(set1.contains("b"), true);
			assertEquals(set1.contains("c"), true);

			rs = redisUtil.sHasKey("set1" , "a");
			System.out.println(rs);
			assertEquals(rs, true);

			rs = redisUtil.sHasKey("set1" , "aa");
			assertEquals(rs, false);


			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			rs = redisUtil.exists("set1");
			System.out.println(rs);
			assertEquals(rs, false);

			num = redisUtil.remove("set2");
			System.out.println(num);
			assertEquals(num >=0, true);


			num = redisUtil.sSet("set2" ,new Integer[] {1,2,3,4,5});
			System.out.println(num);
			assertEquals(num >=0, true);

			Set<Integer> set2 = redisUtil.sGet("set2" , Integer.class);
			System.out.println(set2.size());
			assertEquals(set2.size()==5, true);
			System.out.println(set2);



			num = redisUtil.sSet("set3" , 3L ,new Integer[] {1,2,3,4,5});
			System.out.println(num);
			assertEquals(num >= 0, true);


			Set<Integer> set3 = redisUtil.sGet("set3" , Integer.class);
			assertEquals(set3.size()==5, true);
			System.out.println(set3);
			for(Integer o : set3) {
				System.out.println(o);
			}

			rs = redisUtil.sHasKey("set3", 3);
			assertEquals(rs, true);

			rs = set3.contains(4);
			assertEquals(rs, true);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("===========================");
			rs = redisUtil.exists("set3");
			System.out.println("key{set3}： " + (rs?"exists":"not exists") +" assert: false , result: " + rs);
			assertEquals(rs, false);
			System.out.println("===========================");

			Result rs1=new Result("0", "中国1");
			Result rs2=new Result("1", "中国2");
			Result rs3=new Result("2", "中国3");

			redisUtil.remove("set4");
			num = redisUtil.sSet("set4" ,new Result[] {rs1 ,rs2,rs3});
			System.out.println(num);
			assertEquals(num >= 0, true);

			Set<Result> set4 = redisUtil.sGet("set4" , Result.class);
			assertEquals(set4.size()==3, true);
			System.out.println(set4);
			for(Result r : set4) {
				System.out.println(r);
			}

			rs = redisUtil.sHasKey("set4", rs1);
			assertEquals(rs, true);

			num = redisUtil.sSize("set4");
			assertEquals(num == 3, true);

			num = redisUtil.sSet("set5" ,new Object[] {rs1 ,rs2,rs3});
			assertEquals(num >= 0, true);


		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}



	@Test
	public void test_26_sRemove() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

//			Boolean rs =  true;
			long num=0;

			num = redisUtil.remove("set13");
			assertEquals(num >=0, true);

			num = redisUtil.sSet("set13" , new String[] {"a" ,"b" ,"c" ,"c"});
			assertEquals(num >= 0, true);

			String[] set13= null;

			num = redisUtil.sRemove("set13" , set13);
			assertEquals(num == -1, true);


			num = redisUtil.sRemove("set13" , new String[] {});
			assertEquals(num == -1, true);

			num = redisUtil.sRemove("set13" , new String[] {"a" ,"b"});
			assertEquals(num == 2, true);


			num = redisUtil.remove("set14");
			assertEquals(num >=0, true);

			num = redisUtil.sSet("set14" , new Integer[] {12,34,56,56,2,34});
			assertEquals(num >= 0, true);

			Integer[] set14= null;

			num = redisUtil.sRemove("set14" , set14);
			assertEquals(num == -1, true);


			num = redisUtil.sRemove("set14" , new Integer[] {});
			assertEquals(num == -1, true);

			num = redisUtil.sRemove("set14" , new Integer[] {12,34});
			assertEquals(num == 2, true);


			Result rs1=new Result("0", "中国1");
			Result rs2=new Result("1", "中国2");
			Result rs3=new Result("2", "中国3");

			num = redisUtil.remove("set15");
			assertEquals(num >=0, true);

			num = redisUtil.sSet("set15" , new Result[] {rs1,rs1,rs2,rs3});
			assertEquals(num >= 0, true);

			Result[] set15= null;

			num = redisUtil.sRemove("set15" , set15);
			assertEquals(num == -1, true);


			num = redisUtil.sRemove("set15" , new Result[] {});
			assertEquals(num == -1, true);

			num = redisUtil.sRemove("set15" , new Result[] {rs1,rs3});
			assertEquals(num == 2, true);

			num = redisUtil.sSet("set16" ,new Object[] {rs1 ,rs2,rs3});
			assertEquals(num >= 0, true);


		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}


	@Test
	public void test_27_lSet() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;
			long num=0;

			num = redisUtil.remove("list1");
			assertEquals(num >=0, true);

			rs = redisUtil.lSet("list1" , 12);
			assertEquals(rs, true);

			num  = redisUtil.lSize("list1");
			assertEquals(num == 1, true);

			int a  = redisUtil.lGet("list1" , 0L , Integer.class);
			System.out.println(a);
			assertEquals(a == 12, true);


			num = redisUtil.remove("list2");
			assertEquals(num >=0, true);

			List<String> list2 =new ArrayList<String>();
			list2.add("abc");
			list2.add("def");
			list2.add("中国a中");

			rs = redisUtil.lSet("list2" , list2);
			assertEquals(rs, true);

			num  = redisUtil.lSize("list2");
			assertEquals(num == 3, true);

			String s = redisUtil.lGet("list2" , 0L);
			System.out.println(s);
			assertEquals(s.equals("abc"), true);

			s = redisUtil.lGet("list2" , 1L);
			System.out.println(s);
			assertEquals(s.equals("def"), true);

			s = redisUtil.lGet("list2" , 2L);
			System.out.println(s);
			assertEquals(s.equals("中国a中"), true);



			num = redisUtil.remove("list3");
			assertEquals(num >=0, true);
			Result rs1=new Result("0", "中国1");
			Result rs2=new Result("1", "中国2");
			Result rs3=new Result("2", "中国3");

			List<Result> list3 =new ArrayList<Result>();

			list3.add(rs1);
			list3.add(rs2);
			list3.add(rs3);

			rs = redisUtil.lSet("list3" , list3);
			assertEquals(rs, true);

			num  = redisUtil.lSize("list3");
			assertEquals(num == 3, true);

			Result rs11 = redisUtil.lGet("list3" , 0L , Result.class);
			System.out.println(rs11);
			assertEquals(rs11.getStatus().equals("0"), true);
			assertEquals(rs11.getMsg().equals("中国1"), true);

			rs11 = redisUtil.lGet("list3" , 1L , Result.class);
			System.out.println(rs11);
			assertEquals(rs11.getStatus().equals("1"), true);
			assertEquals(rs11.getMsg().equals("中国2"), true);


			rs11 = redisUtil.lGet("list3" , 2L , Result.class);
			System.out.println(rs11);
			assertEquals(rs11.getStatus().equals("2"), true);
			assertEquals(rs11.getMsg().equals("中国3"), true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}


	@Test
	public void test_28_lGet() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;
			long num=0;

			num = redisUtil.remove("list1");
			assertEquals(num >=0, true);

			rs = redisUtil.lSet("list1" , 12);
			assertEquals(rs, true);

			rs = redisUtil.lSet("list1" , 13);
			assertEquals(rs, true);

			rs = redisUtil.lSet("list1" , 14);
			assertEquals(rs, true);

			rs = redisUtil.lSet("list1" , 15);
			assertEquals(rs, true);

			List<Integer> list11 = redisUtil.lGet("list1" , 0L ,1L , Integer.class);
			assertEquals(list11.size() == 2, true);
			assertEquals(list11.get(0) == 12, true);
			assertEquals(list11.get(1) == 13, true);



			num = redisUtil.remove("list2");
			assertEquals(num >=0, true);

			List<String> list2 =new ArrayList<String>();
			list2.add("abc");
			list2.add("def");
			list2.add("中国a中");
			list2.add("中国a中12");

			rs = redisUtil.lSet("list2" , list2);
			assertEquals(rs, true);

			List<String> list22 = redisUtil.lGet("list2" , 1L ,3L);
			assertEquals(list22.size() == 3, true);
			assertEquals(list22.get(0).equals("def"), true);
			assertEquals(list22.get(1).equals("中国a中"), true);
			assertEquals(list22.get(2).equals("中国a中12"), true);


			num = redisUtil.remove("list3");
			assertEquals(num >=0, true);
			Result rs1=new Result("0", "中国1");
			Result rs2=new Result("1", "中国2");
			Result rs3=new Result("2", "中国3");

			List<Result> list3 =new ArrayList<Result>();

			list3.add(rs1);
			list3.add(rs2);
			list3.add(rs3);

			rs = redisUtil.lSet("list3" , list3);
			assertEquals(rs, true);

			List<Result> list33 = redisUtil.lGet("list3" , 1L ,2L , Result.class);
			assertEquals(list33.size() == 2, true);
			assertEquals(list33.get(0).getStatus().equals("1"), true);
			assertEquals(list33.get(0).getMsg().equals("中国2"), true);

			assertEquals(list33.get(1).getStatus().equals("2"), true);
			assertEquals(list33.get(1).getMsg().equals("中国3"), true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}



	@Test
	public void test_29_lPush() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;
			long num=0;

			num = redisUtil.remove("list1");
			assertEquals(num >=0, true);
			rs = redisUtil.lPush("list1" , 12);
			assertEquals(rs, true);
			rs = redisUtil.lPush("list1" , 13);
			assertEquals(rs, true);
			rs = redisUtil.lPush("list1" , 14);
			assertEquals(rs, true);
			rs = redisUtil.lPush("list1" , 15);
			assertEquals(rs, true);
			num = redisUtil.lSize("list1");
			assertEquals(num == 4, true);

			Integer a = redisUtil.lPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 3, true);
			assertEquals(a == 15, true);

			a = redisUtil.lPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 2, true);
			assertEquals(a == 14, true);

			a = redisUtil.lPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 1, true);
			assertEquals(a == 13, true);


			a = redisUtil.lPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 0, true);
			assertEquals(a == 12, true);

			a = redisUtil.lPop("list1" , Integer.class);
			assertEquals(a == null, true);
			num = redisUtil.lSize("list1");
			assertEquals(num == 0, true);




			num = redisUtil.remove("list2");
			assertEquals(num >=0, true);

			rs = redisUtil.lPush("list2" , "abc");
			assertEquals(rs, true);
			rs = redisUtil.lPush("list2" , "def");
			assertEquals(rs, true);
			rs = redisUtil.lPush("list2" , "中国a中12");
			assertEquals(rs, true);

			String s = redisUtil.lPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 2, true);
			assertEquals(s.equals("中国a中12"), true);

			s = redisUtil.lPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 1, true);
			assertEquals(s.equals("def"), true);

			s = redisUtil.lPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 0, true);
			assertEquals(s.equals("abc"), true);

			s = redisUtil.lPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 0, true);
			assertEquals(s==null, true);



			num = redisUtil.remove("list3");
			assertEquals(num >=0, true);

			Result rs1=new Result("0", "中国1");
			Result rs2=new Result("1", "中国2");
			Result rs3=new Result("2", "中国3");

			rs = redisUtil.lPush("list3" , rs1);
			assertEquals(rs, true);
			rs = redisUtil.lPush("list3" , rs2);
			assertEquals(rs, true);
			rs = redisUtil.lPush("list3" , rs3);
			assertEquals(rs, true);

			Result result = redisUtil.lPop("list3" , Result.class);
			num = redisUtil.lSize("list3");
			assertEquals(num == 2, true);
			assertEquals(result.getStatus().equals("2"), true);
			assertEquals(result.getMsg().equals("中国3"), true);

			result = redisUtil.lPop("list3" , Result.class);
			num = redisUtil.lSize("list3");
			assertEquals(num == 1, true);
			assertEquals(result.getStatus().equals("1"), true);
			assertEquals(result.getMsg().equals("中国2"), true);


			result = redisUtil.lPop("list3" , Result.class);
			num = redisUtil.lSize("list3");
			assertEquals(num == 0, true);
			assertEquals(result.getStatus().equals("0"), true);
			assertEquals(result.getMsg().equals("中国1"), true);

			s = redisUtil.lPop("list3");
			num = redisUtil.lSize("list3");
			assertEquals(num == 0, true);
			assertEquals(s==null, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}

	@Test
	public void test_30_queue() {
		if(redisFactory == null) return;

		try {
			RedisUtil redisUtil =redisFactory.openSession("default");
			assertEquals(redisUtil!=null, true);

			Boolean rs =  true;
			long num=0;

			num = redisUtil.remove("list1");
			assertEquals(num >=0, true);
			rs = redisUtil.lSet("list1" , 12);
			assertEquals(rs, true);
			rs = redisUtil.lSet("list1" , 13);
			assertEquals(rs, true);
			rs = redisUtil.lSet("list1" , 14);
			assertEquals(rs, true);
			rs = redisUtil.lSet("list1" , 15);
			assertEquals(rs, true);
			num = redisUtil.lSize("list1");
			assertEquals(num == 4, true);

			Integer a = redisUtil.lLeftPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 3, true);
			assertEquals(a == 12, true);

			a = redisUtil.lLeftPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 2, true);
			assertEquals(a == 13, true);

			a = redisUtil.lLeftPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 1, true);
			assertEquals(a == 14, true);


			a = redisUtil.lLeftPop("list1" , Integer.class);
			num = redisUtil.lSize("list1");
			assertEquals(num == 0, true);
			assertEquals(a == 15, true);

			a = redisUtil.lLeftPop("list1" , Integer.class);
			assertEquals(a == null, true);
			num = redisUtil.lSize("list1");
			assertEquals(num == 0, true);




			num = redisUtil.remove("list2");
			assertEquals(num >=0, true);

			rs = redisUtil.lSet("list2" , "abc");
			assertEquals(rs, true);
			rs = redisUtil.lSet("list2" , "def");
			assertEquals(rs, true);
			rs = redisUtil.lSet("list2" , "中国a中12");
			assertEquals(rs, true);

			String s = redisUtil.lLeftPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 2, true);
			assertEquals(s.equals("abc"), true);

			s = redisUtil.lLeftPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 1, true);
			assertEquals(s.equals("def"), true);

			s = redisUtil.lLeftPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 0, true);
			assertEquals(s.equals("中国a中12"), true);

			s = redisUtil.lLeftPop("list2");
			num = redisUtil.lSize("list2");
			assertEquals(num == 0, true);
			assertEquals(s==null, true);



			num = redisUtil.remove("list3");
			assertEquals(num >=0, true);

			Result rs1=new Result("0", "中国1");
			Result rs2=new Result("1", "中国2");
			Result rs3=new Result("2", "中国3");

			rs = redisUtil.lSet("list3" , rs1);
			assertEquals(rs, true);
			rs = redisUtil.lSet("list3" , rs2);
			assertEquals(rs, true);
			rs = redisUtil.lSet("list3" , rs3);
			assertEquals(rs, true);

			Result result = redisUtil.lLeftPop("list3" , Result.class);
			num = redisUtil.lSize("list3");
			assertEquals(num == 2, true);
			assertEquals(result.getStatus().equals("0"), true);
			assertEquals(result.getMsg().equals("中国1"), true);

			result = redisUtil.lLeftPop("list3" , Result.class);
			num = redisUtil.lSize("list3");
			assertEquals(num == 1, true);
			assertEquals(result.getStatus().equals("1"), true);
			assertEquals(result.getMsg().equals("中国2"), true);


			result = redisUtil.lLeftPop("list3" , Result.class);
			num = redisUtil.lSize("list3");
			assertEquals(num == 0, true);
			assertEquals(result.getStatus().equals("2"), true);
			assertEquals(result.getMsg().equals("中国3"), true);

			s = redisUtil.lLeftPop("list3");
			num = redisUtil.lSize("list3");
			assertEquals(num == 0, true);
			assertEquals(s==null, true);

		}catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}

	}

}

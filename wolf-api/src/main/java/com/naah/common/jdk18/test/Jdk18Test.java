package com.naah.common.jdk18.test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.naah.common.jdk18.utils.BigDecimalUtils;
import com.naah.common.jdk18.vo.StudentInfo;
import com.naah.common.jdk18.vo.User;

public class Jdk18Test {

	public static void main(String[] args) {
	    List<User> userList = findUserList();
		
	   // testGroup(userList);
		
	   // testFilter(userList);
	    
	   // testSum(userList);
	    //testManayGroup(userList);
	   // testMaxAndMinValue(userList);
	    
	   //testList2Map(userList);
	    
	    //testSort(userList);
	    
	    //testMap();
	    testFilter();
	}
	
	private static List<User> findUserList() {
        List<User> userList = new ArrayList<User>();
		
		for (int i=1;i<=100;i++) {
			User user = new User();
			user.setId(Long.valueOf(i));
			user.setAge(i);
			user.setName("张均"+i);
			
			if(i%5==0) {
				user.setJobNumber("jobNumber5");
			}else {
				user.setJobNumber("jobNumber0");
			}
			
			if(i%2==0) {
				user.setSex("MAN");
			}else {
				user.setSex("WOMAN");
			}
			
			if(i%5==0) {
				user.setFamilyMemberQuantity(new BigDecimal(5));
			}else if(i%5==1){
				user.setFamilyMemberQuantity(new BigDecimal(1));
			}else if(i%5==2) {
				user.setFamilyMemberQuantity(new BigDecimal(2));
			}else if(i%5==3) {
				user.setFamilyMemberQuantity(new BigDecimal(3));
			}else {
				user.setFamilyMemberQuantity(new BigDecimal(i));
			}
			//user.setEntryDate(DateUtils.addDays(new Date(), i));
			user.setEntryDate(new Date());
			userList.add(user);
		}
		return userList;
	}
	
	public static void testGroup(List<User> userList){
		//通过groupingBy可以分组指定字段
        Map<String, List<User>> groupBySex = userList.stream().collect(Collectors.groupingBy(User::getSex));
		
        //遍历分组
        for (Map.Entry<String, List<User>> entryUser : groupBySex.entrySet()) {
            String key = entryUser.getKey();
            List<User> entryUserList = entryUser.getValue();
            System.out.println(entryUserList.size()+"====key-->"+key+",value:"+JSON.toJSONString(entryUserList));
        }
	}
	
	
	
	public static void testManayGroup(List<User> userList){
		//通过groupingBy多字段分组   家庭成员、行不、工作编号
        Function<User, List<Object>> compositeKey = wlb ->
                Arrays.<Object>asList(wlb.getFamilyMemberQuantity(), wlb.getSex(), wlb.getJobNumber());
                
        Map<Object, List<User>> map =
        		userList.stream().collect(Collectors.groupingBy(compositeKey, Collectors.toList()));
        
        
        //遍历分组
        for (Map.Entry<Object, List<User>> entryUser : map.entrySet()) {
        	
        	//对应的key值
            List<Object> key = (List<Object>) entryUser.getKey();
            
            String age = String.valueOf(key.get(0));
            String sex = String.valueOf(key.get(1));
            String jobNum = String.valueOf(key.get(2));

            //通过key获取对应的value
            List<User> entryUserList = entryUser.getValue();
            System.out.println("----grouping by 分组key>>"+JSON.toJSON(key)+"--------"+entryUserList.size());
            System.out.println("----分组统计对应的数据   value>>"+JSON.toJSON(entryUserList));
 
        }
	}
	
	public static void testFilter(List<User> userList) {
		//通过filter方法可以过滤某些条件 jobNumber5
        List<User> userCommonList = userList.stream().filter(a -> a.getJobNumber().equals("jobNumber5")).collect(Collectors.toList());
        for (User user : userCommonList) {
        	System.out.println("====value:"+JSON.toJSONString(user));
		}
	}
	
	
	public static void testSum(List<User> userList) {
		//求和
        //基本类型
        int sumAge = userList.stream().mapToInt(User::getAge).sum();
	    System.out.println(sumAge);

        
        //BigDecimal求和
        BigDecimal totalQuantity = userList.stream().map(User::getFamilyMemberQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
	    System.out.println(totalQuantity);
	}
	
	public static void testBigDecimal(List<User> userList) {
		BigDecimal totalQuantity = userList.stream().map(User::getFamilyMemberQuantity).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
	    System.out.println(totalQuantity);

	}
	
	public static void testMaxAndMinValue(List<User> userList){
		Date entryDate = userList.stream().map(User::getEntryDate).max(Date::compareTo).get();
		System.out.println("最早日期：entryDate"+entryDate);
		
		 entryDate = userList.stream().map(User::getEntryDate).min(Date::compareTo).get();
		System.out.println("最早日期：entryDate"+entryDate);
		
		
		Comparator<User> comparator = Comparator.comparing(User::getAge);
		User maxObject = userList.stream().max(comparator).get();
		System.out.println("age-max："+JSON.toJSONString(maxObject));

	}
	
	public static void testList2Map(List<User> userList) {
		 /**
         * List -> Map
         * 需要注意的是：
         * toMap 如果集合对象有重复的key，会报错Duplicate key ....
         *  user1,user2的id都为1。
         *  可以用 (k1,k2)->k1 来设置，如果有重复的key,则保留key1,舍弃key2
         */
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, a -> a,(k1,k2)->k1));
		System.out.println("list-to-map："+JSON.toJSONString(userMap));
	}
	
	public static void testSort(List<User> userList) {
		//排序
        //单字段排序，根据id排序
        userList.sort(Comparator.comparing(User::getId));
		System.out.println("单字段排序，根据id排序："+JSON.toJSONString(userList));

        
        //多字段排序，根据id，年龄排序
        userList.sort(Comparator.comparing(User::getId).thenComparing(User::getAge));
        
		System.out.println("多字段排序，根据id，年龄排序："+JSON.toJSONString(userList));

	}
	
	public static void testMap() {
		Map<String,Integer> map = new HashMap<>();
        map.put("张三", 1);
        map.put("李四", 2);
        map.put("王五", 3);
        //第一种方法
        Optional<Map.Entry<String, Integer>> max0 = map.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        //第二种方法
        Optional<Map.Entry<String, Integer>> max1 = map.entrySet()
                .stream().max((x1, x2) -> Integer.compare(x1.getValue(), x2.getValue()));

        //第三种方法
        Optional<Map.Entry<String, Integer>> max3 = map.entrySet()
                .stream()
                .collect(Collectors.maxBy(Map.Entry.comparingByValue()));

        //第四种方法
        Optional<Map.Entry<String, Integer>> max4 = map.entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));

        //第五种方法
        IntSummaryStatistics max5 = map.entrySet()
                .stream()
                .collect(Collectors.summarizingInt(Map.Entry::getValue));
	}
	
	
	public static void testFilter() {
		//测试数据，请不要纠结数据的严谨性
		List<StudentInfo> studentList = new ArrayList<>();
		studentList.add(new StudentInfo("李小明",true,18,1.76,LocalDate.of(2001,3,23)));
		studentList.add(new StudentInfo("张小丽",false,18,1.61,LocalDate.of(2001,6,3)));
		studentList.add(new StudentInfo("王大朋",true,19,1.82,LocalDate.of(2000,3,11)));
		studentList.add(new StudentInfo("陈小跑",false,17,1.67,LocalDate.of(2002,10,18)));
		
		//整体输出
		StudentInfo.printStudents(studentList,"全量输出");
		
		//查找身高在1.8米及以上的男生
		List<StudentInfo> boys =  studentList.stream().filter(item->item.getGender() && item.getHeight()>=1.8).collect(Collectors.toList());
		
		StudentInfo.printStudents(boys,"身高在1.8米及以上的男生");

	}
	
}

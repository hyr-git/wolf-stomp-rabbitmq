package com.naah.common.jdk18.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.naah.common.jdk18.vo.UserBean;
 
/**
 * Lambda表达式写法
 *
 * @author gilbert
 */
public class LambdaDemo {
 
    /**
     * 功能描述 无参无返回值
     *
     * @param list
     * @return void
     * @author gilbert
     * @date 2019/6/12
     */
    public static void lambdaWithParamAndNoReturn(List<UserBean> list) {
    	System.out.println("lambdaWithParamAndNoReturn----->");
        list.forEach(userBean -> System.out.println("hello," + userBean.getUserName()));
    }
 
    /**
     *功能描述 重新封装集合数据
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void initList(List<UserBean> list){
    	System.out.println("initList----->");
        List<UserBean> userList = list.stream().map(userBean -> new UserBean(userBean.getUserId(), userBean.getWorkId(), userBean.getUserName())).collect(Collectors.toList());
        userList.forEach(userBean -> System.out.println("new list:" + userBean.getUserName()));
    }
 
    /**
     *功能描述 集合过滤
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void filterList(List<UserBean> list){
    	System.out.println("filterList----->");
        //List<UserBean> filterList = list.stream().filter(userBean -> userBean.getUserName().contains("g")).collect(Collectors.toList());
        List<UserBean> filterList = list.stream().filter(userBean -> Integer.valueOf(userBean.getUserId()) > 6).collect(Collectors.toList());
        filterList.forEach(userBean -> System.out.println("filter list:" + userBean.getUserName()));
    }
 
    /**
     *功能描述 排序
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void sortList(List<UserBean> list){
    	System.out.println("sortList----->");

        //按userid排序
        List<UserBean> sortList = list.stream().sorted((userBean1,userBean2) -> userBean1.getUserName().compareTo(userBean2.getUserName())).collect(Collectors.toList());
        sortList.forEach(userBean -> System.out.println("sortList:" + userBean.getUserId() + "," + userBean.getUserName()));
    
        List<UserBean> sortList1 =  list.stream().sorted(Comparator.comparing(UserBean::getUserName)).collect(Collectors.toList());
        sortList1.forEach(s->System.out.println(JSON.toJSON(sortList1)));
    }
 
    /**
     *功能描述 多条件排序
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void multiSortList(List<UserBean> list){
    	System.out.println("multiSortList----->");

        list.sort(Comparator.comparing(UserBean::getUserId).thenComparing(UserBean::getUserName));
        list.forEach(userBean -> System.out.println("multiSortList:" + userBean.getUserId() + "," + userBean.getUserName()));
    }
 
    /**
     *功能描述 倒序
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void reversedSortList(List<UserBean> list){
    	System.out.println("reversedSortList----->");

        //第一种写法
        Comparator<UserBean> comparator = (userBean1,userBean2) -> userBean1.getUserName().compareTo(userBean2.getUserName());
        list.sort(comparator.reversed());
        //第二种写法
        //list.sort(Comparator.comparing(UserBean::getUserId).reversed());
 
        list.forEach(userBean -> System.out.println("reverseSortList:" + userBean.getUserId() + "," + userBean.getUserName()));
    }
 
    /**
     *功能描述 多条件倒序
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void multiReversedSortList(List<UserBean> list){
    	System.out.println("multiReversedSortList----->");

        list.sort(Comparator.comparing(UserBean::getUserId).thenComparing(UserBean::getUserName).reversed());
        list.forEach(userBean -> System.out.println("multiReversedSortList:" + userBean.getUserId() + "," + userBean.getUserName()));
    }
 
    /**
     *功能描述 集合分组
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void groupByList(List<UserBean> list){
    	System.out.println("groupByList----->");

        Map<String,List<UserBean>> groupByMap = list.stream().collect(Collectors.groupingBy(UserBean::getWorkId));
        groupByMap.forEach((k,v) -> System.out.println(k+"," + v));
    }
 
    /**
     *功能描述 求和
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void sumByList(List<UserBean> list){
    	System.out.println("sumByList----->");

        System.out.println("sum="+ list.stream().mapToInt(UserBean::getUserId).sum());
    }
 
    /**
     *功能描述 最大值
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void maxByList(List<UserBean> list){
    	System.out.println("maxByList----->");

        OptionalInt optional = list.stream().mapToInt(UserBean::getUserId).max();
        System.out.println("max=" + optional.getAsInt());
    }
 
    /**
     *功能描述 最小值
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void minByList(List<UserBean> list){
    	System.out.println("minByList----->");

        OptionalInt optional = list.stream().mapToInt(UserBean::getUserId).min();
        System.out.println("min=" + optional.getAsInt());
    }
 
    /**
     *功能描述 平均值
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void averageByList(List<UserBean> list){
    	System.out.println("averageByList----->");

        OptionalDouble optionalDouble = list.stream().mapToInt(UserBean::getUserId).average();
        System.out.println("average=" + optionalDouble.getAsDouble());
    }
 
    /**
     *功能描述 List转map
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void listToMap(List<UserBean> list){
    	System.out.println("listToMap----->");

        //用 (k1,k2)->k1 来设置，如果有重复的key,则保留key1,舍弃key2
        Map<Integer,UserBean> map = list.stream().collect(Collectors.toMap(UserBean::getUserId,userBean -> userBean, (k1, k2) -> k1));
        map.forEach((k,v) -> System.out.println("k=" + k + ",v=" + v));
    }
 
    /**
     *功能描述 map转list
     * @author gilbert
     * @date 2019/6/12
     * @param [map]
     * @return void
     */
    public static void mapToList(Map<Integer,String> map){
    	System.out.println("mapToList----->");

        List<UserBean> list = map.entrySet().stream().sorted(Comparator.comparing(key -> key.getKey())).map(key -> new UserBean(Integer.valueOf(key.getKey()),key.getValue(),key.getValue())).collect(Collectors.toList());
        list.forEach(userBean -> System.out.println(userBean.getUserId() + "," + userBean.getUserName()));
    }
 
    /**
     *功能描述 字符串转list
     * @author gilbert
     * @date 2019/6/12
     * @param [str]
     * @return void
     */
    public static void stringToList(String str){
    	System.out.println("stringToList----->");

        //不需要处理
        //<String> list = Arrays.asList(str.split(","));
        //需要处理
        List<String> list = Arrays.asList(str.split(",")).stream().map(string -> String.valueOf(string)).collect(Collectors.toList());
        list.forEach(string -> System.out.println(string));
    }
 
    /**
     *功能描述 姓名以逗号拼接
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void joinStringValueByList(List<UserBean> list){
    	System.out.println("joinStringValueByList----->");

        System.out.println(list.stream().map(UserBean::getUserName).collect(Collectors.joining(",")));
    }
 
    /**
     *功能描述 分组统计
     * @author gilbert
     * @date 2019/6/12
     * @param [list]
     * @return void
     */
    public static void countByList(List<UserBean> list){
    	System.out.println("countByList----->");

        Map<String, Long> map = list.stream().collect(Collectors.groupingBy(UserBean::getWorkId,Collectors.counting()));
        map.forEach((k,v) -> System.out.println("key=" + k + ",value=" + v));
    }
    
    
 
    public static void main(String[] args) {
    	
        //List<UserBean> list = Arrays.asList(new UserBean(1, "AKB001", "gilbert"), new UserBean("2", "AKB002", "apple"), new UserBean("3", "AKB003", "cat"));
        List<UserBean> list = Stream.of(new UserBean(1, "AKB001", "gilbert"),
                                        new UserBean(2, "AKB002", "apple"),
                                        new UserBean(4, "AKB004", "dog"),
                                        new UserBean(5, "AKB005", "egg"),
                                        new UserBean(6, "AKB006", "frog"),
                                        new UserBean(6, "AKB006", "banana"),
                                        new UserBean(7, "AKB007", "google"),
                                        new UserBean(3, "AKB003", "cat"))
                .collect(Collectors.toList());
        lambdaWithParamAndNoReturn(list);
        initList(list);
        filterList(list);
        sortList(list);
        reversedSortList(list);
        multiSortList(list);
        multiReversedSortList(list);
        groupByList(list);
        sumByList(list);
        maxByList(list);
        minByList(list);
        averageByList(list);
        listToMap(list);
        String str = "apple,banana,cat,dog";
        stringToList(str);
        Map<Integer,String> map =new HashMap<Integer, String>() {
            {
                put(1, "apple");
                put(2, "banana");
                put(3, "cat");
                put(4, "dog");
                put(5, "frog");
            }
        };
        mapToList(map);
        joinStringValueByList(list);
        countByList(list);
    }
    

}

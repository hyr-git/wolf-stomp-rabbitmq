package com.naah.common.jdk18.test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;


/***
 * 模拟HashMap中的扩容,动态计算出对应的扩容前后的下标。
 * 假定当前容量是16,扩容之后的容量是32
 * 扩容之前的索引下标例如是3,5,6
 * 扩容之后的索引下标就是   19,21,22
 * @author mlj
 *
 */
public class JDKHashCode {
	
	public static void main(String[] args) {
		
		List<Integer> numList =Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14);
		numList.forEach(s->System.out.print("key="+s+",index="+getHashCode(s,16)+"   "));
		System.out.println();
		
		numList.forEach(s->System.out.print("key="+s+",index="+getHashCode(s,32)+"   "));
		System.out.println();
		getHouseTotal();
		
		tableSizeFor(16);
		
		/*List<String> keyList = Arrays.asList("a","b","c","d","e","f","g","h");
		while() {
			
		}*/
		
		//"word,excel,pdf,ppt,txt,img"
		String key = "word,excel,pdf,ppt,txt,img";
		System.out.println(key.toUpperCase());
		
	}
	
	static final int MAXIMUM_CAPACITY = 1 << 30;
	
	public static int hashCode(Integer key ) {
		//return key.hashCode()^(length-1);
		 int h;
         int a = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
         return a;
	}
	
	
	public static BigDecimal getHouseTotal() {
		BigDecimal big = BigDecimal.valueOf(0.564);
		BigDecimal years = BigDecimal.valueOf(30);
		BigDecimal total = BigDecimal.valueOf(80);
		BigDecimal tolCount =  total.multiply(big).multiply(years);
		System.out.println(tolCount.floatValue());
		return tolCount;
	}
	
	public static int getHashCode(Integer key,int length ) {
		 return hashCode(key)^(length-1);
	}
	
	
	
	 static final int tableSizeFor(int cap) {
		 System.out.println("cap--------"+cap);
		 System.out.println("MAXIMUM_CAPACITY--------"+MAXIMUM_CAPACITY);
	        int n = cap - 1;
			System.out.println("n--------"+cap);
	        n |= n >>> 1;
			System.out.println("n |= n >>> 1--------"+n);

	        n |= n >>> 2;
			System.out.println("n |= n >>> 2--------"+n);

	        n |= n >>> 4;
			System.out.println("n |= n >>> 4--------"+n);

	        n |= n >>> 8;
			System.out.println("n |= n >>> 8--------"+n);

	        n |= n >>> 16;
			System.out.println("n |= n >>> 16--------"+n);

	        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	    }

	
	 static final int hash(Object key) {
		 int h;
         int a = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
		 System.out.println("a="+a);
		 return a;
	 }
	 
	/* void transfer(entry newTable[],boolean rehash) {
		 int newCapacity = newTable.length;
		 for(Entry<K,V> e : newTable) {
			 //获取链表的头信息e
			 while(null!= e) {
				 //获取要转移的下一个节点
				 Entry<K<V>> next = e.next;
			     if(rehash) {
			    	 e.hash = null == e.key ? 0 :hash(e.key);
			     }
			     //计算要转移的节点在新的entry数组中newTable中的位置
			     int i= indexFor(e.hash,newCapacity);
			     //使用头插法将需要转移的节点插入到newTable原有的单链表中
			     e.next = newTable[i];
			     //将newTable的hash桶的指针指向要移动的节点
			     newTable[i] = e;
			     //转移下一个需要移动的节点e
			     e = next;
			 }
		 }
	 }*/

}

package com.naah.stomp.config;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.naah.stomp.interceptor.GetHeaderParamInterceptor;
import com.naah.stomp.properties.StompProperties;


/*****
 * stomp定义了自己的消息传输体制。首先是通过一个后台绑定的连接点endpoint来建立socket连接，然后生产者通过send方法，绑定好发送的目的地也就是destination，而topic和app(后面还会说到)则是一种消息处理手段的分支，走app/url的消息会被你设置到的MassageMapping拦截到，进行你自己定义的具体逻辑处理，而走topic/url的消息就不会被拦截，直接到Simplebroker节点中将消息推送出去。其中simplebroker是spring的一种基于内存的消息队列，你也可以使用activeMQ，rabbitMQ代替。

作者：rpf_siwash
链接：https://www.jianshu.com/p/32fae52c61f6
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 * 
 * 主要包含2部分内容:
 *     1、消息代理--指定了客户端定义地址以及发送消息的路由地址
 *     2、endpoint指定了客户端建立连接时的请求地址。
 * @author m1832
 *
 */
@Configuration
//使用此注解来标识WebSocket的broker，即使用broker来处理消息
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(StompProperties.class)
public class WebScoketStompConfig implements WebSocketMessageBrokerConfigurer {

	@Autowired
	private StompProperties stompProperties;
	
	@Autowired
    private GetHeaderParamInterceptor getHeaderParamInterceptor;

	
	   /***
	    * 修改消息代理的配置，默认处理以/topic为前缀的消息
	    * setApplicationDestinationPrefixes：配置请求的根路径，表示通过MessageMapping处理/td/*请求，不会发送到代理
	    * enableStompBrokerRelay:配置代理，匹配路径的请求会进入代理：mq等
	    */
	     @Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
	    	 config.setPathMatcher(new AntPathMatcher("."));

	    	/**
	    	 * 所有目的地以“/app”打头的消息都将会路由到带有@MessageMapping注解的方法中，而不会发布到代理队列或主题中
	    	 * 该消息不会发送到代理MQ中，直接使用messageMapping控制器处理
	         *  默认前缀是/user,此处修改为/app
	         * 用户可以订阅来自订阅来自"/app/{topicName}"和"/topic/{topicName}"的消息，
	         * topic为前缀的直接发送到端点上,app为前缀的通过@MessageMapping路由,最终也发到端点上
	         */
	        config.setApplicationDestinationPrefixes("/app");
	    	 
	    	 /**
	         * 在Controller中，可通过@SendTo注解指明发送目标，这样服务器就可以将消息发送到订阅相关消息的客户端
	         * 在本Demo中，使用topic来达到聊天室效果（单聊+多聊），使用toAll进行群发效果
	         * 客户端只可以订阅这两个前缀的主题
	         * 启用simpleBroker,使得订阅到此"topic"等前缀的客户端可以收到消息
             */
			//基于内存实现的stomp代理，单机适用
			//config.enableSimpleBroker("/topic","/queue","/toAll","/toWeb","toApp");

	        
	        /**
	         * 启用了STOMP代理中继（broker relay）功能，并将其目的地前缀设置为“/topic”和“/queue”。这样的话，
	         * Spring就能知道所有目的地前缀为“/topic”或“/queue”的消息都会发送到STOMP代理中
	         * 基于MQ实现stomp代理,适用于集群,以/topic与/queue开头的消息会发送到stomp代理中
	         * TODO 服务器参数配置  如果是用自己的消息中间件，则按照下面的去配置，删除上面的配置
	         * systemLogin:设置代理所需的密码
             * client:设置客户端连接代理所需的密码，默认为guest
	         */
	        config.enableStompBrokerRelay("/topic", "/queue")//,"/hyr/toWeb/","/hyr/toApp/"
	            .setRelayHost(stompProperties.getServer())
                .setRelayPort(stompProperties.getPort())
                .setClientLogin(stompProperties.getUsername())
                .setClientPasscode(stompProperties.getPassword())
                .setSystemLogin(stompProperties.getUsername())
                .setSystemPasscode(stompProperties.getPassword())
                .setVirtualHost("/");
	        
	        /***
	         * 设置单独发送到某个user需要添加的前缀,用户订阅/user/topic/work地址后会去掉/user,
	         * 并加上用户名(需要spring security支持)等唯一表示组成新的目的地发送回去。
	         * 对于这个url来说加上后缀之后走代理.发送时需要定制用户名,具体用法：
	         *     convertAndSendToUser或者sendToUser注解
	         */
	        //config.setUserDestinationPrefix("/user");
	    }
	     
	     
	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	 
	        /**
	         * 路径"/websocket"被注册为STOMP端点，对外暴露，客户端通过该路径接入WebSocket服务
	         */
	        registry.addEndpoint("/webSocket").setAllowedOrigins("*")
	        /*.setHandshakeHandler(new DefaultHandshakeHandler() {
	            @Override
	            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
	                  //将客户端标识封装为Principal对象，从而让服务端能通过getName()方法找到指定客户端
	                  Object o = attributes.get("name");
	                  return new FastPrincipal(o.toString());
	            }
	         })*/
	        //添加socket拦截器，用于从请求中获取客户端标识参数
	        //.addInterceptors(new HandleShakeInterceptors())
	        .withSockJS();
	    }
	    
	    
	    
	    //定义一个自己的权限验证类
	    class FastPrincipal implements Principal {

	        private final String name;

	        public FastPrincipal(String name) {
	            this.name = name;
	        }

	        public String getName() {
	            return name;
	        }
	    }
	    
	    /**
	     * 注册stomp的端点
	         具体参考 https://blog.csdn.net/qq_28988969/article/details/78134114
	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        // 允许使用socketJs方式访问，访问点为webSocketServer，允许跨域
	        // 在网页上我们就可以通过这个链接
	        // http://localhost:8080/webSocketServer
	        // 来和服务器的WebSocket连接
	        registry.addEndpoint("/webSocketServer").setAllowedOrigins("*").withSockJS();
	    }  */
	    
	    /**
	     * 采用自定义拦截器，获取connect时候传递的参数
	     * 配置客户端入站通道拦截器
	     * @param registration
	     */
	    @Override
	    public void configureClientInboundChannel(ChannelRegistration registration) {
	        registration.interceptors(getHeaderParamInterceptor);
	    }

}

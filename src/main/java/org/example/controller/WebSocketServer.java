package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.example.context.BaseContext;
import org.example.service.PostService;
import org.example.service.CommentService;
import org.example.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@RestController
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {
    private static ObjectMapper objectMapper=new ObjectMapper();

    private static Map<Integer,Session> sessionMap=new HashMap<>();
    @Autowired
    private static RedisTemplate redisTemplate;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate){
        WebSocketServer.redisTemplate=redisTemplate;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) throws IOException {
        List msgs=new ArrayList();
        String key= RedisKeyUtil.getChatBoxKey(userId);
        while (redisTemplate.opsForList().size(key)>0) {
            String msg = redisTemplate.opsForList().leftPop(key).toString();
            msgs.add(msg);
        }
        session.getBasicRemote().sendText(msgs.toString());
        System.out.println(msgs);
        System.out.println("WebSocket opened: " + session.getId() + ", userId: " + userId);
        sessionMap.put(userId,session);

    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        Integer userId = BaseContext.get();
        Map msg = objectMapper.readValue(message, Map.class);
        Integer senderId = (Integer) msg.get("senderId");
        Integer receiverId = (Integer) msg.get("receiverId");
        msg.put("speakerId",senderId);
        msg.remove("senderId");
        msg.remove("receiverId");
        message = objectMapper.writeValueAsString(msg);
        Session session1 = sessionMap.get(receiverId);
        if(session1!=null){
            session1.getBasicRemote().sendText(message);
            System.out.println("发送消息:"+message);
        }else{
            String key=RedisKeyUtil.getChatBoxKey(receiverId);
            redisTemplate.opsForList().rightPush(key,message);
            System.out.println("存储消息:"+message);
        }


    }

    @OnMessage
    public void onBinaryMessage(Session session, ByteBuffer buffer) {
        System.out.println("Received binary message from " + session.getId() + ". Size = " + buffer.limit());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessionMap.remove(BaseContext.get());
        System.out.println("WebSocket " + session.getId() + " closed: " + BaseContext.get());
    }

}
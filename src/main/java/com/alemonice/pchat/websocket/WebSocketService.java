package com.alemonice.pchat.websocket;

import com.alemonice.pchat.enums.DataType;
import com.alemonice.pchat.enums.UserStatus;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketService extends WebSocketHandler {

    private static Logger logger = LogManager.getLogger(WebSocketService.class.getName());

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userId;

    private static ConcurrentHashMap<String, Session> userSocketMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法.
     *
     * @param userId  当前会话用户
     * @param session 当前会话session
     * @throws IOException IOException
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws IOException {
        this.session = session;
        this.userId = userId;
        userSocketMap.put(userId, session);
        addOnlineCount();
        logger.info(session.getId() + "有新链接加入，当前链接数为：" + userSocketMap.size());
    }

    @OnClose
    public void onClose() throws IOException {
        userSocketMap.remove(this.userId);
        JSONObject obj = new JSONObject();
        obj.put("dataType", DataType.User.getValue());
        obj.put("status", UserStatus.reave.getValue());
        obj.put("user", userMap.get(this.userId));
        msgHandler(obj.toJSONString());
        subOnlineCount();
        logger.info("有一链接关闭，当前链接数为：" + userSocketMap.size());
    }

    /**
     * 收到客户端消息.
     *
     * @param message 客户端发送过来的消息
     * @param session 当前会话session
     * @throws IOException IOException
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("来终端的警情消息:" + message);
        msgHandler(message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("userSocketMap发生错误!");
        error.printStackTrace();
    }

    public static ConcurrentHashMap<String, Session> getUserSocketMap() {
        return userSocketMap;
    }

}


package com.alemonice.pchat.websocket;

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
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{userId}")
public class WarningPushSocket {

    private static Logger logger = LogManager.getLogger(WarningPushSocket.class.getName());

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userId;

    private static ConcurrentHashMap<String, Session> userSocketMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法.
     * @param userId 当前会话ID
     * @param session 当前会话session
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        this.session = session;
        this.userId = userId;
        userSocketMap.put(userId, session);
        addOnlineCount();
        logger.info(session.getId() + "有新链接加入，当前链接数为：" + userSocketMap.size());
    }

    @OnClose
    public void onClose() {
        userSocketMap.remove(this.userId);
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
//        sendMsgToAll(message);
        msgHandler(message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("userSocketMap发生错误!");
        error.printStackTrace();
    }

    /**
     * 给所有客户端群发消息.
     *
     * @param message 消息内容
     * @throws IOException IOException
     */
    private void sendMsgToAll(String message) throws IOException {
        for (Map.Entry<String, Session> entry : userSocketMap.entrySet()) {
            entry.getValue().getBasicRemote().sendText(message);
        }
        logger.info("成功群送一条消息:" + userSocketMap.size());
    }

    private void msgHandler(String msg) throws IOException  {
        JSONObject msgObj = JSONObject.parseObject(msg);
        String talkContent = msgObj.getString("talkContent");
        String currentDialogUser = msgObj.getString("currentDialogUser");
        String userId = msgObj.getString("userId");
        JSONObject sendJson = new JSONObject();
        sendJson.put("sendId", userId);
        sendJson.put("talkContent", talkContent);
        sendJson.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        sendMessage(userSocketMap.get(currentDialogUser), sendJson.toJSONString());
    }

    /**
     * 单条消息发送.
     * @param session session
     * @param message message
     * @throws IOException IOException
     */
    private void sendMessage(Session session, String message) throws IOException {
        session.getBasicRemote().sendText(message);
        logger.info("成功发送一条消息:" + message);
    }

    public static synchronized int getOnlineCount() {
        return WarningPushSocket.onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WarningPushSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WarningPushSocket.onlineCount--;
    }

    public static Object getUserList() {
        return userSocketMap.keys();
    }
}


package com.alemonice.pchat.websocket;

import com.alemonice.pchat.enums.DataType;
import com.alemonice.pchat.enums.UserStatus;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.Session;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HB on 2021/4/8 10:57.
 */
public class WebSocketHandler {

    private static Logger logger = LogManager.getLogger(WebSocketHandler.class.getName());

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        onlineCount--;
    }


    protected static Map<String, JSONObject> userMap = new HashMap<>();

    public static List<String> getUserIdList() {
        List<String> list = new ArrayList<>();
        Enumeration<String> keys = WebSocketService.getUserSocketMap().keys();
        while (keys.hasMoreElements()) {
            list.add(keys.nextElement());
        }
        return list;
    }

    public static Map<String, JSONObject> getUserMap() {
        return userMap;
    }

    /**
     * 推送用户信息.
     *
     * @param status 1、2
     * @param user user
     * @throws IOException IOException
     */
    public void pushUserInfo(int status, JSONObject user) throws IOException {
        JSONObject userListJson = new JSONObject();
        userListJson.put("dataType", DataType.User.getValue());
        userListJson.put("status", status);
        userListJson.put("user", user);
        sendMessageToAll(userListJson.toJSONString());
    }

    /**
     * 收到消息后的处理.
     *
     * @param msg msg
     * @throws IOException IOException
     */
    public void msgHandler(String msg) throws IOException {

        JSONObject msgObj = JSONObject.parseObject(msg);
        int dataType = msgObj.getInteger("dataType");
        if (dataType == DataType.User.getValue()) {
            JSONObject user = msgObj.getJSONObject("user");
            int status = msgObj.getInteger("status");
            if (status == UserStatus.online.getValue()) {
                userMap.put(user.getString("userId"), user);
            } else if (status == UserStatus.reave.getValue()) {
                userMap.remove(user.getString("userId"));
            }
            pushUserInfo(status, user);

        } else if (dataType == DataType.Text.getValue()) {
            String talkContent = msgObj.getString("talkContent");
            String currentDialogUserId = msgObj.getString("currentDialogUserId");
            String userId = msgObj.getString("userId");
            JSONObject sendJson = new JSONObject();
            sendJson.put("dataType", DataType.Text.getValue());
            sendJson.put("sendId", userId);
            sendJson.put("talkContent", talkContent);
            sendJson.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
            sendMessage(WebSocketService.getUserSocketMap().get(currentDialogUserId), sendJson.toJSONString());
        }

    }

    /**
     * 发送单条消息.
     *
     * @param session session
     * @param message message
     * @throws IOException IOException
     */
    private void sendMessage(Session session, String message) throws IOException {
        session.getBasicRemote().sendText(message);
        logger.info("成功发送一条消息:" + message);
    }

    /**
     * 给所有客户端群发消息.
     *
     * @param message 消息内容
     * @throws IOException IOException
     */
    private void sendMessageToAll(String message) throws IOException {
        for (Map.Entry<String, Session> entry : WebSocketService.getUserSocketMap().entrySet()) {
            entry.getValue().getBasicRemote().sendText(message);
        }
        logger.info("成功群送一条消息:" + WebSocketService.getUserSocketMap().size());
    }

}

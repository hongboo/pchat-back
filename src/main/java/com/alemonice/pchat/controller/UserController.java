package com.alemonice.pchat.controller;

import com.alemonice.pchat.websocket.WarningPushSocket;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by HB on 2021/4/6 10:02.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/getUserId")
    public Object getUserId(String userId) {
        JSONObject json = new JSONObject();
        if (userId == null) {
            json.put("userId", UUID.randomUUID().toString().replaceAll("-", ""));
        } else {
            json.put("userId", userId);
        }
        return json;
    }

    @PostMapping("/getUserList")
    public Object getUserList() {
        JSONObject json = new JSONObject();
        json.put("userList", WarningPushSocket.getUserList());
        return json;
    }
}

package com.alemonice.pchat.controller;

import com.alemonice.pchat.websocket.WebSocketHandler;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Created by HB on 2021/4/6 10:02.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/getUserId")
    public Object getUserId(String userId) {
        JSONObject json = new JSONObject();
        json.put("userId", generateRandomNumber());
        return json;
    }

    private String generateRandomNumber() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    @PostMapping("/getUserMap")
    public Object getUserMap() {
        JSONObject json = new JSONObject();
        json.put("userMap", WebSocketHandler.getUserMap());
        return json;
    }
}

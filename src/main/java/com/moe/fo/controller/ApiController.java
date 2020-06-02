package com.moe.fo.controller;

import com.moe.fo.common.ApiResponse;
import com.moe.fo.util.BlockChainUtil;
import com.moe.fo.util.MD5;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {
    @PostMapping("/login")
    public ApiResponse<String> apiLogin(@RequestParam String username, @RequestParam String password) {
        String path = "user/";
        return BlockChainUtil.login(path, username, password);
    }

    @PostMapping("/register")
    public ApiResponse<String> apiRegister(@RequestParam String username, @RequestParam String password) {
        String path = "user/";
        return BlockChainUtil.register(path, username, password);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> apiLogout(@RequestParam String token) {
        return BlockChainUtil.logout(token);
    }

    @PostMapping("/getInfo")
    public ApiResponse<Map<String, Object>> apiGetUserInfo(@RequestParam String token) {
        return BlockChainUtil.getUserInfo(token);
    }

    @PostMapping("/setInfo")
    public ApiResponse<String> apiSetUserInfo(
            @RequestParam String token,
            @RequestParam String name,
            @RequestParam int role
    ) {
        return BlockChainUtil.setUserInfo(token, name, role);
    }

    @PostMapping("/getItem")
    public ApiResponse<Map<String, Object>> apiGetItemInfo(@RequestParam String id) {
        return BlockChainUtil.getItemInfo(id);
    }

    @PostMapping("/setItem")
    public ApiResponse<String> apiSetItemInfo(
            @RequestParam String name
    ) {
        String id = MD5.encode("" + System.currentTimeMillis());
        return BlockChainUtil.setItemInfo(id, name);
    }

    @PostMapping("/addOp")
    public ApiResponse<String> apiAddOp(
            @RequestParam String token,
            @RequestParam String id,
            @RequestParam String action,
            @RequestParam String memo
    ) {
        return BlockChainUtil.addOp(token, id, action, memo);
    }
}

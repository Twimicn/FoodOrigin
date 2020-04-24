package com.moe.fo.controller;

import com.moe.fo.util.BlockChainUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@Controller
public class IndexController {
    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "HELLO WORLD" + BlockChainUtil.getBalance(Objects.requireNonNull(BlockChainUtil.getSiteCredentials()).getAddress());
    }
}

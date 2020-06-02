package com.moe.fo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moe.fo.common.ApiResponse;
import com.moe.fo.common.Constant;
import com.moe.fo.contract.FoodOriginContract;
import org.springframework.util.StringUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockChainUtil {
    private static FoodOriginContract contract;
    private static Credentials siteCredentials = null;
    private static Map<String, Credentials> credentialsMap = null;
    private static Web3j web3j = null;
    private static final BigDecimal TEN18 = new BigDecimal("1000000000000000000");
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Credentials> getCredentialsMap() {
        if (credentialsMap == null) {
            credentialsMap = new HashMap<>();
        }
        return credentialsMap;
    }

    public static Credentials getSiteCredentials() {
        if (siteCredentials == null) {
            try {
                siteCredentials = WalletUtils.loadCredentials("cutebunny", "site.json");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return siteCredentials;
    }

    public static String getBalance(String address) {
        try {
            EthGetBalance ethGetBalance = getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigDecimal wei = new BigDecimal(ethGetBalance.getBalance());
            BigDecimal balance = wei.divide(TEN18, 10, BigDecimal.ROUND_UP);
            return balance.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static Web3j getWeb3j() {
        if (web3j == null) {
            web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/47788e47975644b49657a044ad9c5a8a"));
        }
        return web3j;
    }

    public static ApiResponse<String> login(String path, String username, String password) {
        File file = new File(path + username + ".json");
        if (StringUtils.isEmpty(username) || !file.exists()) {
            return ApiResponse.<String>builder().status(1002).msg("用户不存在").build();
        }
        try {
            Credentials credentials = WalletUtils.loadCredentials(password, file);
            String token = MD5.encode(username + ":" + System.currentTimeMillis());
            if (credentials != null) {
                getCredentialsMap().put(token, credentials);
                return ApiResponse.<String>builder().status(0).msg("ok").data(token).build();
            }
            return ApiResponse.<String>builder().status(1001).msg("密码错误").build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ApiResponse.<String>builder().status(1001).msg("密码错误").build();
        }
    }

    public static ApiResponse<String> register(String path, String username, String password) {
        File file = new File(path + username + ".json");
        if (StringUtils.isEmpty(username) || file.exists()) {
            return ApiResponse.<String>builder().status(1003).msg("用户名已存在").build();
        }
        try {
            String fileName = WalletUtils.generateNewWalletFile(password, new File(path), false);
            String token = MD5.encode(username + ":" + System.currentTimeMillis());
            File oldFile = new File(path + fileName);
            oldFile.renameTo(file);
            Credentials credentials = WalletUtils.loadCredentials(password, file);
            if (credentials != null) {
                getCredentialsMap().put(token, credentials);
                return ApiResponse.<String>builder().status(0).msg("ok").data(token).build();
            }
            return ApiResponse.<String>builder().status(1).msg("区块链连接出错").build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ApiResponse.<String>builder().status(1).msg("区块链连接出错").build();
        }
    }

    public static ApiResponse<Void> logout(String token) {
        getCredentialsMap().remove(token);
        return ApiResponse.<Void>builder().status(0).msg("ok").build();
    }

    public static ApiResponse<Map<String, Object>> getUserInfo(String token) {
        Map<String, Object> user = new HashMap<>();
        Credentials credentials = getUser(token);
        if (credentials == null) {
            return ApiResponse.<Map<String, Object>>builder().status(1004).msg("非法的Token").build();
        }
        try {
            List<Type> res = getContract().getUser(credentials.getAddress()).send();
            String name = (String) res.get(0).getValue();
            BigInteger role = (BigInteger) res.get(1).getValue();
            if (StringUtils.isEmpty(name) && role.intValue() == 0) {
                return ApiResponse.<Map<String, Object>>builder().status(2001).msg("用户未设置身份").build();
            }
            user.put("name", name);
            user.put("role", role.intValue());
            return ApiResponse.<Map<String, Object>>builder().status(0).msg("ok").data(user).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<Map<String, Object>>builder().status(1).msg("区块链连接出错").build();
        }
    }

    public static ApiResponse<String> setUserInfo(String token, String name, int role) {
        Credentials credentials = getUser(token);
        if (credentials == null) {
            return ApiResponse.<String>builder().status(1004).msg("非法的Token").build();
        }
        try {
            TransactionReceipt transactionReceipt = getContract().setUser(credentials.getAddress(), name, role).send();
            return ApiResponse.<String>builder().status(0).msg("ok").data(transactionReceipt.getTransactionHash()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<String>builder().status(1).msg("区块链连接出错").build();
        }
    }

    public static ApiResponse<Map<String, Object>> getItemInfo(String itemId) {
        Map<String, Object> item = new HashMap<>();
        try {
            List<Type> res = getContract().getItem(itemId).send();
            String name = (String) res.get(0).getValue();
            if (StringUtils.isEmpty(name)) {
                return ApiResponse.<Map<String, Object>>builder().status(3001).msg("商品不存在").build();
            }
            item.put("name", name);
            String[] actions = {Constant.ACTION_PLANT, Constant.ACTION_CARRIAGE, Constant.ACTION_SELL};
            for (String action : actions) {
                Map<String, Object> op = new HashMap<>();
                List<Type> opRes = getContract().getOps(itemId, action).send();
                String u = (String) opRes.get(0).getValue();
                String d = (String) opRes.get(1).getValue();
                if (!StringUtils.isEmpty(d)) {
                    HashMap data = objectMapper.readValue(d, HashMap.class);
                    op.put("user", u);
                    op.put("data", data);
                    item.put(action, op);
                }
            }
            return ApiResponse.<Map<String, Object>>builder().status(0).msg("ok").data(item).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<Map<String, Object>>builder().status(1).msg("区块链连接出错").build();
        }
    }

    public static ApiResponse<String> setItemInfo(String itemId, String name) {
        try {
            TransactionReceipt transactionReceipt = getContract().addItem(itemId, name).send();
            return ApiResponse.<String>builder().status(0).msg("ok").data(transactionReceipt.getTransactionHash()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<String>builder().status(1).msg("区块链连接出错").build();
        }
    }

    public static ApiResponse<String> addOp(String token, String itemId, String action, String memo) {
        Credentials credentials = getUser(token);
        if (credentials == null) {
            return ApiResponse.<String>builder().status(1004).msg("非法的Token").build();
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("user", credentials.getAddress());
            map.put("action", action);
            map.put("memo", memo);
            String data = objectMapper.writeValueAsString(map);
            TransactionReceipt transactionReceipt = getContract().addOp(itemId, action, data).send();
            return ApiResponse.<String>builder().status(0).msg("ok").data(transactionReceipt.getTransactionHash()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<String>builder().status(1).msg("区块链连接出错").build();
        }
    }

    public static Credentials getUser(String token) {
        return getCredentialsMap().getOrDefault(token, null);
    }

    public static FoodOriginContract getContract() {
        if (contract == null) {
            contract = FoodOriginContract.load("0x3b1eb5026f6f9980aa3857bdfeb63bd8678c4d49", getWeb3j(), getSiteCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        }
        return contract;
    }
}

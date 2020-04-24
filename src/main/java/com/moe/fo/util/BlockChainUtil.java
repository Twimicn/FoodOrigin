package com.moe.fo.util;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;

public class BlockChainUtil {
    private static Credentials siteCredentials = null;
    private static Web3j web3j = null;
    private static final BigDecimal TEN18 = new BigDecimal("1000000000000000000");

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

    private static Web3j getWeb3j() {
        if (web3j == null) {
            web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/47788e47975644b49657a044ad9c5a8a"));
        }
        return web3j;
    }
}

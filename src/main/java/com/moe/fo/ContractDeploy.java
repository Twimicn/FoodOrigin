package com.moe.fo;

import com.moe.fo.contract.FoodOriginContract;
import com.moe.fo.util.BlockChainUtil;
import org.web3j.crypto.Credentials;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

/**
 * 部署只能合约
 */
public class ContractDeploy {
    public static void main(String[] args) {
        try {
            Credentials credentials = BlockChainUtil.getSiteCredentials();
            FoodOriginContract contract = FoodOriginContract.deploy(BlockChainUtil.getWeb3j(), credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();
            String contractAddress = contract.getContractAddress();
            System.out.println(contractAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.heartgo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.errorprone.annotations.Var;
import com.heartgo.model.Transaction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.json.Json;
import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class TransactionService {
  private List<Transaction> transaction;
  private    String transactionTest = "[{\"transactionid\":\"79\",\"transactiondate\":\"1505806838\",\"parentorder\":\"79\",\"suborder\":\"79\",\"payid\":\"79\",\"transtype\":\"79\",\"fromtype\":1,\"fromid\":\"NdhcINGG\",\"totype\":1,\"toid\":\"NdhcINGG\",\"productid\":\"79\",\"productinfo\":\"wegoodi%3\",\"organizationid\":\"pingan\",\"amount\":4,\"price\":9}]";



    // Love Java 8
    public List<Transaction> findByUserName(String organizationid) {

        List<Transaction> results = transaction.stream()
                .filter(x -> x.getOrganizationid().equalsIgnoreCase(organizationid))
                .collect(Collectors.toList());

        return results;

    }

    @PostConstruct
    private void iniDataForTesting() {



        transaction = new ArrayList<Transaction>();



//        Transaction transaction1 = new Transaction("1",
//                "2017/09/01 00:00:00",
//                "2",
//                "3",
//                "4",
//                "5",
//                6,
//                "NdhcINGG",
//                7,
//                "NdhcINGG",
//                "8",
//                "交易信息为11111",
//                "pingan",
//                9,
//                10);
////        Transaction transaction2 = new Transaction("yflow", "password222", "yflow@yahoo.com");
////        Transaction transaction3 = new Transaction("laplap", "password333", "mkyong@yahoo.com");
//        Transaction transaction2 = new Transaction("a",
//                "2017/09/7 04:00:00",
//                "b",
//                "c",
//                "d",
//                "e",
//                11,
//                "NdhcINGG",
//                22,
//                "NdhcINGG",
//                "8",
//                "交易信息为3333",
//                "pingan",
//                9,
//                10);
//        Transaction transaction3 = new Transaction("1",
//                "2017/09/19 02:00:00",
//                "2",
//                "3",
//                "4",
//                "5",
//                6,
//                "NdhcINGG",
//                7,
//                "NdhcINGG",
//                "8",
//                "交易信息为3333",
//                "pingan",
//                9,
//                10);
//        transaction.add(transaction1);
//        transaction.add(transaction2);
//        transaction.add(transaction3);
//
//    }


    }
}

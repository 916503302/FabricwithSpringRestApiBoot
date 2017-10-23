package com.heartgo.model;

import java.util.List;

public class AjaxResponseBody {

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

      public List<User> getResult() {
        return result;
    }

    public void setResult(List<User> result) {
        this.result = result;
    }

    public List<Transaction> getTransactionsResult() {
        return transactionsResult;
    }

    public void setTransactionsResult(List<Transaction> transactionsResult) {
        this.transactionsResult = transactionsResult;
    }

    String msg;
    List<User> result;

    List<Transaction> transactionsResult;






    //getters and setters

}
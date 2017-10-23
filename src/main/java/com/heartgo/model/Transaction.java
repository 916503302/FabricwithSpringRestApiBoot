package com.heartgo.model;

public class Transaction {
    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getTransactiondate() {
        return transactiondate;
    }

    public void setTransactiondate(String transactiondate) {
        this.transactiondate = transactiondate;
    }

    public String getParentorder() {
        return parentorder;
    }

    public void setParentorder(String parentorder) {
        this.parentorder = parentorder;
    }

    public String getSuborder() {
        return suborder;
    }

    public void setSuborder(String suborder) {
        this.suborder = suborder;
    }

    public String getPayid() {
        return payid;
    }

    public void setPayid(String payid) {
        this.payid = payid;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public int getFromtype() {
        return fromtype;
    }

    public void setFromtype(int fromtype) {
        this.fromtype = fromtype;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public int getTotype() {
        return totype;
    }

    public void setTotype(int totype) {
        this.totype = totype;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(String productinfo) {
        this.productinfo = productinfo;
    }

    public String getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(String organizationid) {
        this.organizationid = organizationid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Transaction(String transactionid, String transactiondate, String parentorder, String suborder, String payid, String transtype, int fromtype, String fromid, int totype, String toid, String productid, String productinfo, String organizationid, int amount, int price) {
        this.transactionid = transactionid;
        this.transactiondate = transactiondate;
        this.parentorder = parentorder;
        this.suborder = suborder;
        this.payid = payid;
        this.transtype = transtype;
        this.fromtype = fromtype;
        this.fromid = fromid;
        this.totype = totype;
        this.toid = toid;
        this.productid = productid;
        this.productinfo = productinfo;
        this.organizationid = organizationid;
        this.amount = amount;
        this.price = price;
    }

    String transactionid;
    String transactiondate;
    String parentorder;
    String suborder;
    String payid;
    String transtype;
    int fromtype;
    String fromid;
    int totype;
    String toid;
    String productid;
    String productinfo;
    String organizationid;
    int amount;
    int price;





}

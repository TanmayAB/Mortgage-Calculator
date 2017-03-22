package com.example.mortgagecalculator;

/**
 * Created by Student on 3/17/17.
 */

public class Datafields {

    private String type;
    private String address;
    private String city;
    private String state;
    private String  zip;
    private String  propertyprice;
    private String  downpayment;
    private String  rate;
    private String  terms;
    private String  monthlyPayments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPropertyprice() {
        return propertyprice;
    }

    public void setPropertyprice(String propertyprice) {
        this.propertyprice = propertyprice;
    }

    public String getDownpayment() {
        return downpayment;
    }

    public void setDownpayment(String downpayment) {
        this.downpayment = downpayment;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getMonthlyPayments() {
        return monthlyPayments;
    }

    public void setMonthlyPayments(String monthlyPayments) {
        this.monthlyPayments = monthlyPayments;
    }

    public void setFields(String[] fields){
        type = fields[0];
        address = fields[1];
        city = fields[2];
        state = fields[3];
        zip = fields[4];
        propertyprice = fields[5];
        downpayment = fields[6];
        rate = fields[7];
        terms = fields[8];
        monthlyPayments = fields[9];
    }


    public Datafields getFields()
    {
        return this;
    }

    public String getFullAddress(){
        return address + "," + city + "," + state + "," + zip;
    }
}

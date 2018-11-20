/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kryptag.aggregator;

import com.kryptag.rabbitmqconnector.MessageClasses.LoanResponse;
import java.util.ArrayList;

/**
 *
 * @author florenthaxha
 */
public class AggregatorResponseStore {

    private final String ssn;
    private final int amountOfResps;
    private ArrayList<LoanResponse> loanResps;

    public AggregatorResponseStore(String ssn, int amountOfResps, ArrayList<LoanResponse> loanResps) {
        this.ssn = ssn;
        this.amountOfResps = amountOfResps;
        this.loanResps = loanResps;
    }

    public ArrayList<LoanResponse> getLoanResps() {
        return loanResps;
    }

    public String getSsn() {
        return ssn;
    }

    public int getAmountOfResps() {
        return amountOfResps;
    }

    public void addToList(LoanResponse loanresp) {
        this.loanResps.add(loanresp);
    }

    public Boolean isListReady() {
        return this.loanResps.size() == this.amountOfResps;
    }

    public LoanResponse getBestOffer() {
        LoanResponse bestOffer = null;
        float best = Float.MAX_VALUE;
        for (int i = 0; i < this.loanResps.size(); i++) {
            float newest = this.loanResps.get(i).getInterestRate();
            
            if(newest >= best){
                best = newest;
                bestOffer = this.loanResps.get(i);
            }

        }
        return bestOffer;
    }

}

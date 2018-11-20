/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kryptag.aggregator;

import com.google.gson.Gson;
import com.kryptag.rabbitmqconnector.MessageClasses.LoanResponse;
import com.kryptag.rabbitmqconnector.MessageClasses.RuleMessage;
import com.kryptag.rabbitmqconnector.RMQConnection;
import com.kryptag.rabbitmqconnector.RMQConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author florenthaxha
 */
public class Consumer extends RMQConsumer{
    private final HashMap<String, AggregatorResponseStore> map;
    
    public Consumer(HashMap<String, AggregatorResponseStore> map, ConcurrentLinkedQueue q, RMQConnection rmq) {
        super(q, rmq);
        this.map = map;
    }
    
    
    
    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            doWork();
        }
    }
    
    private void doWork(){
        Gson g = new Gson();
        while(!this.getQueue().isEmpty()){
            String json = this.getQueue().remove().toString();
            try {
                RuleMessage rmsg = g.fromJson(json, RuleMessage.class);
                ArrayList<LoanResponse> list = new ArrayList();
                String key = rmsg.getCmsg().getSsn();
                AggregatorResponseStore ars = new AggregatorResponseStore(key, rmsg.getBankNames().size(), list);
                map.put(key, ars);
            } catch (Exception e) {
            }
            try {
                LoanResponse loanResp = g.fromJson(json, LoanResponse.class);
                AggregatorResponseStore ars = map.get(loanResp.getSsn());
                ars.addToList(loanResp);
                if (ars.isListReady()) {
                    LoanResponse bestOffer = ars.getBestOffer();
                    this.getRmq().sendMessage(g.toJson(bestOffer));
                    map.remove(ars.getSsn());
                }
            } catch (Exception e) {
            }
        } 
    }
    
}

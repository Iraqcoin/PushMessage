/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class ZExpression {
    
    private final String property;
    private final String value;
    private final ZOperator operator;
    private final ZComparisonOperator comparisonOperator;
    
    public ZExpression(String property, String value, ZComparisonOperator compOp){
        this.property = property;
        this.value = value;
        this.operator = ZOperator.AND;
        this.comparisonOperator = compOp;
    }
    
    public ZExpression(String property, List<String> value, ZOperator op){
        this.property = property;
        this.operator = op;
        this.comparisonOperator = ZComparisonOperator.IN;
        this.value = JSONValue.toJSONString(value);
    }
    
    public ZExpression(String property, String value, ZOperator op, ZComparisonOperator compOp){
        this.property = property;
        this.value = value;
        this.operator = op;
        this.comparisonOperator = compOp;
    }
    
    
    public Map<String, String> valuesAsMap(){
        Map<String, String> data = new HashMap<>();
        data.put("property", property);
        data.put("value", value);
        data.put("compOp", comparisonOperator.name());
        data.put("op", operator.name());
        return data;
    }
    
    
    public static ZExpression parseFromJson(Map<String, String> data) throws ParseException{
        String property = data.get("property");
        String value = data.get("value");
        ZOperator op = ZOperator.valueOf(data.get("op"));
        ZComparisonOperator compOp = ZComparisonOperator.valueOf(data.get("compOp"));
        return new ZExpression(property, value, op, compOp);
    }
    
    
    public String valueAsJson(){
        Map<String, String> data = new HashMap<>();
        data.put("property", property);
        data.put("value", value);
        data.put("compOp", comparisonOperator.name());
        data.put("op", operator.name());
        return JSONValue.toJSONString(data);
               
    }
    
    public enum ZOperator {
        AND("AND"), OR("OR");
        
        private final String value;
        private ZOperator(String valueInSQL){
            this.value = valueInSQL;
        }
        
        public String getOperator(){
            return this.value;
        }
    }
    
    public enum ZComparisonOperator {
        EQUAL("="), NOT_EQUAL("!="), LIKE("like"), IN("in");
        
        
        private final String value;
        private ZComparisonOperator(String value) {
            this.value = value;
        }
        
        public String getOperator(){
            return this.value;
        }
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public ZOperator getOperator() {
        return operator;
    }

    public ZComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }
    
}

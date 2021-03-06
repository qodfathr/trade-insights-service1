package com.redhat.demo.engine;

import io.vertx.core.json.JsonObject;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import io.vertx.core.shareddata.LocalMap;

import java.text.DecimalFormat;
public class TradeRecommendationsEngine {

   private SharedData engineData;

   public TradeRecommendationsEngine(SharedData engineData) {
     this.engineData = engineData;
   }
  
   public void nextRecommendation(RoutingContext rc) {
        /* format of results
            {
                "order" : "BUY ACME",
                "detail":
                {
                    "targetPrice" : 10,
                    "numShares" : 1000
                },
                "context":
                {
                    "confidenceScore" : 10,
                    "tradeWindowStart" : "Wed Mar 29 20:21:29 EDT 2017",
                    "tradeWindowEnd" : "Thu Mar 30 01:21:29 EDT 2017"
                }
            } 
        */

        JsonObject recommendation = new JsonObject();
        JsonObject detail = new JsonObject();
        JsonObject context = new JsonObject();

        String pick = determineStockPick();
        
        detail.put("targetPrice", determineTargetPrice(pick));
        detail.put("numShares",determineNumShares());

        context.put("confidenceScore",determineConfidenceScore());
        context.put("tradeWindowStart", determineTradeWindowStartDate().toString());
        context.put("tradeWindowEnd", determineTradeWindowEndDate().toString());

        recommendation.put("order", "BUY " + pick);
        recommendation.put("detail",detail);
        recommendation.put("context",context);

        rc.response()
          .putHeader(HttpHeaders.CONTENT_TYPE,"application/json")
          .end(recommendation.encode());
    }

    private String determineStockPick() {
        // int index = new java.util.Random().nextInt();
        int max = 10;
        int min = 0;
        List<String> options = Arrays.asList(
            "BABO",
            "TODD",
            "ACME",
            "FALK",
            "PETE",
            "BURR",
            "JIM",
            "OCRL",
            "RTH",
            "IMB"
        );
        int index  = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, options.size());
        System.out.println(index);
        System.out.println(options.get(index));
        return options.get(index);
    }
    private int determineNumShares() {
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(50, 5000);
    }

    private double determineTargetPrice() {        
        return 10.85;
    }

    private double determineTargetPrice(String ticker) {        
         LocalMap<String, Double> previousMap = engineData.getLocalMap("previousMap");
         Double oldPrice = previousMap.get(ticker);
         if (oldPrice == null) {
             // oldPrice = 10.85;            
             double random = java.util.concurrent.ThreadLocalRandom.current().nextDouble(10.85,345.24);
             oldPrice = new Double(random);
         }
         DecimalFormat df2 = new DecimalFormat("###.##");
         double increment = java.util.concurrent.ThreadLocalRandom.current().nextDouble(10.85,24.12);
         Double newPrice = oldPrice + increment;
         Double twoDecimalPlaces = new Double(df2.format(newPrice));
         System.out.println("new price " + twoDecimalPlaces);
        
         previousMap.put(ticker, twoDecimalPlaces);

         return twoDecimalPlaces.doubleValue();
    }

   /* results in a runtime error
   Invalid type for shareddata data structure: java.math.BigDecimal

   private double determineTargetPrice(String ticker) {        
       LocalMap<String, BigDecimal> previousMap = engineData.getLocalMap("previousMap");
       BigDecimal oldPrice = previousMap.get(ticker);
       if (oldPrice == null) {
           double random = java.util.concurrent.ThreadLocalRandom.current().nextDouble(10.85,345.24);
           oldPrice = BigDecimal.valueOf(random);
       }
       double increment = java.util.concurrent.ThreadLocalRandom.current().nextDouble(10.85,24.12);
       BigDecimal newPrice = oldPrice.add(BigDecimal.valueOf(increment));
       BigDecimal twoDecimalPrice = newPrice.setScale(2, RoundingMode.HALF_UP);
       System.out.println("newPrice: " + twoDecimalPrice);
       previousMap.put(ticker, twoDecimalPrice);
       return newPrice.doubleValue();
   }
*/

    private int determineConfidenceScore() {
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(5, 10);        
    }

   private Date determineTradeWindowStartDate(){
    LocalDateTime today = LocalDateTime.now();
    return Date.from(today.atZone(ZoneId.systemDefault()).toInstant());
    
   }
   private Date determineTradeWindowEndDate(){
    LocalDateTime tomorrow = LocalDateTime.now().plusHours(3);
    return Date.from(tomorrow.atZone(ZoneId.systemDefault()).toInstant());
   }
}
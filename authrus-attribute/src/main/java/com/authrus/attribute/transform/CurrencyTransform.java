package com.authrus.attribute.transform;

import java.util.Currency;

public class CurrencyTransform implements ObjectTransform<Currency, String> {

   public Currency toObject(String symbol) {
      return Currency.getInstance(symbol);
   }

   public String fromObject(Currency currency) {
      return currency.toString();
   }
}

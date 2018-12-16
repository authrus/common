package com.authrus.attribute.transform;

import java.util.regex.Pattern;
import java.util.Locale;

public class LocaleTransform implements ObjectTransform<Locale, String> {

   private final Pattern pattern;

   public LocaleTransform() {
      this.pattern = Pattern.compile("_");
   }

   public Locale toObject(String locale) {
      String[] list = pattern.split(locale);

      if (list.length < 1) {
         throw new TransformException("Invalid locale " + locale);
      }
      return read(list);
   }

   private Locale read(String[] locale) {
      String[] list = new String[] { "", "", "" };

      for (int i = 0; i < list.length; i++) {
         if (i < locale.length) {
            list[i] = locale[i];
         }
      }
      return new Locale(list[0], list[1], list[2]);
   }

   public String fromObject(Locale locale) {
      return locale.toString();
   }
}

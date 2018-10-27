package com.authrus.common;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationPresentScanner implements ClassPathScanner {

   private final Class<? extends Annotation> annotation;
   private final ClassPathScanner scanner;
   
   public AnnotationPresentScanner(Class<? extends Annotation> annotation, String suffix, String packages) {
      this.scanner = new PatternMatchingScanner(suffix, packages);
      this.annotation = annotation;
   }
   
   @Override
   public Set<Class<?>> scan() {
      return scanner.scan()
            .stream()
            .filter(Objects::nonNull)
            .filter(type -> type.isAnnotationPresent(annotation))
            .collect(Collectors.toSet());
   }
}

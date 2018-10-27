package com.authrus.rest.container;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResourceSet {

   private final Set<Class<?>> types;
   private final Set<String> files;
}

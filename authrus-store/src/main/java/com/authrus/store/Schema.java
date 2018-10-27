package com.authrus.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.zuooh.tuple.grid.Structure;

@Data
@Builder
@AllArgsConstructor
public class Schema<T> {
   
   private final Structure structure;
   private final Class<T> type;
   private final String entity;
   private final String key;
}

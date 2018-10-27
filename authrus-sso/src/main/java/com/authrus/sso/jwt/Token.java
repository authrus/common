package com.authrus.sso.jwt;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Token {

   private final Map<String, String> clains;
   private final String token;
   private final long expiry;
}

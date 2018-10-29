package com.authrus.gateway.deploy;

import java.net.InetSocketAddress;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.zuooh.common.ssl.Certificate;

@Data
@AllArgsConstructor 
public class Destination {
   
   private InetSocketAddress address;
   private Certificate certificate;
   private String name;
}
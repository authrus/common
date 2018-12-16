package com.authrus.transport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;

import javax.net.ServerSocketFactory;

import junit.framework.TestCase;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportChannel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.ssl.Certificate;
import com.authrus.common.ssl.DefaultCertificate;
import com.authrus.common.ssl.KeyStoreReader;
import com.authrus.common.ssl.KeyStoreType;
import com.authrus.common.ssl.SecureCertificate;
import com.authrus.common.ssl.SecureProtocol;
import com.authrus.common.ssl.SecureSocketContext;
import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.trace.TraceAgent;

//keytool -genkey -alias alias -keypass simulator -keystore lig.keystore -storepass simulator
public class SecureTransportBuilderTest extends TestCase {
   
   private static final byte[] CERT = {
      (byte)254, (byte)237, (byte)254, (byte)237, (byte)0, (byte)0, (byte)0, (byte)2, (byte)0, (byte)0, (byte)0, (byte)1, (byte)0, (byte)0, (byte)0, (byte)1, (byte)0, (byte)5, (byte)97, (byte)108, 
      (byte)105, (byte)97, (byte)115, (byte)0, (byte)0, (byte)1, (byte)72, (byte)223, (byte)59, (byte)151, (byte)90, (byte)0, (byte)0, (byte)1, (byte)143, (byte)48, (byte)130, (byte)1, (byte)139, (byte)48, (byte)14, 
      (byte)6, (byte)10, (byte)43, (byte)6, (byte)1, (byte)4, (byte)1, (byte)42, (byte)2, (byte)17, (byte)1, (byte)1, (byte)5, (byte)0, (byte)4, (byte)130, (byte)1, (byte)119, (byte)99, (byte)71, (byte)145, 
      (byte)114, (byte)233, (byte)151, (byte)15, (byte)79, (byte)205, (byte)117, (byte)103, (byte)237, (byte)127, (byte)224, (byte)138, (byte)227, (byte)25, (byte)133, (byte)153, (byte)59, (byte)189, (byte)140, (byte)148, (byte)16, 
      (byte)198, (byte)216, (byte)203, (byte)193, (byte)135, (byte)27, (byte)44, (byte)159, (byte)134, (byte)156, (byte)149, (byte)74, (byte)188, (byte)215, (byte)225, (byte)118, (byte)221, (byte)82, (byte)132, (byte)255, (byte)161, 
      (byte)150, (byte)75, (byte)233, (byte)79, (byte)155, (byte)112, (byte)190, (byte)115, (byte)59, (byte)201, (byte)15, (byte)24, (byte)23, (byte)236, (byte)6, (byte)48, (byte)233, (byte)25, (byte)97, (byte)38, (byte)46, 
      (byte)164, (byte)245, (byte)93, (byte)174, (byte)38, (byte)226, (byte)10, (byte)199, (byte)67, (byte)130, (byte)103, (byte)9, (byte)228, (byte)14, (byte)167, (byte)32, (byte)22, (byte)42, (byte)193, (byte)44, (byte)46, 
      (byte)119, (byte)164, (byte)179, (byte)213, (byte)216, (byte)128, (byte)157, (byte)121, (byte)142, (byte)221, (byte)208, (byte)255, (byte)12, (byte)127, (byte)16, (byte)214, (byte)156, (byte)211, (byte)139, (byte)119, (byte)160, 
      (byte)131, (byte)89, (byte)239, (byte)72, (byte)219, (byte)45, (byte)130, (byte)245, (byte)28, (byte)246, (byte)23, (byte)103, (byte)70, (byte)81, (byte)148, (byte)88, (byte)31, (byte)35, (byte)139, (byte)197, (byte)206, 
      (byte)76, (byte)42, (byte)74, (byte)107, (byte)249, (byte)47, (byte)143, (byte)27, (byte)200, (byte)204, (byte)3, (byte)212, (byte)51, (byte)84, (byte)148, (byte)179, (byte)89, (byte)86, (byte)201, (byte)91, (byte)221, 
      (byte)253, (byte)160, (byte)8, (byte)1, (byte)163, (byte)197, (byte)200, (byte)14, (byte)207, (byte)29, (byte)152, (byte)250, (byte)38, (byte)140, (byte)131, (byte)74, (byte)160, (byte)99, (byte)4, (byte)165, (byte)38, 
      (byte)30, (byte)174, (byte)73, (byte)25, (byte)72, (byte)105, (byte)213, (byte)212, (byte)76, (byte)82, (byte)142, (byte)55, (byte)194, (byte)112, (byte)35, (byte)152, (byte)37, (byte)205, (byte)149, (byte)250, (byte)236, 
      (byte)92, (byte)27, (byte)185, (byte)119, (byte)26, (byte)114, (byte)217, (byte)130, (byte)95, (byte)121, (byte)123, (byte)135, (byte)59, (byte)63, (byte)173, (byte)197, (byte)148, (byte)173, (byte)38, (byte)101, (byte)22, 
      (byte)43, (byte)170, (byte)8, (byte)149, (byte)5, (byte)214, (byte)238, (byte)235, (byte)218, (byte)248, (byte)39, (byte)152, (byte)185, (byte)179, (byte)121, (byte)249, (byte)66, (byte)179, (byte)237, (byte)187, (byte)84, 
      (byte)173, (byte)140, (byte)10, (byte)237, (byte)152, (byte)57, (byte)7, (byte)249, (byte)230, (byte)139, (byte)184, (byte)236, (byte)144, (byte)208, (byte)24, (byte)34, (byte)172, (byte)150, (byte)19, (byte)142, (byte)230, 
      (byte)246, (byte)158, (byte)51, (byte)74, (byte)98, (byte)44, (byte)181, (byte)48, (byte)190, (byte)209, (byte)193, (byte)158, (byte)99, (byte)212, (byte)133, (byte)38, (byte)146, (byte)243, (byte)251, (byte)6, (byte)167, 
      (byte)79, (byte)15, (byte)100, (byte)123, (byte)230, (byte)215, (byte)152, (byte)244, (byte)103, (byte)99, (byte)150, (byte)172, (byte)91, (byte)195, (byte)82, (byte)227, (byte)86, (byte)66, (byte)122, (byte)40, (byte)185, 
      (byte)107, (byte)8, (byte)76, (byte)67, (byte)100, (byte)137, (byte)53, (byte)222, (byte)63, (byte)147, (byte)238, (byte)178, (byte)37, (byte)168, (byte)37, (byte)95, (byte)122, (byte)116, (byte)76, (byte)244, (byte)7, 
      (byte)238, (byte)129, (byte)182, (byte)198, (byte)61, (byte)55, (byte)146, (byte)230, (byte)196, (byte)231, (byte)87, (byte)171, (byte)52, (byte)50, (byte)181, (byte)249, (byte)252, (byte)168, (byte)43, (byte)10, (byte)154, 
      (byte)235, (byte)48, (byte)85, (byte)226, (byte)74, (byte)94, (byte)147, (byte)70, (byte)134, (byte)56, (byte)65, (byte)174, (byte)254, (byte)138, (byte)177, (byte)1, (byte)137, (byte)118, (byte)67, (byte)250, (byte)204, 
      (byte)3, (byte)236, (byte)91, (byte)242, (byte)121, (byte)153, (byte)154, (byte)236, (byte)106, (byte)62, (byte)132, (byte)55, (byte)5, (byte)101, (byte)181, (byte)0, (byte)0, (byte)0, (byte)1, (byte)0, (byte)5, 
      (byte)88, (byte)46, (byte)53, (byte)48, (byte)57, (byte)0, (byte)0, (byte)3, (byte)39, (byte)48, (byte)130, (byte)3, (byte)35, (byte)48, (byte)130, (byte)2, (byte)224, (byte)160, (byte)3, (byte)2, (byte)1, 
      (byte)2, (byte)2, (byte)4, (byte)3, (byte)0, (byte)8, (byte)244, (byte)48, (byte)11, (byte)6, (byte)7, (byte)42, (byte)134, (byte)72, (byte)206, (byte)56, (byte)4, (byte)3, (byte)5, (byte)0, (byte)48, 
      (byte)99, (byte)49, (byte)11, (byte)48, (byte)9, (byte)6, (byte)3, (byte)85, (byte)4, (byte)6, (byte)19, (byte)2, (byte)84, (byte)69, (byte)49, (byte)13, (byte)48, (byte)11, (byte)6, (byte)3, (byte)85, 
      (byte)4, (byte)8, (byte)19, (byte)4, (byte)84, (byte)101, (byte)115, (byte)116, (byte)49, (byte)13, (byte)48, (byte)11, (byte)6, (byte)3, (byte)85, (byte)4, (byte)7, (byte)19, (byte)4, (byte)84, (byte)101, 
      (byte)115, (byte)116, (byte)49, (byte)13, (byte)48, (byte)11, (byte)6, (byte)3, (byte)85, (byte)4, (byte)10, (byte)19, (byte)4, (byte)84, (byte)101, (byte)115, (byte)116, (byte)49, (byte)13, (byte)48, (byte)11, 
      (byte)6, (byte)3, (byte)85, (byte)4, (byte)11, (byte)19, (byte)4, (byte)84, (byte)101, (byte)115, (byte)116, (byte)49, (byte)24, (byte)48, (byte)22, (byte)6, (byte)3, (byte)85, (byte)4, (byte)3, (byte)19, 
      (byte)15, (byte)78, (byte)105, (byte)97, (byte)108, (byte)108, (byte)32, (byte)71, (byte)97, (byte)108, (byte)108, (byte)97, (byte)103, (byte)104, (byte)101, (byte)114, (byte)48, (byte)30, (byte)23, (byte)13, (byte)49, 
      (byte)52, (byte)49, (byte)48, (byte)48, (byte)53, (byte)48, (byte)55, (byte)51, (byte)52, (byte)53, (byte)55, (byte)90, (byte)23, (byte)13, (byte)49, (byte)53, (byte)48, (byte)49, (byte)48, (byte)51, (byte)48, 
      (byte)55, (byte)51, (byte)52, (byte)53, (byte)55, (byte)90, (byte)48, (byte)99, (byte)49, (byte)11, (byte)48, (byte)9, (byte)6, (byte)3, (byte)85, (byte)4, (byte)6, (byte)19, (byte)2, (byte)84, (byte)69, 
      (byte)49, (byte)13, (byte)48, (byte)11, (byte)6, (byte)3, (byte)85, (byte)4, (byte)8, (byte)19, (byte)4, (byte)84, (byte)101, (byte)115, (byte)116, (byte)49, (byte)13, (byte)48, (byte)11, (byte)6, (byte)3, 
      (byte)85, (byte)4, (byte)7, (byte)19, (byte)4, (byte)84, (byte)101, (byte)115, (byte)116, (byte)49, (byte)13, (byte)48, (byte)11, (byte)6, (byte)3, (byte)85, (byte)4, (byte)10, (byte)19, (byte)4, (byte)84, 
      (byte)101, (byte)115, (byte)116, (byte)49, (byte)13, (byte)48, (byte)11, (byte)6, (byte)3, (byte)85, (byte)4, (byte)11, (byte)19, (byte)4, (byte)84, (byte)101, (byte)115, (byte)116, (byte)49, (byte)24, (byte)48, 
      (byte)22, (byte)6, (byte)3, (byte)85, (byte)4, (byte)3, (byte)19, (byte)15, (byte)78, (byte)105, (byte)97, (byte)108, (byte)108, (byte)32, (byte)71, (byte)97, (byte)108, (byte)108, (byte)97, (byte)103, (byte)104, 
      (byte)101, (byte)114, (byte)48, (byte)130, (byte)1, (byte)183, (byte)48, (byte)130, (byte)1, (byte)44, (byte)6, (byte)7, (byte)42, (byte)134, (byte)72, (byte)206, (byte)56, (byte)4, (byte)1, (byte)48, (byte)130, 
      (byte)1, (byte)31, (byte)2, (byte)129, (byte)129, (byte)0, (byte)253, (byte)127, (byte)83, (byte)129, (byte)29, (byte)117, (byte)18, (byte)41, (byte)82, (byte)223, (byte)74, (byte)156, (byte)46, (byte)236, (byte)228, 
      (byte)231, (byte)246, (byte)17, (byte)183, (byte)82, (byte)60, (byte)239, (byte)68, (byte)0, (byte)195, (byte)30, (byte)63, (byte)128, (byte)182, (byte)81, (byte)38, (byte)105, (byte)69, (byte)93, (byte)64, (byte)34, 
      (byte)81, (byte)251, (byte)89, (byte)61, (byte)141, (byte)88, (byte)250, (byte)191, (byte)197, (byte)245, (byte)186, (byte)48, (byte)246, (byte)203, (byte)155, (byte)85, (byte)108, (byte)215, (byte)129, (byte)59, (byte)128, 
      (byte)29, (byte)52, (byte)111, (byte)242, (byte)102, (byte)96, (byte)183, (byte)107, (byte)153, (byte)80, (byte)165, (byte)164, (byte)159, (byte)159, (byte)232, (byte)4, (byte)123, (byte)16, (byte)34, (byte)194, (byte)79, 
      (byte)187, (byte)169, (byte)215, (byte)254, (byte)183, (byte)198, (byte)27, (byte)248, (byte)59, (byte)87, (byte)231, (byte)198, (byte)168, (byte)166, (byte)21, (byte)15, (byte)4, (byte)251, (byte)131, (byte)246, (byte)211, 
      (byte)197, (byte)30, (byte)195, (byte)2, (byte)53, (byte)84, (byte)19, (byte)90, (byte)22, (byte)145, (byte)50, (byte)246, (byte)117, (byte)243, (byte)174, (byte)43, (byte)97, (byte)215, (byte)42, (byte)239, (byte)242, 
      (byte)34, (byte)3, (byte)25, (byte)157, (byte)209, (byte)72, (byte)1, (byte)199, (byte)2, (byte)21, (byte)0, (byte)151, (byte)96, (byte)80, (byte)143, (byte)21, (byte)35, (byte)11, (byte)204, (byte)178, (byte)146, 
      (byte)185, (byte)130, (byte)162, (byte)235, (byte)132, (byte)11, (byte)240, (byte)88, (byte)28, (byte)245, (byte)2, (byte)129, (byte)129, (byte)0, (byte)247, (byte)225, (byte)160, (byte)133, (byte)214, (byte)155, (byte)61, 
      (byte)222, (byte)203, (byte)188, (byte)171, (byte)92, (byte)54, (byte)184, (byte)87, (byte)185, (byte)121, (byte)148, (byte)175, (byte)187, (byte)250, (byte)58, (byte)234, (byte)130, (byte)249, (byte)87, (byte)76, (byte)11, 
      (byte)61, (byte)7, (byte)130, (byte)103, (byte)81, (byte)89, (byte)87, (byte)142, (byte)186, (byte)212, (byte)89, (byte)79, (byte)230, (byte)113, (byte)7, (byte)16, (byte)129, (byte)128, (byte)180, (byte)73, (byte)22, 
      (byte)113, (byte)35, (byte)232, (byte)76, (byte)40, (byte)22, (byte)19, (byte)183, (byte)207, (byte)9, (byte)50, (byte)140, (byte)200, (byte)166, (byte)225, (byte)60, (byte)22, (byte)122, (byte)139, (byte)84, (byte)124, 
      (byte)141, (byte)40, (byte)224, (byte)163, (byte)174, (byte)30, (byte)43, (byte)179, (byte)166, (byte)117, (byte)145, (byte)110, (byte)163, (byte)127, (byte)11, (byte)250, (byte)33, (byte)53, (byte)98, (byte)241, (byte)251, 
      (byte)98, (byte)122, (byte)1, (byte)36, (byte)59, (byte)204, (byte)164, (byte)241, (byte)190, (byte)168, (byte)81, (byte)144, (byte)137, (byte)168, (byte)131, (byte)223, (byte)225, (byte)90, (byte)229, (byte)159, (byte)6, 
      (byte)146, (byte)139, (byte)102, (byte)94, (byte)128, (byte)123, (byte)85, (byte)37, (byte)100, (byte)1, (byte)76, (byte)59, (byte)254, (byte)207, (byte)73, (byte)42, (byte)3, (byte)129, (byte)132, (byte)0, (byte)2, 
      (byte)129, (byte)128, (byte)118, (byte)124, (byte)182, (byte)241, (byte)70, (byte)135, (byte)7, (byte)73, (byte)139, (byte)4, (byte)26, (byte)75, (byte)28, (byte)94, (byte)239, (byte)55, (byte)25, (byte)90, (byte)202, 
      (byte)50, (byte)254, (byte)148, (byte)225, (byte)170, (byte)207, (byte)22, (byte)138, (byte)99, (byte)53, (byte)223, (byte)139, (byte)97, (byte)165, (byte)165, (byte)254, (byte)254, (byte)164, (byte)253, (byte)191, (byte)101, 
      (byte)109, (byte)28, (byte)16, (byte)238, (byte)38, (byte)135, (byte)176, (byte)195, (byte)180, (byte)237, (byte)228, (byte)185, (byte)242, (byte)93, (byte)120, (byte)27, (byte)120, (byte)201, (byte)88, (byte)10, (byte)71, 
      (byte)105, (byte)228, (byte)76, (byte)158, (byte)1, (byte)189, (byte)105, (byte)101, (byte)112, (byte)42, (byte)137, (byte)222, (byte)97, (byte)159, (byte)4, (byte)34, (byte)9, (byte)101, (byte)100, (byte)224, (byte)231, 
      (byte)63, (byte)109, (byte)155, (byte)91, (byte)201, (byte)61, (byte)192, (byte)148, (byte)91, (byte)97, (byte)142, (byte)219, (byte)254, (byte)130, (byte)20, (byte)96, (byte)217, (byte)63, (byte)94, (byte)214, (byte)87, 
      (byte)217, (byte)9, (byte)121, (byte)193, (byte)114, (byte)235, (byte)69, (byte)217, (byte)229, (byte)232, (byte)21, (byte)213, (byte)180, (byte)49, (byte)17, (byte)102, (byte)216, (byte)18, (byte)215, (byte)234, (byte)184, 
      (byte)86, (byte)112, (byte)148, (byte)215, (byte)163, (byte)33, (byte)48, (byte)31, (byte)48, (byte)29, (byte)6, (byte)3, (byte)85, (byte)29, (byte)14, (byte)4, (byte)22, (byte)4, (byte)20, (byte)17, (byte)172, 
      (byte)107, (byte)54, (byte)7, (byte)44, (byte)208, (byte)128, (byte)174, (byte)91, (byte)116, (byte)26, (byte)171, (byte)254, (byte)203, (byte)174, (byte)46, (byte)206, (byte)34, (byte)88, (byte)48, (byte)11, (byte)6, 
      (byte)7, (byte)42, (byte)134, (byte)72, (byte)206, (byte)56, (byte)4, (byte)3, (byte)5, (byte)0, (byte)3, (byte)48, (byte)0, (byte)48, (byte)45, (byte)2, (byte)21, (byte)0, (byte)129, (byte)155, (byte)176, 
      (byte)122, (byte)136, (byte)117, (byte)65, (byte)45, (byte)233, (byte)30, (byte)147, (byte)102, (byte)112, (byte)45, (byte)83, (byte)141, (byte)169, (byte)211, (byte)9, (byte)38, (byte)2, (byte)20, (byte)46, (byte)198, 
      (byte)107, (byte)55, (byte)250, (byte)175, (byte)116, (byte)169, (byte)30, (byte)204, (byte)185, (byte)158, (byte)63, (byte)254, (byte)44, (byte)68, (byte)33, (byte)195, (byte)211, (byte)145, (byte)184, (byte)150, (byte)81, 
      (byte)171, (byte)184, (byte)243, (byte)58, (byte)3, (byte)238, (byte)46, (byte)243, (byte)73, (byte)253, (byte)51, (byte)54, (byte)193, (byte)11, (byte)239, (byte)192, (byte)176, };
     

   public void sstestGoogle() throws Exception {
      Certificate certificate = new DefaultCertificate();
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "www.google.com", 443);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);      
      SecureTransportBuilder transportBuilder = new SecureTransportBuilder(builder, certificate, reactor);
      Transport transport = transportBuilder.connect();
      TransportChannel channel = new TransportChannel(transport);
      ByteCursor cursor = channel.getCursor();
      ByteWriter sender = channel.getWriter();
      sender.write(
            ("GET / HTTP/1.1\r\n" +      
             "Host: wwww.google.com\r\n"+
             "Server: Test/1.0\r\n"+
             "Connection: close\r\n\r\n").getBytes());
      sender.flush();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      while(cursor.isOpen()) {
         if(cursor.isReady()){
            byte[] octet = new byte[1];
            int count = cursor.read(octet);
            
            if(count == 1){
               out.write(octet[0]);
            }
         }
      }
      System.err.println(out.toString());
   }
   
   public void testTransport() throws Exception {
      File tempFile = File.createTempFile("test.ssl", "jks");
      FileOutputStream out = new FileOutputStream(tempFile);
      out.write(CERT);
      out.close();
      KeyStoreReader reader = new KeyStoreReader(KeyStoreType.JKS, tempFile, "simulator", "simulator");
      SecureSocketContext socketContext = new SecureSocketContext(reader, SecureProtocol.TLS);
      ServerSocketFactory factory = socketContext.getServerSocketFactory();
      final ServerSocket serverSocket = factory.createServerSocket(23352);
      Certificate certificate = new SecureCertificate(socketContext); 
      //Certificate certificate = new DefaultCertificate();
      //SSLContext context = certificate.getContext();
      //ServerSocketFactory factory = context.getServerSocketFactory();
      //final ServerSocket serverSocket = factory.createServerSocket(23352);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", 23352);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);      
      SecureTransportBuilder transportBuilder = new SecureTransportBuilder(builder, certificate, reactor);
      final CountDownLatch done = new CountDownLatch(1);      
      Transport transport = transportBuilder.connect();
      TransportChannel channel = new TransportChannel(transport);
      ByteCursor cursor = channel.getCursor();      
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      done.await();
      while(cursor.isOpen()) {
         try {
            if(cursor.isReady()){
               byte[] octet = new byte[1];
               int count = cursor.read(octet);
               
               if(count == 1){
                  buffer.write(octet[0]);
               }
            }
         } catch(Exception e){
            //e.printStackTrace(); 
            break;
         }
      }
      String text = buffer.toString();
      assertEquals(text, "Hello World!");
      System.err.println(buffer.toString());
   }

   public static void main(String[] list) throws Exception{
      String name = null;
      
      if(list.length == 0){
         name = "C:\\Work\\development\\bitbucket.org\\infra\\common\\lig.keystore";         
      } else {
         name = list[0];
      }
      File file = new File(name);
      FileInputStream in = new FileInputStream(file);
      StringBuilder builder = new StringBuilder("private static final byte[] CERT = {\r\n");
      int count = 0;
      int width = 0;
      
      while((count = in.read())!=-1){
         if(width++ == 20) {
            builder.append("\r\n");
            width = 0;
         }
         builder.append("(byte)");
         builder.append(count);
         builder.append(", ");
      }
      builder.append("};\r\n");
      System.err.println(builder);
   }
}

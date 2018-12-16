package com.authrus.common.socket.throttle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.HostAddress;

public class ThrottleConnection implements Connection {

   private final ThrottleAlarm throttleAlarm;
   private final Throttle readThrottle;
   private final Throttle writeThrottle;
   private final Connection connection;

   public ThrottleConnection(ThrottleMonitor throttleMonitor, ThrottleAlarm throttleAlarm, Connection connection) {
      this.readThrottle = throttleMonitor.getReadThrottle();
      this.writeThrottle = throttleMonitor.getWriteThrottle();
      this.throttleAlarm = throttleAlarm;
      this.connection = connection;
   }

   @Override
   public HostAddress getRemoteAddress() {
      return connection.getRemoteAddress();
   }

   @Override
   public HostAddress getLocalAddress() {
      return connection.getLocalAddress();
   }

   @Override
   public InputStream getInputStream() throws IOException {
      InputStream in = connection.getInputStream();

      if (in == null) {
         throw new IOException("Coult not acquire input stream");
      }
      return new ThrottleInputStream(in);
   }

   @Override
   public OutputStream getOutputStream() throws IOException {
      OutputStream out = connection.getOutputStream();

      if (out == null) {
         throw new IOException("Coult not acquire input stream");
      }
      return new ThrottleOutputStream(out);
   }

   @Override
   public boolean isReadFailure() {
      return connection.isReadFailure();
   }

   @Override
   public boolean isWriteFailure() {
      return connection.isWriteFailure();
   }

   @Override
   public boolean isConnected() {
      return connection.isConnected();
   }

   @Override
   public boolean close() {
      return connection.close();
   }

   private class ThrottleInputStream extends InputStream {

      private ThrottleStatus previous;
      private InputStream in;
      private long count;

      public ThrottleInputStream(InputStream in) {
         this.previous = ThrottleStatus.OFF;
         this.in = in;
      }

      @Override
      public int read() throws IOException {
         int octet = in.read();

         try {
            if (octet > 0) {
               ThrottleResult result = readThrottle.update(1);
               ThrottleStatus status = result.getResultStatus();
               long percentage = result.getTotalPercentageUsed();
               long alert = result.getAlertPercentage();
               long delay = result.getThrottleDelay();

               if (status != previous || percentage > alert) {
                  ThrottleEvent event = new ThrottleEvent(result, connection, count + 1, 1);

                  if (throttleAlarm != null) {
                     throttleAlarm.raiseInputAlarm(event);
                  }
               }
               if (delay > 0) {
                  Thread.sleep(delay);
               }
               previous = status;
               count++;
            }
         } catch (InterruptedException e) {
            throw new IllegalThreadStateException("Thread has been interrupted");
         }
         return octet;

      }

      @Override
      public int read(byte[] buf, int off, int len) throws IOException {
         int read = in.read(buf, off, len);

         try {
            if (read > 0) {
               ThrottleResult result = readThrottle.update(read);
               ThrottleStatus status = result.getResultStatus();
               long percentage = result.getTotalPercentageUsed();
               long alert = result.getAlertPercentage();
               long delay = result.getThrottleDelay();

               if (status != previous || percentage > alert) {
                  ThrottleEvent event = new ThrottleEvent(result, connection, count + read, read);

                  if (throttleAlarm != null) {
                     throttleAlarm.raiseInputAlarm(event);
                  }
               }
               if (delay > 0) {
                  Thread.sleep(delay);
               }
               previous = status;
               count += read;
            }
         } catch (InterruptedException e) {
            throw new IllegalThreadStateException("Thread has been interrupted");
         }
         return read;
      }

      @Override
      public long skip(long skip) throws IOException {
         long skipped = in.skip(skip);

         try {
            if (skipped > 0) {
               ThrottleResult result = readThrottle.update(skipped);
               ThrottleStatus status = result.getResultStatus();
               long percentage = result.getTotalPercentageUsed();
               long alert = result.getAlertPercentage();
               long delay = result.getThrottleDelay();

               if (status != previous || percentage > alert) {
                  ThrottleEvent event = new ThrottleEvent(result, connection, count + skipped, skipped);

                  if (throttleAlarm != null) {
                     throttleAlarm.raiseInputAlarm(event);
                  }
               }
               if (delay > 0) {
                  Thread.sleep(delay);
               }
               previous = status;
               count += skipped;
            }
         } catch (InterruptedException e) {
            throw new IllegalThreadStateException("Thread has been interrupted");
         }
         return skipped;
      }

      @Override
      public void close() throws IOException {
         in.close();
      }
   }

   private class ThrottleOutputStream extends OutputStream {

      private ThrottleStatus previous;
      private OutputStream out;
      private long count;

      public ThrottleOutputStream(OutputStream out) {
         this.previous = ThrottleStatus.OFF;
         this.out = out;
      }

      @Override
      public void write(int octet) throws IOException {
         out.write(octet);

         try {
            ThrottleResult result = writeThrottle.update(1);
            ThrottleStatus status = result.getResultStatus();
            long percentage = result.getTotalPercentageUsed();
            long alert = result.getAlertPercentage();
            long delay = result.getThrottleDelay();

            if (status != previous || percentage > alert) {
               ThrottleEvent event = new ThrottleEvent(result, connection, count + 1, 1);

               if (throttleAlarm != null) {
                  throttleAlarm.raiseOutputAlarm(event);
               }
            }
            if (delay > 0) {
               Thread.sleep(delay);
            }
            previous = status;
            count++;
         } catch (InterruptedException e) {
            throw new IllegalThreadStateException("Thread has been interrupted");
         }
      }

      @Override
      public void write(byte[] buf, int off, int len) throws IOException {
         out.write(buf, off, len);

         try {
            if (len > 0) {
               ThrottleResult result = writeThrottle.update(len);
               ThrottleStatus status = result.getResultStatus();
               long percentage = result.getTotalPercentageUsed();
               long alert = result.getAlertPercentage();
               long delay = result.getThrottleDelay();

               if (status != previous || percentage > alert) {
                  ThrottleEvent event = new ThrottleEvent(result, connection, count + len, len);

                  if (throttleAlarm != null) {
                     throttleAlarm.raiseOutputAlarm(event);
                  }
               }
               if (delay > 0) {
                  Thread.sleep(delay);
               }
               previous = status;
               count += len;
            }
         } catch (InterruptedException e) {
            throw new IllegalThreadStateException("Thread has been interrupted");
         }
      }

      @Override
      public void flush() throws IOException {
         out.flush();
      }

      @Override
      public void close() throws IOException {
         out.close();
      }
   }
}

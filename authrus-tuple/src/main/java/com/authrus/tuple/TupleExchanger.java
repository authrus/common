package com.authrus.tuple;

import com.authrus.common.thread.ThreadExchanger;
import com.authrus.common.thread.ThreadPoolFactory;

public class TupleExchanger implements TupleListener {

   private final ThreadPoolFactory factory;
   private final ThreadExchanger exchanger;
   private final TupleListener listener;
   private final int threads;

   public TupleExchanger(TupleListener listener, int threads) {
      this(listener, threads, 10000);
   }

   public TupleExchanger(TupleListener listener, int threads, long wait) {
      this.factory = new ThreadPoolFactory(TupleExchanger.class);
      this.exchanger = new ThreadExchanger(factory, threads, wait);
      this.listener = listener;
      this.threads = threads;
   }

   @Override
   public void onUpdate(Tuple tuple) {
      Call call = new Call(CallType.UPDATE, tuple);

      if (threads > 0) {
         exchanger.execute(call);
      } else {
         call.run();
      }
   }

   @Override
   public void onException(Exception cause) {
      Call call = new Call(CallType.EXCEPTION, null, cause);

      if (threads > 0) {
         exchanger.execute(call);
      } else {
         call.run();
      }
   }

   @Override
   public void onHeartbeat() {
      Call call = new Call(CallType.HEARTBEAT);

      if (threads > 0) {
         exchanger.execute(call);
      } else {
         call.run();
      }
   }

   @Override
   public void onReset() {
      Call call = new Call(CallType.RESET);

      if (threads > 0) {
         exchanger.execute(call);
      } else {
         call.run();
      }
   }

   private enum CallType {
      UPDATE {
         @Override
         public void call(TupleListener listener, Tuple tuple, Exception cause) {
            listener.onUpdate(tuple);
         }
      },
      EXCEPTION {
         @Override
         public void call(TupleListener listener, Tuple tuple, Exception cause) {
            listener.onException(cause);
         }
      },
      HEARTBEAT {
         @Override
         public void call(TupleListener listener, Tuple tuple, Exception cause) {
            listener.onHeartbeat();
         }
      },
      RESET {
         @Override
         public void call(TupleListener listener, Tuple tuple, Exception cause) {
            listener.onReset();
         }
      };

      public abstract void call(TupleListener listener, Tuple tuple, Exception cause);
   }

   private class Call implements Runnable {

      private final Exception cause;
      private final CallType type;
      private final Tuple tuple;

      public Call(CallType type) {
         this(type, null);
      }

      public Call(CallType type, Tuple tuple) {
         this(type, tuple, null);
      }

      public Call(CallType type, Tuple tuple, Exception cause) {
         this.tuple = tuple;
         this.cause = cause;
         this.type = type;
      }

      public void run() {
         type.call(listener, tuple, cause);
      }
   }
}

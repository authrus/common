package com.authrus.tuple;

public final class DynamicBindingPerf
{
    private static final int ITERATIONS = 50 * 1000 * 1000;
 
    public static void main(final String[] args)
        throws Exception
    {
        final Operation[] operations = new Operation[4];
        int index = 0;
        operations[index++] = new StepIncOperation();
        operations[index++] = new StepDecOperation();
        operations[index++] = new IncOperation();
        operations[index++] = new DecOperation();
 
        int value = 777;
        for (int i = 0; i < 3; i++)
        {
            System.out.println("*** Run each method in turn: loop " + i);
 
            for (final Operation operation : operations)
            {
                System.out.println(operation.getClass().getName()); 
                value = runTests(operation, value);
            }
        } 
 
        System.out.println("value = " + value);
    } 
 
    private static int runTests(final Operation operation, int value)
    {
        for (int i = 0; i < 10; i++)
        {
            final long start = System.nanoTime();
 
            value += opRun(operation, value);
 
            final long duration = System.nanoTime() - start;
            final long opsPerSec = 
                (ITERATIONS * 1000L * 1000L * 1000L) / duration;
            System.out.printf("    %,d ops/sec\n", opsPerSec);
        }
 
        return value;
    } 
 
    private static int opRun(final Operation operation, int value)
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            value += operation.map(value);
        } 
 
        return value;
    } 
    

   public static interface Operation
   {
       int map(int value);
   }
    
   public static class IncOperation implements Operation
   {
       public int map(final int value)
       {
           return value + 1;
       }
   }
    
   public static class DecOperation implements Operation
   {
       public int map(final int value)
       {
           return value - 1;
       }
   }
    
   public static class StepIncOperation implements Operation
   {
       public int map(final int value)
       {
           return value + 7;
       }
   }
    
   public static class StepDecOperation implements Operation
   {
       public int map(final int value)
       {
           return value - 3;
       }
   }
 
}

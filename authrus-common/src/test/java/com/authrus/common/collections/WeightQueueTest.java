package com.authrus.common.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class WeightQueueTest {

   @Test
   public void testBestFirst() {
      WeightQueue<Score> bestScores = new GreatestFirstQueue<Score>();

      bestScores.offer(new Score(10));
      bestScores.offer(new Score(5));
      bestScores.offer(new Score(70));
      bestScores.offer(new Score(100));
      bestScores.offer(new Score(-10));
      bestScores.offer(new Score(80));

      assertFalse(bestScores.isEmpty());
      assertEquals(bestScores.size(), 6);
      assertEquals(bestScores.poll().getScore(), 100);
      assertEquals(bestScores.poll().getScore(), 80);
      assertEquals(bestScores.poll().getScore(), 70);
      assertEquals(bestScores.poll().getScore(), 10);
      assertEquals(bestScores.poll().getScore(), 5);
      assertEquals(bestScores.poll().getScore(), -10);
   }

   public void testLeastFirst() {
      WeightQueue<Score> bestScores = new LeastFirstQueue<Score>();

      bestScores.offer(new Score(10));
      bestScores.offer(new Score(5));
      bestScores.offer(new Score(70));
      bestScores.offer(new Score(100));
      bestScores.offer(new Score(-10));
      bestScores.offer(new Score(80));

      assertFalse(bestScores.isEmpty());
      assertEquals(bestScores.size(), 6);
      assertEquals(bestScores.poll().getScore(), -10);
      assertEquals(bestScores.poll().getScore(), 5);
      assertEquals(bestScores.poll().getScore(), 10);
      assertEquals(bestScores.poll().getScore(), 70);
      assertEquals(bestScores.poll().getScore(), 80);
      assertEquals(bestScores.poll().getScore(), 100);
   }

   public static class Score implements Weight {

      private final long score;

      private Score(int score) {
         this.score = score;
      }

      public long getScore() {
         return score;
      }

      public long getWeight() {
         return score;
      }
   }

}

package com.authrus.common.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.authrus.common.time.SampleAverager;
import com.authrus.common.time.StopWatch;

public class StopWatchTest {

   @Test
   public void testAverage() {
      SampleAverager sampler = new SampleAverager();
      sampler.sample(1);
      sampler.sample(1);
      sampler.sample(1);
      sampler.sample(1);
      assertEquals(sampler.count(), 4);
      assertEquals(sampler.maximum(), 1);
      assertEquals(sampler.minimum(), 1);
      assertEquals(sampler.average(), 1);

      sampler = new SampleAverager();
      sampler.sample(1);
      sampler.sample(2);
      sampler.sample(3);
      sampler.sample(4);
      assertEquals(sampler.count(), 4);
      assertEquals(sampler.maximum(), 4);
      assertEquals(sampler.minimum(), 1);
      assertEquals(sampler.average(), 2);

      sampler = new SampleAverager();
      sampler.sample(2);
      sampler.sample(4);
      sampler.sample(5);
      sampler.sample(6);
      sampler.sample(21);
      sampler.sample(4);
      sampler.sample(52);
      sampler.sample(66);
      assertEquals(sampler.count(), 8);
      assertEquals(sampler.maximum(), 66);
      assertEquals(sampler.minimum(), 2);
      assertEquals(sampler.average(), 20);
   }

   @Test
   public void testStartStop() {
      StopWatch stopWatch = new StopWatch();
      boolean failure = false;

      try {
         stopWatch.stop();
      } catch (Exception e) {
         failure = true;
      }
      assertTrue("Stop watch should not be stoppable before it has started", failure);
      failure = false;

      try {
         stopWatch.start();
      } catch (Exception e) {
         failure = true;
      }
      assertFalse("Stop should be startable without issue", failure);
      failure = false;

      try {
         stopWatch.start();
      } catch (Exception e) {
         failure = true;
      }
      assertTrue("Should not be able to start the stop watch twice", failure);
      failure = false;

      try {
         stopWatch.stop();
      } catch (Exception e) {
         failure = true;
      }
      assertFalse("Stop should be allowed after start", failure);

   }

}

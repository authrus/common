package com.authrus.tuple.grid;

import junit.framework.TestCase;

import com.authrus.predicate.Any;
import com.authrus.predicate.Predicate;
import com.authrus.predicate.PredicateParser;
import com.authrus.tuple.grid.Cell;
import com.authrus.tuple.grid.ColumnAllocator;
import com.authrus.tuple.grid.Row;
import com.authrus.tuple.grid.RowMatcher;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.grid.Version;

public class RowMatcherTest extends TestCase {

   public void testRowMatcherWithIntegerAndLong() {
      String[] key = new String[] { "productId" };
      String[] constants = new String[] { "productId", "company"};
      Structure structure = new Structure(key, constants);
      RowMatcher matcher = new RowMatcher(structure);
      ColumnAllocator allocator = new ColumnAllocator(structure);

      allocator.getColumn("productId");
      allocator.getColumn("company");
      allocator.getColumn("side");
      allocator.getColumn("priceType");
      allocator.getColumn("productType");

      Version version = new Version(1);
      Cell[] cells = new Cell[] { 
            new Cell("productId", 10234, version), 
            new Cell("company", "JPM", version), 
            new Cell("side", 1, version),
            new Cell("priceType", 2, version), 
            new Cell("productType", "AUD FRN", version)
      };
      
      Row row = new Row(allocator, version, cells, "10234", 0, 0);
      Predicate any = new Any();
      
      assertTrue(matcher.match(any, row, false));
      
      PredicateParser selectProduct = new PredicateParser("productId == 10234 && side == 1");
      
      assertTrue(matcher.match(selectProduct, row, false));
   }
   
   public void testRowMatcher() {
      String[] constants = new String[] { "ProductIdentifier", "Company", "ProductType" };
      Structure structure = new Structure(constants);
      RowMatcher matcher = new RowMatcher(structure);
      ColumnAllocator allocator = new ColumnAllocator(structure);

      allocator.getColumn("ProductIdentifier");
      allocator.getColumn("Company");
      allocator.getColumn("ContactEmail");
      allocator.getColumn("ProductType");
      allocator.getColumn("BenchmarkProductIdentifier");

      Version version = new Version(1);
      Cell[] cells = new Cell[] { new Cell("ProductIdentifier", "AU3FN000789", version), new Cell("Company", "JPM", version), new Cell("ContactEmail", "glen.woodward@anz.com", version),
            new Cell("ProductType", "FloatingRateNote", version), new Cell("BenchmarkProductIdentifier", "TYB Jun", version), new Cell("BidYield", "2.3", version),
            new Cell("OfferYield", "1.82", version)

      };
      Row row = new Row(allocator, version, cells, "AU3FN000789.JPM", 0, 0);
      Predicate any = new Any();

      assertTrue("Should match the any predicate", matcher.match(any, row));

      Predicate matchFloater = new PredicateParser("ProductType == 'FloatingRateNote'");

      assertTrue("Should match the FloatingRateNote", matcher.match(matchFloater, row));

      Predicate matchCompany = new PredicateParser("Company == 'JPM'");

      assertTrue("Should match the company JPM", matcher.match(matchCompany, row));

      Predicate matchIncorrectCompany = new PredicateParser("Company == 'RBS'");

      assertFalse("Should match the company JPM", matcher.match(matchIncorrectCompany, row));

      Predicate matchCorrectCompanyAndCorrectProductType = new PredicateParser("Company == 'RBS' && ProductType == FloatingRateNote");

      assertFalse("Should match the company JPM and product type FloatingRateNote", matcher.match(matchCorrectCompanyAndCorrectProductType, row));

      boolean failure = false;

      try {
         Predicate referencingNonConstant = new PredicateParser("BidYield > 2.3");

         matcher.match(referencingNonConstant, row);
      } catch (Exception e) {
         e.printStackTrace();
         failure = true;
      }
      assertTrue("Should have failed as it was referencing non constant BidYield", failure);
   }

}

/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package at.roesel.oadataprocessor.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class JournalTest {

    private Journal journal;

    @BeforeEach
    public void setUp() {
        journal = new Journal();
    }

    @Test
    public void testAddVar_EmptyVariableSet() {
        JournalVar var = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        boolean result = journal.addVar(var);

        assertTrue(result);
        assertEquals(1, journal.getVariable().size());
    }

    @Test
    public void testAddVar_NonOverlappingDateRange() {
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 1", LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 31));

        journal.addVar(var1);
        boolean result = journal.addVar(var2);

        assertTrue(result);
        assertEquals(2, journal.getVariable().size()); // Non-overlapping, both should exist
    }

    @Test
    public void testAddVar_AdjacentDateRange() {
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 1", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28));

        journal.addVar(var1);
        boolean result = journal.addVar(var2);

        assertTrue(result);
        assertEquals(1, journal.getVariable().size()); // Should merge into a single entry

        JournalVar mergedVar = journal.getVariable().iterator().next();
        assertEquals(LocalDate.of(2023, 1, 1), mergedVar.getStartDate());
        assertEquals(LocalDate.of(2023, 2, 28), mergedVar.getEndDate());
    }

    @Test
    public void testAddVar_OverlappingDateRange() {
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 15), LocalDate.of(2023, 2, 15));

        journal.addVar(var1);
        boolean result = journal.addVar(var2);

        assertTrue(result);
        assertEquals(1, journal.getVariable().size()); // Should merge into a single entry

        JournalVar mergedVar = journal.getVariable().iterator().next();
        assertEquals(LocalDate.of(2023, 1, 1), mergedVar.getStartDate());
        assertEquals(LocalDate.of(2023, 2, 15), mergedVar.getEndDate());
    }

    @Test
    public void testAddVar_DifferentPublisherId() {
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 2", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));

        journal.addVar(var1);
        boolean result = journal.addVar(var2);

        assertTrue(result);
        assertEquals(2, journal.getVariable().size()); // Should not merge due to different publisherId
    }

    @Test
    public void testAddVar_DifferentName() {
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 2", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));

        journal.addVar(var1);
        boolean result = journal.addVar(var2);

        assertTrue(result);
        assertEquals(2, journal.getVariable().size()); // Should not merge due to different name
    }

    @Test
    public void testPublisherId_NoVariables() {
        // Test when variable set is empty
        String publisherId = journal.publisherId(LocalDate.of(2023, 1, 1));
        assertNull(publisherId);  // Should return null when no variables exist
    }

    @Test
    public void testPublisherId_SingleVariable() {
        // Test with a single JournalVar in the variable set
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        journal.addVar(var1);

        String publisherId = journal.publisherId(LocalDate.of(2023, 1, 15));
        assertEquals("Publisher 1", publisherId);  // Should return the publisher of var1
    }

    @Test
    public void testPublisherId_ExactDateMatch() {
        // Test when the date matches exactly with a JournalVar
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 2", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28));
        journal.addVar(var1);
        journal.addVar(var2);

        String publisherId = journal.publisherId(LocalDate.of(2023, 2, 1));
        assertEquals("Publisher 2", publisherId);  // Should return "Publisher 2"
    }

    @Test
    public void testPublisherId_OverlappingDates() {
        // Test when multiple JournalVars have overlapping date ranges
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 2", LocalDate.of(2023, 1, 15), LocalDate.of(2023, 2, 15));
        journal.addVar(var1);
        journal.addVar(var2);

        String publisherId = journal.publisherId(LocalDate.of(2023, 1, 20));
        assertEquals("Publisher 2", publisherId);  // Should return "Publisher 2" (latest start date)
    }

    @Test
    public void testPublisherId_AdjacentDateRanges() {
        // Test when JournalVars have adjacent date ranges
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 2", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28));
        journal.addVar(var1);
        journal.addVar(var2);

        String publisherId = journal.publisherId(LocalDate.of(2023, 2, 15));
        assertEquals("Publisher 2", publisherId);  // Should return "Publisher 2" (within range)
    }

    @Test
    public void testPublisherId_OutOfRangeDate() {
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 2", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28));
        journal.addVar(var1);
        journal.addVar(var2);

        // Provide a date that is outside all defined date ranges
        String publisherId = journal.publisherId(LocalDate.of(2022, 12, 31));

        // Since the date is outside all ranges, we expect the fallback publisher (Publisher 2, latest start date)
        assertEquals("Publisher 2", publisherId);  // Should return the publisher with the latest start date
    }

    @Test
    public void testPublisherId_LatestStartDateFallback() {
        // Test fallback to latest start date when no date matches
        JournalVar var1 = createJournalVar("1", "Journal 1", "Publisher 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        JournalVar var2 = createJournalVar("2", "Journal 1", "Publisher 2", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28));
        journal.addVar(var1);
        journal.addVar(var2);

        // Provide a date that doesn't fall within any range, expecting fallback to latest start date (Publisher 2)
        String publisherId = journal.publisherId(LocalDate.of(2023, 3, 1));
        assertEquals("Publisher 2", publisherId);  // Should return "Publisher 2" (latest start date)
    }


    // Helper method to create JournalVar instances
    private JournalVar createJournalVar(String id, String name, String publisherId, LocalDate startdate, LocalDate enddate) {
        JournalVar var = new JournalVar();
        var.setId(id);
        var.setName(name);
        var.setPublisherId(publisherId);
        var.setWikiPublisherId("w_" + publisherId);
        var.setStartDate(startdate);
        var.setEndDate(enddate);
        var.setCreated(System.currentTimeMillis());
        var.setUpdated(System.currentTimeMillis());
        return var;
    }
}

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

package at.roesel.oadataprocessor.services.wikidata;

import at.roesel.oadataprocessor.model.Journal;
import at.roesel.oadataprocessor.model.JournalVar;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static at.roesel.common.StringSupport.str2array;

public class JournalUpdater {

    private final Logger logger = LoggerFactory.getLogger(JournalUpdater.class);

    public boolean updateJournal(Journal journal, Journal newJournal) {

        boolean changed = false;
        String name = journal.getName();
        if (!Objects.equals(journal.getName(), newJournal.getName())) {
            changed = true;
            logger.debug(String.format("name is different: %s -> %s", name, newJournal.getName()));
            journal.setName(newJournal.getName());
        }

        List<String> issns = journal.issnsAsList();
        List<String> newIssns = newJournal.issnsAsList();
        if (!CollectionUtils.isEqualCollection(issns, newIssns)) {
            changed = true;
            logger.debug(String.format("issn is different: %s -> %s", journal.getIssn(), newJournal.getIssn()));
            journal.setIssn(newJournal.getIssn());
        }

        String issnl = journal.getIssnl();
        if (!Objects.equals(journal.getIssnl(), newJournal.getIssnl())) {
            changed = true;
            journal.setIssnl(newJournal.getIssnl());
            logger.debug(String.format("issnl is different: %s -> %s", issnl, newJournal.getIssnl()));
        }

        String title = journal.getTitle();
        if (!Objects.equals(journal.getTitle(), newJournal.getTitle())) {
            changed = true;
            journal.setTitle(newJournal.getTitle());
            logger.debug(String.format("title is different: %s -> %s", title, newJournal.getTitle()));
        }

        // multiple values
        if (!areMultipleValuesEqual(journal.getWikiInstanceOf(), newJournal.getWikiInstanceOf())) {
            changed = true;
            logger.debug(String.format("wikiInstanceOf is different: %s -> %s", journal.getWikiInstanceOf(), newJournal.getWikiInstanceOf()));
            journal.setWikiInstanceOf(newJournal.getWikiInstanceOf());
        }

        // publisher
        if (!Objects.equals(journal.getWikiPublisherId(), newJournal.getWikiPublisherId())) {
            changed = true;
            logger.debug(String.format("publisher is different: %s -> %s", journal.getWikiPublisherId(), newJournal.getWikiPublisherId()));
            journal.setWikiPublisherId(newJournal.getWikiPublisherId());
        }

        // multiple parent publishers
        // multiple parent publishers
        if (updateVariable(journal, newJournal)) {
            changed = true;
        }

        // set last Update from Wikidata
        journal.setUpdatedWikidata(newJournal.getUpdatedWikidata());

        if (changed) {
            logger.debug(String.format("Updated journal id = %s, wikidataId = %s", journal.getId(), journal.getWikiId()));
        }

        return changed;
    }

    public boolean updateVariable(Journal journal, Journal newJournal) {
        boolean changed = false;
        Map<String, JournalVar> existingMap = new HashMap<>();
        // Create a map with a composite key: "wikiPublisherId|startDate"
        for (JournalVar var : journal.getVariable()) {
            String key = var.getWikiPublisherId() + "|" + var.getStartDate();
            existingMap.put(key, var);
        }

        Set<JournalVar> updatedSet = new HashSet<>();

        for (JournalVar newVar : newJournal.getVariable()) {
            String key = newVar.getWikiPublisherId() + "|" + newVar.getStartDate();
            JournalVar existingVar = existingMap.remove(key);
            if (existingVar == null || !existingVar.equals(newVar)) {
                changed = true;
                if (existingVar == null) {
                    updatedSet.add(newVar);
                } else {
                    // take the changed dates from the new Journal
                    existingVar.setStartDate(newVar.getStartDate());
                    existingVar.setEndDate(newVar.getEndDate());
                    updatedSet.add(existingVar);
                }
            } else {
                updatedSet.add(existingVar);
            }
        }

        if (!existingMap.isEmpty()) { // Some entries were removed
            changed = true;
        }

        journal.setVariable(updatedSet);
        return changed;
    }


    private boolean areMultipleValuesEqual(String oldValues, String newValues) {
        String[] oldValueArray = str2array(oldValues, Journal.listDelimiter);
        String[] newValueArray = str2array(newValues, Journal.listDelimiter);
        Arrays.sort(oldValueArray);
        Arrays.sort(newValueArray);
        boolean areEqual = Arrays.equals(oldValueArray, newValueArray);
        return areEqual;
    }

}

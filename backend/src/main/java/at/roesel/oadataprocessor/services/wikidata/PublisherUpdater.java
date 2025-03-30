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

import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.PublisherVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static at.roesel.common.StringSupport.str2array;

public class PublisherUpdater {

    private final Logger logger = LoggerFactory.getLogger(PublisherUpdater.class);

    public boolean updatePublisher(Publisher publisher, Publisher newPublisher) {

        boolean changed = false;
        String name = publisher.getName();
        if (!Objects.equals(publisher.getName(), newPublisher.getName())) {
            changed = true;
            // set the previous name as alias
            if (publisher.getName() != null && !publisher.getName().isEmpty()) {
                publisher.addAlias(publisher.getName());
            }
            // set the new name
            publisher.setName(newPublisher.getName());
            logger.debug(String.format("name is different: %s -> %s", name, newPublisher.getName()));
        }

        if (!Objects.equals(publisher.getAlias(), newPublisher.getAlias())) {
            // we keep existing aliases and only add new ones
            for (String alias : newPublisher.getAlternateNames()) {
                boolean result = publisher.addAlias(alias);
                if (result) {
                    changed = true;
                    logger.debug(String.format("added new alias %s", alias));
                }
            }
        }

        // multiple values
        if (!areMultipleValuesEqual(publisher.getIsni(), newPublisher.getIsni())) {
            changed = true;
            logger.debug(String.format("Isni is different: %s -> %s", publisher.getIsni(), newPublisher.getIsni()));
            // set the new isni
            publisher.setIsni(newPublisher.getIsni());
        }

        if (!areMultipleValuesEqual(publisher.getRor(), newPublisher.getRor())) {
            changed = true;
            logger.debug(String.format("Ror is different: %s -> %s", publisher.getRor(), newPublisher.getRor()));
            publisher.setRor(newPublisher.getRor());
        }

        String ringgoldId = publisher.getRinggoldId();
        if (!Objects.equals(publisher.getRinggoldId(), newPublisher.getRinggoldId())) {
            boolean changed2 = publisher.addRinggoldId(newPublisher.getRinggoldId());
            if (changed2) {
                changed = true;
                logger.debug(String.format("RinggoldId is different: %s -> %s", ringgoldId, newPublisher.getRinggoldId()));
            }
        }

        // multiple values
        if (!areMultipleValuesEqual(publisher.getWikiInstanceOf(), newPublisher.getWikiInstanceOf())) {
            changed = true;
            logger.debug(String.format("InstanceOf is different: %s -> %s", publisher.getWikiInstanceOf(), newPublisher.getWikiInstanceOf()));
            publisher.setWikiInstanceOf(newPublisher.getWikiInstanceOf());
        }


        // parent publishers
        if (!Objects.equals(publisher.getWikiParentId(), newPublisher.getWikiParentId())) {
            changed = true;
            logger.debug(String.format("parent is different: %s -> %s", publisher.getWikiParentId(), newPublisher.getWikiParentId()));
            publisher.setWikiParentId(newPublisher.getWikiParentId());
        }

        // multiple parent publishers
        if (updateVariable(publisher, newPublisher)) {
            changed = true;
        }

        if (changed) {
            logger.debug(String.format("Updated publisher id = %s, wikidataId = %s", publisher.getId(), publisher.getWikiId()));
        }

        return changed;
    }

    public boolean updateVariable(Publisher publisher, Publisher newPublisher) {
        boolean changed = false;
        Map<String, PublisherVar> existingMap = new HashMap<>();
        for (PublisherVar var : publisher.getVariable()) {
            String key = var.getWikiParentId() + "|" + var.getStartDate();
            existingMap.put(key, var);
        }

        Set<PublisherVar> updatedSet = new HashSet<>();

        for (PublisherVar newVar : newPublisher.getVariable()) {
            String key = newVar.getWikiParentId() + "|" + newVar.getStartDate();
            PublisherVar existingVar = existingMap.remove(key);
            if (existingVar == null || !existingVar.equals(newVar)) {
                changed = true;
                if (existingVar == null) {
                    updatedSet.add(newVar);
                } else {
                    // take the changed dates from the new Publisher
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

        publisher.setVariable(updatedSet);
        return changed;
    }


    private boolean areMultipleValuesEqual(String oldValues, String newValues) {
        String[] oldValueArray = str2array(oldValues, Publisher.listDelimiter);
        String[] newValueArray = str2array(newValues, Publisher.listDelimiter);
        Arrays.sort(oldValueArray);
        Arrays.sort(newValueArray);
        boolean areEqual = Arrays.equals(oldValueArray, newValueArray);
        return areEqual;
    }

}

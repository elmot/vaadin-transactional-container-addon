/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.data.tx;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import org.apache.commons.collections.CollectionUtils;
import org.vaadin.data.tx.domain.Location;

import java.util.Collection;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Container manipulation utilities for tests
 *
 * @author Vaadin Ltd
 */
class ContainerTestUtils {

    public static MeetupBeanContainer newFilledContainer() {
        MeetupBeanContainer beanContainer = new MeetupBeanContainer();
        fillContainer(beanContainer);
        return beanContainer;
    }

    public static void fillContainer(Container.Indexed container) {
        newItemToContainer(container, 1, "M 1", 100000000L, 100, 30, 60);
        newItemToContainer(container, 2, "M 2", 200000000L, 200, 60, 30);
        newItemToContainer(container, -2, "M -2", 300000000L, 200, 60, 30);
        newItemToContainer(container, -22, "M -2x", 300000000L, 200, 60, 30);
        newItemToContainer(container, -27, "M -2x", 300000000L, 200, 60, 30);
    }

    @SuppressWarnings("unchecked")
    public static void newItemToContainer(Container.Indexed container, long id, String description, long when,
            double altitude, double latitude, double longitude) {
        Item item = container.addItemAt(container.size(), id);
        item.getItemProperty("description").setValue(description);
        item.getItemProperty("when").setValue(new Date(when));
        item.getItemProperty("location").setValue(new Location(altitude, latitude, longitude));
    }


    public static void compareContainers(Container.Indexed expected, Container.Indexed actual) {
        Collection<?> expectedPropertyIds = expected.getContainerPropertyIds();
        Collection<?> actualPropertyIds = actual.getContainerPropertyIds();
        assertTrue("Properties IDs ", CollectionUtils.isEqualCollection(expectedPropertyIds, actualPropertyIds));
        for (Object expectedPropertyId : expectedPropertyIds) {
            assertEquals("Property Class", expected.getType(expectedPropertyId), actual.getType(expectedPropertyId));
        }
        assertEquals("Size", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Object expectedId = expected.getIdByIndex(i);
            Object actualId = actual.getIdByIndex(i);
            assertEquals("ItemId at index " + i, expectedId, actualId);
            Item expectedItem = expected.getItem(expectedId);
            Item actualItem = actual.getItem(expectedId);
            assertTrue("Property set", CollectionUtils.isEqualCollection(expectedItem.getItemPropertyIds(), actualItem.getItemPropertyIds()));
            for (Object propertyId : expectedPropertyIds) {
                assertEquals("Item:" + expectedId + "; property:" + propertyId,
                        expectedItem.getItemProperty(propertyId).getValue(),
                        actualItem.getItemProperty(propertyId).getValue());
            }
        }

    }

}

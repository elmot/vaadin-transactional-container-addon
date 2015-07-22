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
package com.vaadin.data.tx;

import com.vaadin.data.Property;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.vaadin.data.tx.ContainerTestUtils.newFilledContainer;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;

/**
 * Tests for basic operations
 *
 * @author Vaadin Ltd
 */
public class TestTxOperations {


    public static final String TEST_PROPERTY_ID = "description";
    public static final String NEW_ITEM_DESCRIPTION = "New item description";
    public static final String OLD_ITEM_DESCRIPTION = "Old item description";
    private MeetupBeanContainer backedContainer;
    private UniversalCountingListener countingListener;
    private UniversalCountingListener txCountingListener;
    private TransactionalContainerWrapper transactionalContainer;
    private MeetupBeanContainer fullContainer;

    @Before
    public void setUp() {
        fullContainer = newFilledContainer();
        backedContainer = newFilledContainer();
        countingListener = new UniversalCountingListener();
        backedContainer.addItemSetChangeListener(countingListener);
        Property containerProperty = backedContainer.getContainerProperty(backedContainer.getIdByIndex(0), TEST_PROPERTY_ID);
        ((Property.ValueChangeNotifier) containerProperty).addValueChangeListener(countingListener);

        txCountingListener = new UniversalCountingListener();
        transactionalContainer = new TransactionalContainerWrapper(backedContainer, backedContainer);
        transactionalContainer.addTxListener(txCountingListener);
        transactionalContainer.addItemSetChangeListener(txCountingListener);
        Property transactionalContainerProperty =
                transactionalContainer.getContainerProperty(backedContainer.getIdByIndex(0), TEST_PROPERTY_ID);
        ((Property.ValueChangeNotifier) transactionalContainerProperty).addValueChangeListener(txCountingListener);
    }


    @Test
    public void testRemoveItem() {
        //Remove
        transactionalContainer.removeItem(transactionalContainer.firstItemId());

        assertEquals(transactionalContainer.size(), fullContainer.size() - 1);

        assertEquals(0, countingListener.itemsetChangeCount);
        assertEquals(0, countingListener.valueChangeCount);

        assertEquals(1, txCountingListener.itemsetChangeCount);
        assertEquals(0, txCountingListener.valueChangeCount);
        assertEquals(1, txCountingListener.startCount);
        assertEquals(0, txCountingListener.commitCount);
        assertEquals(0, txCountingListener.rollbackCount);

        // rollback
        transactionalContainer.rollback();

        ContainerTestUtils.compareContainers(transactionalContainer, fullContainer);

        assertEquals(0, countingListener.itemsetChangeCount);
        assertEquals(0, countingListener.valueChangeCount);

        assertEquals(2, txCountingListener.itemsetChangeCount);
        assertEquals(0, txCountingListener.valueChangeCount);
        assertEquals(1, txCountingListener.startCount);
        assertEquals(0, txCountingListener.commitCount);
        assertEquals(1, txCountingListener.rollbackCount);

        //Remove again
        transactionalContainer.removeItem(transactionalContainer.firstItemId());

        assertEquals(transactionalContainer.size(), fullContainer.size() - 1);

        assertEquals(0, countingListener.itemsetChangeCount);
        assertEquals(0, countingListener.valueChangeCount);

        assertEquals(3, txCountingListener.itemsetChangeCount);
        assertEquals(0, txCountingListener.valueChangeCount);
        assertEquals(2, txCountingListener.startCount);
        assertEquals(0, txCountingListener.commitCount);
        assertEquals(1, txCountingListener.rollbackCount);

        //commit
        transactionalContainer.commit();

        assertEquals(backedContainer.size(), fullContainer.size() - 1);
        ContainerTestUtils.compareContainers(transactionalContainer, backedContainer);

        assertEquals(1, countingListener.itemsetChangeCount);
        assertEquals(0, countingListener.valueChangeCount);

        assertEquals(3, txCountingListener.itemsetChangeCount);
        assertEquals(0, txCountingListener.valueChangeCount);
        assertEquals(2, txCountingListener.startCount);
        assertEquals(1, txCountingListener.commitCount);
        assertEquals(1, txCountingListener.rollbackCount);

    }

    @Test
    public void testModification() {
        Object oldItemId = transactionalContainer.firstItemId();
        @SuppressWarnings("unchecked")
        Property<String> oldItemProperty =
                (Property<String>) transactionalContainer.getItem(oldItemId).getItemProperty(TEST_PROPERTY_ID);
        Object newItemId = transactionalContainer.addItemAfter(oldItemId);
        @SuppressWarnings("unchecked")
        Property<String> newItemProperty =
                (Property<String>) transactionalContainer.getItem(newItemId).getItemProperty(TEST_PROPERTY_ID);

        //old item manipulations
        oldItemProperty.setValue(OLD_ITEM_DESCRIPTION);
        assertEquals(1, txCountingListener.valueChangeCount);

        oldItemProperty.setValue(OLD_ITEM_DESCRIPTION);
        assertEquals(1, txCountingListener.valueChangeCount);
        assertEquals(0, countingListener.valueChangeCount);


        //new item manipulations
        newItemProperty.setValue(NEW_ITEM_DESCRIPTION);

        newItemProperty.setValue(NEW_ITEM_DESCRIPTION);
        assertEquals(1, txCountingListener.valueChangeCount);
        assertEquals(0, countingListener.valueChangeCount);

        assertEquals(OLD_ITEM_DESCRIPTION, oldItemProperty.getValue());
        assertEquals(NEW_ITEM_DESCRIPTION, newItemProperty.getValue());

        //rollback and check
        transactionalContainer.rollback();
        assertNotSame(OLD_ITEM_DESCRIPTION, transactionalContainer.getContainerProperty(oldItemId, TEST_PROPERTY_ID).getValue());
        assertNull(transactionalContainer.getItem(newItemId));

        //Modify again and commit
        newItemId = transactionalContainer.addItemAfter(oldItemId);
        newItemProperty =
                (Property<String>) transactionalContainer.getItem(newItemId).getItemProperty(TEST_PROPERTY_ID);
        oldItemProperty =
                (Property<String>) transactionalContainer.getItem(oldItemId).getItemProperty(TEST_PROPERTY_ID);
        oldItemProperty.setValue(OLD_ITEM_DESCRIPTION);
        newItemProperty.setValue(NEW_ITEM_DESCRIPTION);

        transactionalContainer.commit();

        assertEquals(NEW_ITEM_DESCRIPTION, transactionalContainer.getContainerProperty(newItemId, TEST_PROPERTY_ID).getValue());
        assertEquals(OLD_ITEM_DESCRIPTION, transactionalContainer.getContainerProperty(oldItemId, TEST_PROPERTY_ID).getValue());

        assertEquals(1, countingListener.valueChangeCount);
        assertEquals(OLD_ITEM_DESCRIPTION, transactionalContainer.getContainerProperty(oldItemId, TEST_PROPERTY_ID).getValue());
        assertEquals(OLD_ITEM_DESCRIPTION, backedContainer.getContainerProperty(oldItemId, TEST_PROPERTY_ID).getValue());

        assertEquals(NEW_ITEM_DESCRIPTION, transactionalContainer.getContainerProperty(newItemId, TEST_PROPERTY_ID).getValue());
        assertEquals(NEW_ITEM_DESCRIPTION, backedContainer.getContainerProperty(newItemId, TEST_PROPERTY_ID).getValue());

    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        transactionalContainer.addItemAfter(transactionalContainer.firstItemId());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(transactionalContainer);
        oos.close();
        System.out.println("Serialized Container  size: " + baos.size());

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(bais);
        TransactionalContainerWrapper copy = (TransactionalContainerWrapper) objectInputStream.readObject();

        ContainerTestUtils.compareContainers(transactionalContainer, copy);
    }


}

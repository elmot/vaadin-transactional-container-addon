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

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Tests for array operations
 *
 * @author Vaadin Ltd
 */
public class TestTxArrayOperations {
    //todo JTA compatibility tests

    //todo test over SQLContainer


    private MeetupBeanContainer emptyContainer;
    private MeetupBeanContainer expected;
    private MeetupBeanContainer backedContainer;
    private TransactionalContainerWrapper txContainer;
    private UniversalCountingListener countingListener;

    @Test
    public void testRemoveOperations() {

        ContainerTestUtils.fillContainer(txContainer);
        txContainer.commit();

        assertEquals(1, countingListener.commitCount);
        assertEquals(0, countingListener.rollbackCount);
        assertEquals(1, countingListener.startCount);
        ContainerTestUtils.compareContainers(expected, backedContainer);
        ContainerTestUtils.compareContainers(expected, txContainer);

        txContainer.removeAllItems();

        assertEquals(1, countingListener.commitCount);
        assertEquals(0, countingListener.rollbackCount);
        assertEquals(2, countingListener.startCount);
        ContainerTestUtils.compareContainers(expected, backedContainer);
        ContainerTestUtils.compareContainers(emptyContainer, txContainer);

        txContainer.rollback();

        assertEquals(1, countingListener.commitCount);
        assertEquals(1, countingListener.rollbackCount);
        assertEquals(2, countingListener.startCount);
        ContainerTestUtils.compareContainers(expected, backedContainer);
        ContainerTestUtils.compareContainers(expected, txContainer);

        txContainer.removeAllItems();
        assertEquals(1, countingListener.commitCount);
        assertEquals(1, countingListener.rollbackCount);
        assertEquals(3, countingListener.startCount);
        ContainerTestUtils.compareContainers(expected, backedContainer);
        ContainerTestUtils.compareContainers(emptyContainer, txContainer);

        txContainer.commit();

        assertEquals(2, countingListener.commitCount);
        assertEquals(1, countingListener.rollbackCount);
        assertEquals(3, countingListener.startCount);
        ContainerTestUtils.compareContainers(emptyContainer, backedContainer);
        ContainerTestUtils.compareContainers(emptyContainer, txContainer);
    }

    @Test
    public void testAddOperations() {

        assertEquals(0, countingListener.commitCount);
        assertEquals(0, countingListener.rollbackCount);
        assertEquals(0, countingListener.startCount);
        ContainerTestUtils.compareContainers(emptyContainer, backedContainer);
        ContainerTestUtils.compareContainers(emptyContainer, txContainer);

        ContainerTestUtils.fillContainer(txContainer);

        assertEquals(0, countingListener.commitCount);
        assertEquals(0, countingListener.rollbackCount);
        assertEquals(1, countingListener.startCount);
        ContainerTestUtils.compareContainers(emptyContainer, backedContainer);
        ContainerTestUtils.compareContainers(expected, txContainer);

        txContainer.rollback();

        assertEquals(0, countingListener.commitCount);
        assertEquals(1, countingListener.rollbackCount);
        assertEquals(1, countingListener.startCount);
        ContainerTestUtils.compareContainers(emptyContainer, backedContainer);
        ContainerTestUtils.compareContainers(emptyContainer, txContainer);

        ContainerTestUtils.fillContainer(txContainer);

        assertEquals(0, countingListener.commitCount);
        assertEquals(1, countingListener.rollbackCount);
        assertEquals(2, countingListener.startCount);
        ContainerTestUtils.compareContainers(emptyContainer, backedContainer);
        ContainerTestUtils.compareContainers(expected, txContainer);

        txContainer.commit();

        assertEquals(1, countingListener.commitCount);
        assertEquals(1, countingListener.rollbackCount);
        assertEquals(2, countingListener.startCount);
        ContainerTestUtils.compareContainers(expected, backedContainer);
        ContainerTestUtils.compareContainers(expected, txContainer);
    }

    @Before
    public void setUp() {
        emptyContainer = new MeetupBeanContainer();
        expected = ContainerTestUtils.newFilledContainer();

        backedContainer = new MeetupBeanContainer();

        txContainer = new TransactionalContainerWrapper(backedContainer, backedContainer);
        countingListener = new UniversalCountingListener();
        txContainer.addTxListener(countingListener);

        assertEquals(0, countingListener.commitCount);
        assertEquals(0, countingListener.rollbackCount);
        assertEquals(0, countingListener.startCount);
        ContainerTestUtils.compareContainers(emptyContainer, backedContainer);
        ContainerTestUtils.compareContainers(emptyContainer, txContainer);
    }





}

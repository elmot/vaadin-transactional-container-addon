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
import com.vaadin.data.Property;
import org.vaadin.data.tx.TxListener;

import static junit.framework.Assert.fail;

/**
 * Provides various helper methods for connectors. Meant for internal use.
 *
 * @author Vaadin Ltd
 */
class UniversalCountingListener implements TxListener, Container.ItemSetChangeListener,
        Property.ValueChangeListener {
    public int startCount = 0;
    public int commitCount = 0;
    public int rollbackCount = 0;
    public int valueChangeCount = 0;
    public int itemsetChangeCount = 0;

    @Override
    public void transactionStarted(boolean implicit) {
        if (startCount != commitCount + rollbackCount) fail("Unexpected transaction start");
        startCount++;
    }

    @Override
    public void transactionCommitted() {
        if (startCount - 1 != commitCount + rollbackCount) fail("Unexpected commit");
        commitCount++;
    }

    @Override
    public void transactionRolledBack() {
        if (startCount - 1 != commitCount + rollbackCount) fail("Unexpected rollback");
        rollbackCount++;
    }

    @Override
    public void containerItemSetChange(Container.ItemSetChangeEvent itemSetChangeEvent) {
        itemsetChangeCount++;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
        valueChangeCount++;
    }
}

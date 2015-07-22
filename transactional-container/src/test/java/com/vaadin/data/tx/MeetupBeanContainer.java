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

import com.vaadin.data.Item;
import com.vaadin.data.tx.domain.Meetup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;

/**
 * Container to be tested
 *
 * @author Vaadin Ltd
 */
class MeetupBeanContainer extends BeanContainer<Long, Meetup> implements ItemGenerator {

    public MeetupBeanContainer() {
        super(Meetup.class);
    }


    @Override
    public BeanItem<Meetup> addItemAt(int previousItemIdx, Object newItemId) {
        Meetup meetup = new Meetup((Long) newItemId);
        return super.addItemAt(previousItemIdx, (Long) newItemId, meetup);
    }

    @Override
    public BeanItem<Meetup> addItemAfter(Object previousItemId, Object newItemId) {
        Meetup meetup = new Meetup((Long) newItemId);
        return super.addItemAfter((Long) previousItemId, (Long) newItemId, meetup);
    }

    private long idSeq = 100000;

    @Override
    public Item createNewItem(Object itemId) {
        return new BeanItem<Meetup>(new Meetup((Long) itemId));
    }

    @Override
    public Object createNewItemId() {
        return idSeq++;
    }

}

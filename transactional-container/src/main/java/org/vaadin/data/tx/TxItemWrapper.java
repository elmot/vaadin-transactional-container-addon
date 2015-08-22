package org.vaadin.data.tx;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TxItemWrapper is an internal class used by TransactionalContainerWrapper to wrap items
 * for allowing transactional commit and rollback.
 */
class TxItemWrapper implements Item, TxAware {
    private Item innerItem;
    private boolean isNew;
    private Object itemId;
    private final TransactionalContainerWrapper parent;

    private final Map<Object, TxPropertyWrapper<?>> wrapperMap = new LinkedHashMap<Object, TxPropertyWrapper<?>>();

    TxItemWrapper(TransactionalContainerWrapper parent, Item innerItem,
            Object innerItemId, boolean aNew) {
        this.parent = parent;
        this.innerItem = innerItem;
        itemId = innerItemId;
        isNew = aNew;
    }

    @Override
    public void startTransaction() {
        for (TxPropertyWrapper<?> propertyWrapper : wrapperMap.values()) {
            propertyWrapper.startTransaction();
        }
    }

    @Override
    public void commit() {
        for (TxPropertyWrapper<?> propertyWrapper : wrapperMap.values()) {
            propertyWrapper.commit();
        }
    }

    @Override
    public void rollback() {
        for (TxPropertyWrapper<?> propertyWrapper : wrapperMap.values()) {
            propertyWrapper.rollback();
        }
    }

    @Override
    public Collection<?> getItemPropertyIds() {
        return parent.getContainerPropertyIds();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean addItemProperty(Object id, Property property)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeItemProperty(Object id)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Property<?> getItemProperty(Object id) {
        TxPropertyWrapper<?> propertyWrapper = wrapperMap.get(id);
        if(propertyWrapper == null) {
            propertyWrapper = new TxPropertyWrapper(
                    innerItem.getItemProperty(id));
            propertyWrapper
                    .addValueChangeListener(parent.propertyValueChangeListener);
            wrapperMap.put(id, propertyWrapper);
        }
        return propertyWrapper;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    Item getInnerItem() {
        return innerItem;
    }

    @SuppressWarnings("unchecked")
    void setInnerItem(Item innerItem) {
        this.innerItem = innerItem;
        for (Map.Entry<Object, TxPropertyWrapper<?>> idPropertyEntry : wrapperMap
                .entrySet()) {
            TxPropertyWrapper<?> propertyWrapper = idPropertyEntry.getValue();
            propertyWrapper.replaceInnerProperty(innerItem
                    .getItemProperty(idPropertyEntry.getKey()));
            propertyWrapper.reset();
        }
    }

    Object getItemId() {
        return itemId;
    }
}
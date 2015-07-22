package com.vaadin.data.tx;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.shared.util.SharedUtil;

/**
 * TxPropertyWrapper is an internal class used by TransactionalContainerWrapper to wrap
 * properties for allowing transactional commit and rollback.
 */
class TxPropertyWrapper<T> extends AbstractProperty<T> implements
        Property.ValueChangeNotifier, TxAware {

    private T newValue;
    private boolean pendingValue;
    private Property<T> wrappedProperty;

    TxPropertyWrapper(Property<T> wrappedProperty) {
        this.wrappedProperty = wrappedProperty;
    }

    @Override
    public void startTransaction() {
        // Nothing to do
    }

    @Override
    public void commit() {
        if (pendingValue) {
            wrappedProperty.setValue(newValue);
            reset();
        }
    }

    @Override
    public void rollback() {
        if (pendingValue) {
            reset();
            fireValueChange();
        }
    }

    @Override
    public T getValue() {
        return pendingValue ? newValue : wrappedProperty.getValue();
    }

    @Override
    public void setValue(T newValue) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new Property.ReadOnlyException();
        }
        if (!SharedUtil.equals(newValue, this.getValue())) {
            pendingValue = true;
            this.newValue = newValue;
            fireValueChange();
        }
    }

    @Override
    public Class<? extends T> getType() {
        return wrappedProperty.getType();
    }

    @Override
    public boolean isReadOnly() {
        return wrappedProperty.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        wrappedProperty.setReadOnly(true);
        super.setReadOnly(newStatus);
    }

    void reset() {
        pendingValue = false;
        newValue = null;
    }

    void replaceInnerProperty(Property<T> itemProperty) {
        wrappedProperty = itemProperty;
    }
}
package com.vaadin.data.tx;

import java.io.Serializable;

/**
 * Internal notifier interface for transaction events.
 */
interface TxAware extends Serializable {

    /**
     * Explicitly starts a new transaction. If a transaction has been started
     * already, this call should have no effect.
     */
    public void startTransaction();

    /**
     * Commits all pending changes to the underlying container.
     */
    public void commit();

    /**
     * Discards all pending changes and re-syncs the state with the underlying
     * container.
     */
    public void rollback();
}

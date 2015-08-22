package org.vaadin.data.tx;

import java.io.Serializable;

/**
 * TxListener is an interface for listening transaction state changes from
 * external code.
 */
public interface TxListener extends Serializable {

    /**
     * This will be called when a transaction is started.
     *
     * @param implicit
     *            true if the transaction is started implicitly (value
     *            modification, add/remove item), false if startTransaction()
     *            was called
     */
    public void transactionStarted(boolean implicit);

    /**
     * This will be called after a successfull commit.
     */
    public void transactionCommitted();

    /**
     * This will be called after pending changes have been rolled back.
     */
    public void transactionRolledBack();
}

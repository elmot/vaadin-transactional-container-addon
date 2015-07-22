package com.vaadin.data.tx.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.tx.ItemGenerator;
import com.vaadin.data.tx.TransactionalContainerWrapper;
import com.vaadin.data.tx.TxListener;
import com.vaadin.data.tx.demo.domain.Status;
import com.vaadin.data.tx.demo.domain.Ticket;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Theme("valo")
@Widgetset("com.vaadin.data.tx.demo.DemoWidgetset")
public class MyUI extends UI {

    private long idCounter = 10000;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        // Main layout is SplitPanel
        VerticalSplitPanel splitPanel = new VerticalSplitPanel();
        splitPanel.setSizeFull();
        setContent(splitPanel);

        // Build the non-transactional grid
        BeanContainer<Long, Ticket> beanContainer = loadData();
        final Grid grid1 = buildGrid(beanContainer);
        grid1.setSizeFull();

        // Wrap in a layout and add as first component in split panel
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.addComponent(grid1);
        splitPanel.setFirstComponent(layout);

        // Build the transactional container and grid
        final TransactionalContainerWrapper txContainer = buildTxContainerWrapper(beanContainer);
        final Grid txGrid = buildGrid(txContainer);

        // Enable unbuffered editing and build filter row
        txGrid.setEditorEnabled(true);
//        txGrid.setEditorBuffered(false);
        buildFilterRow(txContainer, txGrid);

        // Single-click editor
        txGrid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                {
                    if (itemClickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
                        Object itemId = itemClickEvent.getItemId();
                        if (itemId != null) {
                            txGrid.editItem(itemId);
                        }
                    }

                }
            }
        });

//        txGrid.getColumn("hold").setRenderer(new CheckboxRenderer());

        grid1.getColumn("hold").setRenderer(new HtmlRenderer(), new StringToBooleanConverter() {
            @Override
            protected String getTrueString() {
                return FontAwesome.CHECK_SQUARE_O.getHtml();
            }

            @Override
            protected String getFalseString() {
                return FontAwesome.SQUARE_O.getHtml();
            }
        });
        // Base layout for second component
        VerticalLayout txLayout = new VerticalLayout();
        txLayout.setSizeFull();
        txLayout.setMargin(true);
        txLayout.setSpacing(true);
        txLayout.addComponent(txGrid);
        txLayout.setExpandRatio(txGrid, 1);
        txLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

        // Add row -button
        Button rowAddBtn = new Button("Add a row", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Object selectedRowId = txGrid.getSelectedRow();
                if (selectedRowId == null) {
                    txContainer.addItem();
                } else {
                    txContainer.addItemAfter(selectedRowId);
                }
            }
        });

        // Delete row -button
        final Button rowDeleteBtn = new Button("Delete selected row", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                {
                    Collection<Object> selectedRowIds = txGrid.getSelectedRows();
                    for (Object selectedRowId : selectedRowIds) {
                        txContainer.removeItem(selectedRowId);
                    }
                    txGrid.select(null);
                }
            }
        }
        );

        // Commit -button
        final Button commitBtn = new Button("Commit", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                txContainer.commit();
            }
        });
        commitBtn.setEnabled(false);

        // Rollback -button
        final Button rollbackBtn = new Button("Rollback", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                {
                    txContainer.rollback();
                    txGrid.select(null);
                }
            }
        });

        // Button layout
        HorizontalLayout buttons = new HorizontalLayout(rowAddBtn,
                rowDeleteBtn, commitBtn, rollbackBtn);
        buttons.setSpacing(true);
        txLayout.addComponent(buttons);

        rowDeleteBtn.setEnabled(false);
        rollbackBtn.setEnabled(false);

        // Transaction listener for toggling button states and showing
        // notifications
        txContainer.addTxListener(
                new TxListener() {
                    @Override
                    public void transactionStarted(boolean implicit) {
                        commitBtn.setEnabled(true);
                        rollbackBtn.setEnabled(true);
                        showTrayNotification((implicit ? "Implicit " : "Explicit ")
                                + "transaction started");
                    }

                    @Override
                    public void transactionCommitted() {
                        //In unbuffered mode, all editor changes are always propagated to container,
                        // this just closes the editor
                        txGrid.cancelEditor();

                        commitBtn.setEnabled(false);
                        rollbackBtn.setEnabled(false);
                        showTrayNotification("Changes committed");
                    }

                    @Override
                    public void transactionRolledBack() {
                        txGrid.cancelEditor();
                        commitBtn.setEnabled(false);
                        rollbackBtn.setEnabled(false);
                        showTrayNotification("Changes rolled back");
                    }
                }

        );

        // Add a selection listener to toggle row delete button
        txGrid.addSelectionListener(
                new SelectionEvent.SelectionListener()
                {
                    @Override
                    public void select(SelectionEvent event) {
                        rowDeleteBtn.setEnabled(!event.getSelected().isEmpty());
                    }
                }
        );

        splitPanel.setSecondComponent(txLayout);
    }

    private Grid buildGrid(Container.Indexed beanContainer) {
        Grid grid = new Grid(beanContainer);
        grid.setSizeFull();
        grid.removeColumn("blockedBy");
        grid.removeColumn("parent");
        return grid;
    }

    private TransactionalContainerWrapper buildTxContainerWrapper(
            BeanContainer<Long, Ticket> beanContainer) {
        return new TransactionalContainerWrapper(beanContainer, new ItemGenerator() {
            @Override
            public Item createNewItem(Object id) {
                Ticket ticket = new Ticket();
                ticket.setId((Long) id);
                return new BeanItem<Ticket>(ticket, Ticket.class);
            }

            @Override
            public Object createNewItemId() {
                return idCounter++;
            }
        });
    }

    private void buildFilterRow(final TransactionalContainerWrapper txContainer, Grid txGrid) {
        Grid.HeaderRow filterRow = txGrid.appendHeaderRow();

        // Add filtering fields
        for (Grid.Column column : txGrid.getColumns()) {
            final Object propertyId = column.getPropertyId();
            final Grid.HeaderCell cell = filterRow.getCell(propertyId);
            final TextField filterField = new TextField();
            filterField.setWidth(100, Unit.PERCENTAGE);
            filterField.addStyleName(ValoTheme.TEXTFIELD_SMALL);

            // Update filter When the filter input is changed
            filterField.addTextChangeListener(new FieldEvents.TextChangeListener() {
                @Override
                public void textChange(FieldEvents.TextChangeEvent change) {
                    // Can't modify filters so need to replace
                    txContainer.removeContainerFilters(propertyId);

                    boolean ignoreCase = true;
                    boolean onlyMatchPrefix = false;

                    // (Re)create the filter if necessary
                    if (!change.getText().isEmpty()) {
                        txContainer.addContainerFilter(propertyId,
                                change.getText(), ignoreCase, onlyMatchPrefix);
                    }
                }
            });
            cell.setComponent(filterField);
        }
    }

    private static BeanContainer<Long, Ticket> loadData() {
        BeanContainer<Long, Ticket> container = new BeanContainer<Long, Ticket>(
                Ticket.class) {
            @Override
            public BeanItem<Ticket> addItemAfter(Object previousItemId,
                    Object newItemId) {
                Ticket ticket = new Ticket();
                ticket.setId((Long) newItemId);
                return super.addItemAfter((Long) previousItemId,
                        (Long) newItemId, ticket);
            }
        };
        container.setBeanIdProperty("id");

        List<Ticket> list = new ArrayList<Ticket>();
        for (int i = 0; i < 10000; i++) {
            Ticket t = new Ticket();
            t.setId(i);
            t.setDescription("Vaadin 7.5 release");
            t.setCreatedAt(new Date());
            t.setName("task");
            t.setStatus(i % 2 == 0 ? Status.NOT_STARTED : Status.IN_PROGRESS);
            t.setHold(i % 2 == 0);
            list.add(t);
        }
        container.addAll(list);

        return container;

    }

    private void showTrayNotification(String text) {
        Notification n = new Notification(text, null, Type.TRAY_NOTIFICATION);
        n.setPosition(Position.BOTTOM_LEFT);
        n.show(Page.getCurrent());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}

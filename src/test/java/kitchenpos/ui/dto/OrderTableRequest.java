package kitchenpos.ui.dto;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableRequest {

    private UUID id;
    private String name;
    private int numberOfGuests;
    private boolean empty;

    public OrderTableRequest() {
    }

    public OrderTableRequest(final String name) {
        this.name = name;
    }

    public OrderTableRequest(final OrderTable orderTable) {
        this.name = orderTable.getName();
        this.numberOfGuests = orderTable.getNumberOfGuests();
        this.empty = orderTable.isEmpty();
    }

    public OrderTableRequest(final int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(final int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(final boolean empty) {
        this.empty = empty;
    }
}

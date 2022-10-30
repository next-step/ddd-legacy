package kitchenpos.ordertable.dto;

import java.util.UUID;

public class OrderTableRequest {

    private String name;
    private int numberOfGuests;
    private UUID id;

    public OrderTableRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getNumberOfGuests() {
        return this.numberOfGuests;
    }

    public UUID getId() {
        return this.id;
    }
}

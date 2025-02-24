package kitchenpos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Table(name = "order_table")
@Entity
public class OrderTable {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    @Column(name = "occupied", nullable = false)
    private boolean occupied;

    public OrderTable() {
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

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(final boolean occupied) {
        this.occupied = occupied;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OrderTable orderTable;

        public Builder() {
            orderTable = new OrderTable();
        }

        public Builder id(UUID id) {
            orderTable.id = id;
            return this;
        }

        public Builder name(String name) {
            orderTable.name = name;
            return this;
        }

        public Builder numberOfGuests(int numberOfGuests) {
            orderTable.numberOfGuests = numberOfGuests;
            return this;
        }

        public Builder occupied(boolean occupied) {
            orderTable.occupied = occupied;
            return this;
        }

        public OrderTable build() {
            return orderTable;
        }
    }
}

package kitchenpos.ordertable.domain;

import kitchenpos.common.vo.Name;
import kitchenpos.ordertable.vo.NumberOfGuests;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "order_table")
@Entity
public class OrderTable {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Embedded
    private Name name;

    @Embedded
    private NumberOfGuests numberOfGuests;

    @Column(name = "occupied", nullable = false)
    private boolean occupied;

    protected OrderTable() {

    }

    public OrderTable(Name name, NumberOfGuests numberOfGuests) {
        this.name = name;
        this.numberOfGuests = numberOfGuests;
        this.occupied = false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name.getName();
    }

    public int getNumberOfGuests() {
        return this.numberOfGuests.getNumber();
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(final boolean occupied) {
        this.occupied = occupied;
    }

    public void occupied() {
        this.occupied = true;
    }

    public void vacant() {
        this.occupied = false;
    }

    public void changeNumberOfGuests(int numberOfGuests) {
        if (!this.occupied) {
            throw new IllegalArgumentException("주문 테이블이 공석일 경우 착석 인원을 변경 할 수 없다.");
        }
        this.numberOfGuests = new NumberOfGuests(numberOfGuests);
    }
}

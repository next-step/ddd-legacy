package kitchenpos.ordertable.dto.request;

public class ChangeNumberOfGuestRequest {

    private final int numberOfGuests;

    public ChangeNumberOfGuestRequest(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public int getNumberOfGuests() {
        return this.numberOfGuests;
    }
}

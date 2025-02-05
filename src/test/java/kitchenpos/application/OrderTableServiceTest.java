package kitchenpos.application;

import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderTableServiceTest {

    OrderTableRepository orderTableRepository;
    OrderRepository orderRepository;

    OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new FakeOrderTableRepository();
        orderRepository = new FakeOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }


    @Test
    @DisplayName("테이블 생성시 기본값들을 갖는다. (손님수=0, 사용여부=미사용)")
    void createOrderTable(){
        //given
        OrderTable request = createRequestOrderTable("테이블1");
        //when
        OrderTable orderTable = orderTableService.create(request);
        //then
        assertAll(
                () -> assertThat(orderTable.getName()).isEqualTo("테이블1"),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(orderTable.isOccupied()).isFalse()
        );
    }

    @ParameterizedTest
    @DisplayName("테이블의 이름은 필수이다.")
    @NullAndEmptySource
    void throwWhenNameIsNullOrEmpty(String orderTableName){
        //given
        OrderTable requestOrderTable = createRequestOrderTable(orderTableName);

        //when
        //then
        assertThatThrownBy(() -> orderTableService.create(requestOrderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("식사 테이블앉을수 있다.")
    void canChangeOrderTableIsOccupied(){
        //given
        OrderTable request = createRequestOrderTable("테이블");
        OrderTable orderTable = orderTableService.create(request);

        //when
        OrderTable sitedTable = orderTableService.sit(orderTable.getId());

        assertThat(sitedTable.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블의 인원은 변경시 변경인원은 0이상이어야 한다.")
    void canChangeOrderTableWhenGuestCntIsUnderZero(){
        OrderTable request = createRequestOrderTable("테이블");
        request.setNumberOfGuests(-1);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("사용중인 테이블은 인원수가 변경될 수 있다.")
    void canChangeOrderTableGuest(){
        //given
        OrderTable request = createRequestOrderTable("테이블");
        OrderTable orderTable = orderTableService.create(request);
        OrderTable sitedTable = orderTableService.sit(orderTable.getId());
        sitedTable.setNumberOfGuests(10);
        //when
        OrderTable changedTable = orderTableService.changeNumberOfGuests(sitedTable.getId(), orderTable);
        //then
        assertThat(changedTable.getNumberOfGuests()).isEqualTo(10);
    }


    @Test
    @DisplayName("주문이 완료된 테이블은 청소할수 있다.")
    void canClearOrderTable(){
            //given
            OrderTable request = createRequestOrderTable("테이블");
            OrderTable orderTable = orderTableService.create(request);
            OrderTable sitedTable = orderTableService.sit(orderTable.getId());
            sitedTable.setNumberOfGuests(10);
            OrderTable changedTable = orderTableService.changeNumberOfGuests(sitedTable.getId(), sitedTable);

            //when
            OrderTable clearingTable = orderTableService.clear(changedTable.getId());

            assertAll(
                    () -> assertThat(clearingTable.isOccupied()).isFalse(),
                    () -> assertThat(clearingTable.getNumberOfGuests()).isZero()
            );
    }


    private OrderTable createRequestOrderTable(String name){
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }
}

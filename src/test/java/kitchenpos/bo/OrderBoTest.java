package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.Order;
import kitchenpos.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static kitchenpos.bo.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class OrderBoTest {

    private final MenuDao menuDao = new TestMenuDao();
    private final OrderDao orderDao = new TestOrderDao();
    private final OrderLineItemDao orderLineItemDao = new TestOrerLineItemDao();
    private final OrderTableDao orderTableDao = new TestOrderTableDao();

    private OrderBo orderBo;

    private Order order;

    @BeforeEach
    void setUp() {
        orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);
        order = 일번테이블주문();
        menuDao.save(치맥셋트());
        orderTableDao.save(만석인_일번테이블());
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class orderCreateTest {
        @Test
        @DisplayName("새로운 주문을 생성 할 수 있다.")
        void create() {
            //given - when
            Order expected = orderBo.create(order);

            //then
            Assertions.assertAll(
                    () -> assertThat(expected).isNotNull(),
                    () -> assertThat(expected.getOrderTableId()).isEqualTo(order.getOrderTableId()),
                    () -> assertThat(expected.getOrderedTime()).isEqualTo(order.getOrderedTime())
            );
        }

        @Test
        @DisplayName("주문 시 최초 조리 상태는 '조리중(COOKING)' 으로 설정된다")
        void create2() {
            //given - when
            Order expected = orderBo.create(order);

            //then
            Assertions.assertAll(
                    () -> assertThat(expected).isNotNull(),
                    () -> assertThat(expected.getOrderStatus()).isEqualTo("COOKING")
            );
        }

        @Test
        @DisplayName("주문한 테이블이 비어 있으면 주문이 불가 하다")
        void crate3() {
            //given
            OrderTable emptyOrderTable = OrderTableBuilder
                    .anOrderTable()
                    .withId(1L)
                    .withTableGroupId(1L)
                    .withNumberOfGuests(5)
                    .withEmpty(true)
                    .build();

            orderTableDao.save(emptyOrderTable);

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(order));
        }

        @Test
        @DisplayName("최소 1개 이상의 메뉴를 주문 가능 하다.")
        void create4() {
            //given
            OrderLineItem unknowndMenuOrderLineItem = OrderLineItemBuilder
                    .anOrderLineItem()
                    .withSeq(1L)
                    .withOrderId(1L)
                    .withMenuId(5L) //unregistered menuId
                    .withQuantity(3)
                    .build();

            Order unknownMenuOrder = OrderBuilder
                    .anOrder()
                    .withOrderLineItems(Collections.singletonList(unknowndMenuOrderLineItem))
                    .withOrderTableId(1L)
                    .withOrderedTime(LocalDateTime.now())
                    .build();

            assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(unknownMenuOrder));
        }
    }

    @Test
    @DisplayName("전체 주문 리스트를 조회 할 수 있다.")
    void list() {
        //given
        Order actual = orderBo.create(order);

        //when
        List<Order> expected = orderBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.stream().anyMatch(i -> {
                    Long expectedId = i.getId();
                    Long actualId = actual.getId();

                    return expectedId.equals(actualId);
                }))
        );
    }

    @Test
    @DisplayName("주문 상태를 수정 할 수 있다.")
    void changeOrderStatus() {
        //given
        Order registerdOrder = orderBo.create(order);
        Order changedStatusOrder = OrderBuilder
                .anOrder()
                .withId(registerdOrder.getId())
                .withOrderTableId(registerdOrder.getOrderTableId())
                .withOrderLineItems(registerdOrder.getOrderLineItems())
                .withOrderedTime(registerdOrder.getOrderedTime())
                .withOrderStatus("MEAL")
                .build();

        Order expected = orderBo.changeOrderStatus(registerdOrder.getId(), changedStatusOrder);

        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.getId()).isEqualTo(changedStatusOrder.getId()),
                () -> assertThat(expected.getOrderStatus()).isEqualTo(changedStatusOrder.getOrderStatus())
        );
    }
}

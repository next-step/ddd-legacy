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

    @BeforeEach
    void setUp() {
        orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);
        menuDao.save(치맥셋트());
        orderTableDao.save(만석인_일번테이블());
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class orderCreateTest {
        @Test
        @DisplayName("새로운 주문을 생성 할 수 있다.")
        void create() {
            //given
            Order expected = 일번테이블주문();

            //when
            Order actual = orderBo.create(expected);

            //then
            assertThat(actual).isNotNull();
            Assertions.assertAll(
                    () -> assertThat(actual.getOrderTableId()).isEqualTo(expected.getOrderTableId()),
                    () -> assertThat(actual.getOrderedTime()).isEqualTo(expected.getOrderedTime())
            );
        }

        @Test
        @DisplayName("주문 시 최초 조리 상태는 '조리중(COOKING)' 으로 설정된다")
        void create2() {
            //given
            Order expected = 일번테이블주문();

            Order actual = orderBo.create(expected);

            //then
            assertThat(actual).isNotNull();
            assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        }

        @Test
        @DisplayName("주문한 테이블이 비어 있으면 주문이 불가 하다")
        void crate3() {
            //given
            Order expected = 일번테이블주문();
            OrderTable emptyOrderTable = OrderTableBuilder
                                            .anOrderTable()
                                            .withId(expected.getOrderTableId())
                                            .withEmpty(true)
                                            .build();

            orderTableDao.save(emptyOrderTable);

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> orderBo.create(expected));
        }

        @Test
        @DisplayName("최소 1개 이상의 메뉴를 주문 가능 하다.")
        void create4() {
            //given
            OrderLineItem unknownMenuOrderLineItem = OrderLineItemBuilder
                    .anOrderLineItem()
                    .withSeq(1L)
                    .withOrderId(1L)
                    .withMenuId(5L) //unregistered menuId
                    .withQuantity(3)
                    .build();

            Order unknownMenuOrder = OrderBuilder
                    .anOrder()
                    .withOrderLineItems(Collections.singletonList(unknownMenuOrderLineItem))
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
        Order expected = orderBo.create(일번테이블주문());

        //when
        List<Order> actual = orderBo.list();

        //then
        assertThat(actual).isNotNull();
        assertThat(actual).containsAnyOf(expected);
    }

    @Test
    @DisplayName("주문 상태를 수정 할 수 있다.")
    void changeOrderStatus() {
        //given
        Order registeredOrder = orderBo.create(일번테이블주문());

        Order expected = OrderBuilder
                .anOrder()
                .withId(registeredOrder.getId())
                .withOrderTableId(registeredOrder.getOrderTableId())
                .withOrderLineItems(registeredOrder.getOrderLineItems())
                .withOrderedTime(registeredOrder.getOrderedTime())
                .withOrderStatus("MEAL")
                .build();

        //when
        Order actual = orderBo.changeOrderStatus(registeredOrder.getId(), expected);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}

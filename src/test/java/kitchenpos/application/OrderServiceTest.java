package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import kitchenpos.integration_test_step.MenuIntegrationStep;
import kitchenpos.test_fixture.OrderLineItemTestFixture;
import kitchenpos.test_fixture.OrderTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OrderService 클래스")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService sut;

    @Autowired
    private MenuIntegrationStep menuIntegrationStep;

    @Autowired
    private DatabaseCleanStep databaseCleanStep;

    @DisplayName("새로운 주문 생성")
    @Nested
    class Describe_create {

        @BeforeEach
        void setUp() {
            databaseCleanStep.clean();
        }

        @DisplayName("새로운 주문을 생성할 수 있다.")
        @Test
        void create() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when
            Order result = sut.create(order);

            // then
            assertThat(result).isNotNull();
        }

        @DisplayName("새로운 주문 생성 시 주문 상태는 대기(WAITING) 상태이다.")
        @Test
        void createOrderStatusWaiting() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when
            Order result = sut.create(order);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @DisplayName("새로운 주문 생성 시 주문 유형이 비어있으면 예외가 발생한다.")
        @Test
        void createOrderTypeNullExceptionThrown() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(null)
                    .getOrder();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(order));
        }
    }
}

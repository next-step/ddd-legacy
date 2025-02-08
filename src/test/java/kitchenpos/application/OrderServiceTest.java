package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.*;
import static kitchenpos.fixture.OrderFixture.DEFAULT_DELIVERY_ADDRESS;
import static kitchenpos.fixture.OrderFixture.order;
import static kitchenpos.fixture.OrderTableFixture.DEFAULT_ORDER_TABLE_NAME;
import static kitchenpos.fixture.OrderTableFixture.orderTable;
import static kitchenpos.fixture.ProductFixture.product;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @MockBean
    private KitchenridersClient kitchenridersClient;

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = createMenu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE);
    }

    @Nested
    class 주문_생성_경우 {
        @Test
        void 포장_주문은_유효한_주문_라인_아이템이_주어지면_정상적으로_생성된다() {
            // given
            final Order request = order(OrderType.TAKEOUT, menu);

            // when
            final Order response = orderService.create(request);

            // then
            assertThat(response.getType()).isEqualTo(request.getType());
            assertThat(response.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(response.getOrderLineItems()).hasSize(1);
        }

        @Test
        void 배달_주문은_유효한_배송_주소가_주어지면_정상적으로_생성된다() {
            final Order request = order(OrderType.DELIVERY, menu, DEFAULT_DELIVERY_ADDRESS);

            // when
            final Order response = orderService.create(request);

            // then
            assertThat(response.getType()).isEqualTo(request.getType());
            assertThat(response.getDeliveryAddress()).isEqualTo(request.getDeliveryAddress());
            assertThat(response.getOrderLineItems()).hasSize(1);
        }

        @Test
        void 매장_식사_주문은_착석된_주문_테이블이_주어지면_정상적으로_생성된다() {
            // given
            final Order request = order(OrderType.EAT_IN, menu, createOrderTable(4));

            // when
            final Order response = orderService.create(request);

            // then
            assertThat(response.getType()).isEqualTo(request.getType());
            assertThat(response.getOrderTable().getId()).isEqualTo(request.getOrderTableId());
        }

        @Test
        void 주문_생성_시_필수_필드가_누락되면_예외가_발생한다() {
            // given
            final Order request = new Order();
            request.setOrderLineItems(new ArrayList<>());

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderService.create(request));
        }

        @Test
        void 배달_주문_생성_시_배송_주소가_없으면_예외가_발생한다() {
            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderService.create(order(OrderType.DELIVERY, menu, "")));
        }
    }

    @Nested
    class 주문_상태_전환_경우 {
        @Test
        void WAITING_상태의_DELIVERY_주문은_accept를_통해_ACCEPTED_상태로_전환되고_배달_요청이_진행된다() {
            // given
            final Order request = orderService.create(order(OrderType.DELIVERY, menu, DEFAULT_DELIVERY_ADDRESS));

            // when
            final Order response = orderService.accept(request.getId());

            // then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(kitchenridersClient, times(1))
                    .requestDelivery(eq(request.getId()), any(BigDecimal.class), eq(DEFAULT_DELIVERY_ADDRESS));
        }

        @Test
        void WAITING_상태가_아닌_주문은_accept_시_예외가_발생한다() {
            // given
            final Order created = orderService.create(order(OrderType.TAKEOUT, menu));
            created.setStatus(OrderStatus.ACCEPTED);

            // when & then
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.accept(created.getId()));
        }

        @Test
        void ACCEPTED_상태의_주문은_serve를_통해_SERVED_상태로_전환된다() {
            // given
            final Order request = orderService.create(order(OrderType.TAKEOUT, menu));
            request.setStatus(OrderStatus.ACCEPTED);

            // when
            final Order response = orderService.serve(request.getId());

            // then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void SERVED_상태의_TAKEOUT_주문은_startDelivery_시_예외가_발생한다() {
            // given
            final Order created = orderService.create(order(OrderType.TAKEOUT, menu));
            created.setStatus(OrderStatus.SERVED);

            // when & then
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.startDelivery(created.getId()));
        }

        @Test
        void SERVED_상태의_DELIVERY_주문은_startDelivery를_통해_DELIVERING_상태로_전환된다() {
            // given
            final Order request = orderService.create(order(OrderType.DELIVERY, menu, DEFAULT_DELIVERY_ADDRESS));
            request.setStatus(OrderStatus.SERVED);

            // when
            final Order response = orderService.startDelivery(request.getId());

            // then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        void 배달_중인_상태의_주문은_배달완료_상태로_전환된다() {
            // given
            final Order request = orderService.create(order(OrderType.DELIVERY, menu, DEFAULT_DELIVERY_ADDRESS));
            request.setStatus(OrderStatus.DELIVERING);

            // when
            final Order response = orderService.completeDelivery(request.getId());

            // then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        void DELIVERY_주문_완료_시_상태_조건을_만족하지_않으면_예외가_발생한다() {
            // given
            final Order createdDelivery = orderService.create(order(OrderType.DELIVERY, menu, DEFAULT_DELIVERY_ADDRESS));

            // when & then
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.complete(createdDelivery.getId()));
        }

        @Test
        void TAKEOUT_주문_완료_시_상태_조건을_만족하지_않으면_예외가_발생한다() {
            // given
            final Order request = orderService.create(order(OrderType.TAKEOUT, menu));

            // when & then
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.complete(request.getId()));
        }

        @Test
        void DELIVERY_주문은_DELIVERED_상태여야_완료되며_완료되면_COMPLETED_상태로_전환된다() {
            // given
            final Order request = orderService.create(order(OrderType.DELIVERY, menu, DEFAULT_DELIVERY_ADDRESS));
            request.setStatus(OrderStatus.DELIVERED);

            // when
            final Order response = orderService.complete(request.getId());

            // then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        void TAKEOUT_주문은_SERVED_상태여야_완료되며_완료되면_COMPLETED_상태로_전환된다() {
            // given
            final Order request = orderService.create(order(OrderType.TAKEOUT, menu));
            request.setStatus(OrderStatus.SERVED);

            // when
            final Order response = orderService.complete(request.getId());

            // then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        void EAT_IN_주문은_SERVED_상태여야_완료되며_완료되면_COMPLETED_상태로_전환되고_테이블이_초기화된다() {
            // given
            final Order request = orderService.create(order(OrderType.EAT_IN, menu, createOrderTable(4)));
            request.setStatus(OrderStatus.SERVED);


            // when
            final Order response1 = orderService.complete(request.getId());
            final OrderTable response2 = orderTableRepository.findById(response1.getOrderTable().getId()).orElseThrow(NoSuchElementException::new);

            // then
            assertAll(
                    () -> assertThat(response1.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(response2.getNumberOfGuests()).isZero(),
                    () -> assertThat(response2.isOccupied()).isFalse()
            );
        }
    }

    @Nested
    class 주문_조회_경우 {
        @Test
        void 전체_주문을_조회하면_생성된_모든_주문이_반환된다() {
            // given
            orderService.create(order(OrderType.TAKEOUT, menu));
            orderService.create(order(OrderType.TAKEOUT, menu));

            // when
            List<Order> response = orderService.findAll();

            // then
            assertThat(response).hasSize(2);
        }
    }

    private Menu createMenu(final String name, final BigDecimal price) {
        final MenuGroup menuGroup = menuGroup();
        menuGroupRepository.save(menuGroup);

        final Product product = product();
        productRepository.save(product);

        final MenuProduct menuProduct = menuProduct(seq(), DEFALUT_QUANTITY, product);
        return menuRepository.save(menu(createMenuId(), name, price, menuGroup, List.of(menuProduct), DEFAULT_DISPLAYED));
    }

    private OrderTable createOrderTable(final int numberOfGuests) {
        return orderTableRepository.save(orderTable(DEFAULT_ORDER_TABLE_NAME, numberOfGuests));
    }

}

package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.EatInOrderFixture.*;
import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuFixture.노출된_무료_메뉴;
import static kitchenpos.fixture.MenuGroupFixture.*;
import static kitchenpos.fixture.DeliveryOrderFixture.*;
import static kitchenpos.fixture.OrderFailTestFixture.*;
import static kitchenpos.fixture.OrderTableFixture.*;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceMockTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MenuRepository menuRepository;

    @Mock
    OrderTableRepository orderTableRepository;

    OrderService orderService;

    Menu 양념치킨_메뉴;


    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, new KitchenridersClient());
        this.양념치킨_메뉴 = MenuFixture.양념치킨_메뉴(한마리(), 양념치킨());
    }

    @Nested
    class 배달_주문_전체기능_정상_테스트 {
        @Test
        void 배달_주문을_등록한다() {
            Order 배달_주문 = 배달_주문(양념치킨_메뉴);
            given(menuRepository.findById(any())).willReturn(Optional.of(양념치킨_메뉴));
            given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(양념치킨_메뉴));
            given(orderRepository.save(any())).willReturn(배달_주문);

            Order 등록된_주문 = orderService.create(배달_주문);

            assertThat(등록된_주문.getId()).isNotNull();
        }

        @Test
        void 배달_주문을_수락한다() {
            given(orderRepository.findById(any())).willReturn(Optional.of(대기중인_배달주문(양념치킨_메뉴)));

            Order 수락한_주문 = orderService.accept(UUID.randomUUID());

            assertThat(수락한_주문.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void 배달_주문을_제공한다() {
            given(orderRepository.findById(any())).willReturn(Optional.of(수락된_배달_주문(양념치킨_메뉴)));

            Order 제공된_주문 = orderService.serve(UUID.randomUUID());

            assertThat(제공된_주문.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void 배달을_시작한다() {
            given(orderRepository.findById(any())).willReturn(Optional.of(배달_준비된_주문(양념치킨_메뉴)));

            Order 배달이_시작된_주문 = orderService.startDelivery(UUID.randomUUID());

            assertThat(배달이_시작된_주문.getStatus()).isEqualTo(OrderStatus.DELIVERING);

        }

        @Test
        void 배달을_완료한다() {
            given(orderRepository.findById(any())).willReturn(Optional.of(배달중인_주문(양념치킨_메뉴)));

            Order 배달이_완료된_주문 = orderService.completeDelivery(UUID.randomUUID());

            assertThat(배달이_완료된_주문.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        void 배달_주문을_완료한다() {
            given(orderRepository.findById(any())).willReturn(Optional.of(배달이_완료된_주문(양념치킨_메뉴)));

            Order 완료된_배달_주문 = orderService.complete(UUID.randomUUID());

            assertThat(완료된_배달_주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    class 매장_주문_전체기능_정상_테스트 {
        @Test
        void 매장_주문을_등록한다() {
            Order 매장_주문 = 매장_주문(양념치킨_메뉴);
            given(menuRepository.findById(any())).willReturn(Optional.of(양념치킨_메뉴));
            given(orderTableRepository.findById(any())).willReturn(Optional.of(착석_가능한_손님_2명의_오른쪽_테이블()));
            given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(양념치킨_메뉴));
            given(orderRepository.save(any())).willReturn(매장_주문);

            Order 등록된_주문 = orderService.create(매장_주문);

            assertThat(등록된_주문.getId()).isNotNull();
        }

        @Test
        void 매장_주문을_수락한다() {
            given(orderRepository.findById(any())).willReturn(Optional.of(대기중인_매장주문(양념치킨_메뉴)));

            Order 수락한_주문 = orderService.accept(UUID.randomUUID());

            assertThat(수락한_주문.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void 매장_주문을_제공한다() {
            given(orderRepository.findById(any())).willReturn(Optional.of(수락된_매장_주문(양념치킨_메뉴)));

            Order 제공된_주문 = orderService.serve(UUID.randomUUID());

            assertThat(제공된_주문.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void 매장_주문을_완료한다() {
            given(orderRepository.existsByOrderTableAndStatusNot(any(),any())).willReturn(false);
            given(orderRepository.findById(any())).willReturn(Optional.of(매장_준비된_주문(양념치킨_메뉴, 착석_가능한_손님_2명의_오른쪽_테이블())));

            Order 완료된_매장_주문 = orderService.complete(UUID.randomUUID());

            assertThat(완료된_매장_주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    class 주문_전체기능 {

        @Test
        void 대기중이었던_주문만_수락할_수_있다() {
            UUID 주문_아이디 = UUID.randomUUID();
            given(orderRepository.findById(any())).willReturn(Optional.of(매장_주문(양념치킨_메뉴)));

            assertThatThrownBy(() -> orderService.accept(주문_아이디))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void 음식이_준비되지_않은_주문은_완료할_수_없다() {
            UUID 주문_아이디 = UUID.randomUUID();
            given(orderRepository.findById(any())).willReturn(Optional.of(매장_주문(양념치킨_메뉴)));

            assertThatThrownBy(() -> orderService.complete(주문_아이디))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Nested
        class 주문_등록_예외_테스트 {
            @Test
            void 주문_아이템이_없으면_등록할_수_없다() {
                Order 아이템이_없는_주문 = 아이템이_없는_주문();

                assertThatThrownBy(() -> orderService.create(아이템이_없는_주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 수량이_0개_미만인_포장주문은_등록할_수_없다() {
                Order 수량이_0개_미만_주문 = 상품_수량이_음수인_포장_주문(무료_메뉴());
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(무료_메뉴()));

                assertThatThrownBy(() -> orderService.create(수량이_0개_미만_주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 존재하지_않은_메뉴의_주문은_등록할_수_없다() {
                Order 수량이_0개_미만_주문 = 매장_주문(무료_메뉴());
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(무료_메뉴()));

                assertThatThrownBy(() -> orderService.create(수량이_0개_미만_주문))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            void 미노출_메뉴는_주문_등록할_수_없다() {
                Order 미노출_메뉴_주문 = 매장_주문(무료_메뉴());
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(무료_메뉴()));
                given(menuRepository.findById(any())).willReturn(Optional.of(무료_메뉴()));

                assertThatThrownBy(() -> orderService.create(미노출_메뉴_주문))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            void 메뉴의_가격과_상품가격이_다른면_등록할_수_없다() {
                Order 노출된_무료_메뉴 = 메뉴의_가격과_주문_아이템의_가격이_다른_주문(노출된_무료_메뉴());
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(노출된_무료_메뉴()));
                given(menuRepository.findById(any())).willReturn(Optional.of(노출된_무료_메뉴()));

                assertThatThrownBy(() -> orderService.create(노출된_무료_메뉴))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class 배달_주문_예외_테스트 {
            @Test
            void 배달_주문이_아니면_배달을_시작할_수_없다() {
                UUID 주문_아이디 = UUID.randomUUID();
                given(orderRepository.findById(any())).willReturn(Optional.of(매장_주문(양념치킨_메뉴)));

                assertThatThrownBy(() -> orderService.startDelivery(주문_아이디))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            void 음식이_준비되지_않으면_배달을_시작할_수_없다() {
                UUID 주문_아이디 = UUID.randomUUID();
                given(orderRepository.findById(any())).willReturn(Optional.of(대기중인_배달주문(양념치킨_메뉴)));

                assertThatThrownBy(() -> orderService.startDelivery(주문_아이디))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            void 배달중이_아닌_주문은_배달을_완료할_수_없다() {
                UUID 주문_아이디 = UUID.randomUUID();
                given(orderRepository.findById(any())).willReturn(Optional.of(대기중인_배달주문(양념치킨_메뉴)));

                assertThatThrownBy(() -> orderService.completeDelivery(주문_아이디))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            void 배달중인_주문은_완료_할_수_없다() {
                UUID 주문_아이디 = UUID.randomUUID();
                given(orderRepository.findById(any())).willReturn(Optional.of(배달중인_주문(양념치킨_메뉴)));

                assertThatThrownBy(() -> orderService.complete(주문_아이디))
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }

}

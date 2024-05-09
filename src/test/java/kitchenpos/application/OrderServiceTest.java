package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("주문 서비스 테스트")
@ApplicationMockTest
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("주문등록 테스트")
    class CreateOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문")
            @Test
            void eatInOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문")
            @Test
            void deliveryOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문")
            @Test
            void takeOutOrder() {
                // given

                // when

                // then

            }
        }
    }

    @Nested
    @DisplayName("주문수락 테스트")
    class AcceptOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문")
            @Test
            void eatInOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문")
            @Test
            void deliveryOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문")
            @Test
            void takeOutOrder() {
                // given

                // when

                // then

            }
        }
    }

    @Nested
    @DisplayName("제조완료 테스트")
    class ServedOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문")
            @Test
            void eatInOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문")
            @Test
            void deliveryOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문")
            @Test
            void takeOutOrder() {
                // given

                // when

                // then

            }
        }
    }

    @Nested
    @DisplayName("배달 테스트")
    class DeliveryOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문")
            @Test
            void eatInOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문")
            @Test
            void deliveryOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문")
            @Test
            void takeOutOrder() {
                // given

                // when

                // then

            }
        }
    }

    @Nested
    @DisplayName("주문종료 테스트")
    class CompleteOrder {

        @Nested
        @DisplayName("매장주문")
        class EatIn {

            @DisplayName("매장주문")
            @Test
            void eatInOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("배달주문")
        class Delivery {

            @DisplayName("배달주문")
            @Test
            void deliveryOrder() {
                // given

                // when

                // then

            }
        }

        @Nested
        @DisplayName("포장주문")
        class TakeOut {

            @DisplayName("포장주문")
            @Test
            void takeOutOrder() {
                // given

                // when

                // then

            }
        }
    }
}

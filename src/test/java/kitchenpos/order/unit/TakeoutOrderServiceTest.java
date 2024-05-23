package kitchenpos.order.unit;

import kitchenpos.application.OrderService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TakeoutOrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(
                orderRepository, menuRepository, orderTableRepository, kitchenridersClient
        );
    }

    @Nested
    class 등록 {

        @Test
        @DisplayName("[성공] 테이크아웃 주문을 등록한다.")
        void create() {

        }

        @Nested
        class 타입검증 {

            @Test
            @DisplayName("[실패] 주문 유형을 입력하지 않으면 등록하지 못한다.")
            void 주문유형_null() {

            }

        }

        @Nested
        class 주문항목목록검증 {

            @Test
            @DisplayName("[실패] 주문 항목 목록을 입력하지 않으면 등록하지 못한다.")
            void 주문항목목록_null() {

            }

            @Test
            @DisplayName("[실패] 주문 항목 목록이 비어있으면 등록하지 못한다.")
            void 주문항목목록_empty() {

            }

            @Test
            @DisplayName("[실패] 주문 항목 목록이 전부 메뉴에 등록되어 있어야 한다.")
            void 주문항목목록_미등록메뉴포함() {

            }
        }

        @Nested
        class 주문항목검증 {

            @Test
            @DisplayName("[실패] 수량이 마이너스인 주문 항목이 존재할 경우 등록하지 못한다.")
            void 주문항목_수량마이너스() {

            }

            @Test
            @DisplayName("[실패] 주문 항목이 미등록된 메뉴일 경우 등록하지 못한다.")
            void 주문항목_미등록메뉴() {

            }

            @Test
            @DisplayName("[실패] 주문 항목이 숨김처리된 메뉴일 경우 등록하지 못한다.")
            void 주문항목_숨김처리메뉴() {

            }

            @Test
            @DisplayName("[실패] 주문 항목의 가격과 메뉴의 가격이 동일하지 않으면 등록하지 못한다.")
            void 주문항목가격_메뉴가격_불일치() {

            }

        }

    }

    @Nested
    class 수락 {

        @Test
        @DisplayName("[성공] 주문을 수락한다.")
        void accept() {

        }

        @Nested
        class 주문등록여부검증 {

            @Test
            @DisplayName("[실패] 주문이 등록되어 있지 않으면 수락할 수 없다.")
            void 주문_미등록() {

            }

        }

        @Nested
        class 상태검증 {

            @Test
            @DisplayName("[실패] 주문 상태가 대기중 상태가 아니면 수락할 수 없다.")
            void 상태_not대기중() {

            }

        }

    }

    @Nested
    class 전달 {

        @Test
        @DisplayName("[성공] 주문한 음식들을 전달한다.")
        void serve() {

        }

        @Nested
        class 주문등록여부검증 {

            @Test
            @DisplayName("[실패] 주문이 등록되어 있지 않으면 전달할 수 없다.")
            void 주문_미등록() {

            }

        }

        @Nested
        class 상태검증 {

            @Test
            @DisplayName("[실패] 주문 상태가 수락 상태가 아니면 전달할 수 없다.")
            void 상태_not수락() {

            }

        }

    }

    @Nested
    class 완료 {

        @Test
        @DisplayName("[성공] 주문을 완료한다.")
        void complete() {

        }

        @Nested
        class 주문등록여부검증 {

            @Test
            @DisplayName("[실패] 주문이 등록되어 있지 않으면 완료할 수 없다.")
            void 주문_미등록() {

            }

        }

        @Nested
        class 상태검증 {

            @Test
            @DisplayName("[실패] 주문 상태가 전달 상태가 아니면 완료할 수 없다.")
            void 상태_not전달() {

            }

        }

    }

}

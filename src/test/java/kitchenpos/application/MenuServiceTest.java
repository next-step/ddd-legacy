package kitchenpos.application;

import config.UnitTest;
import kitchenpos.MenuFixture;
import kitchenpos.MenuGroupFixture;
import kitchenpos.MenuProductFixture;
import kitchenpos.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@UnitTest
@DisplayName("메뉴 서비스 테스트")
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService sut;

    @Test
    @DisplayName("메뉴 생성 - 성공: 유효한 입력 값으로 메뉴가 생성된다.")
    void createMenuSuccess() {
        // given
        // Fixture를 사용하여 유효한 메뉴 그룹, 메뉴, 상품 객체들을 생성
        MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
        Menu 후라이드_치킨_세트_메뉴 = MenuFixture.후라이드_치킨_메뉴();
        Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품();
        Product 콜라_상품 = ProductFixture.콜라_상품();

        // 메뉴에 메뉴 그룹 할당
        후라이드_치킨_세트_메뉴.setMenuGroup(추천_메뉴그룹);
        후라이드_치킨_세트_메뉴.setMenuGroupId(추천_메뉴그룹.getId());

        // Fixture를 사용하여 메뉴 구성 상품 설정 (예: 후라이드 치킨 1개, 콜라 2개)
        MenuProductFixture.후라이드_치킨_메뉴_구성_상품(후라이드_치킨_세트_메뉴, 후라이드_치킨_상품, 콜라_상품);

        String menuName = 후라이드_치킨_세트_메뉴.getName();
        BigDecimal menuPrice = 후라이드_치킨_세트_메뉴.getPrice();
        UUID menuGroupId = 추천_메뉴그룹.getId();
        // 구성 상품에 등록된 상품 ID 추출
        UUID 후라이드치킨상품Id = 후라이드_치킨_상품.getId();
        UUID 콜라상품Id = 콜라_상품.getId();

        // 메뉴 그룹 존재 검증
        given(menuGroupRepository.findById(menuGroupId))
                .willReturn(Optional.of(추천_메뉴그룹));

        // 상품 존재 검증: ProductFixture를 사용하여 상품들이 존재함을 설정
        given(productRepository.findAllByIdIn(List.of(후라이드치킨상품Id, 콜라상품Id)))
                .willReturn(List.of(후라이드_치킨_상품, 콜라_상품));
        given(productRepository.findById(후라이드치킨상품Id))
                .willReturn(Optional.of(후라이드_치킨_상품));
        given(productRepository.findById(콜라상품Id))
                .willReturn(Optional.of(콜라_상품));

        // 메뉴명 비속어 검증 (비속어 없음)
        given(purgomalumClient.containsProfanity(menuName)).willReturn(false);

        // 메뉴 저장 시 동작: Fixture를 사용하여 저장된 메뉴 객체 생성
        Menu expected = MenuFixture.후라이드_치킨_메뉴();
        expected.setMenuGroup(추천_메뉴그룹);
        given(menuRepository.save(any(Menu.class))).willReturn(expected);

        // when
        Menu result = sut.create(후라이드_치킨_세트_메뉴);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(menuName);
        assertThat(result.getPrice()).isEqualTo(menuPrice);
        verify(menuRepository).save(any(Menu.class));

    }

    @Test
    @DisplayName("메뉴 생성 - 실패: 요청 메뉴 가격이 0원 미만이면 IllegalArgumentException 발생")
    void createMenuWithNegativePriceThrowIllegalArgumentException() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("메뉴 생성 - 실패: 요청 메뉴 그룹이 없으면 NoSuchElementException 발생")
    void createMenuWithoutMenuGroupThrowNoSuchElementException() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("메뉴 생성 - 실패: 요청 메뉴 구성 상품이 하나도 없으면 IllegalArgumentException 발생")
    void createMenuWithoutMenuProductsThrowIllegalArgumentException() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("메뉴 생성 - 실패: 요청된 일부 메뉴 구성 상품 중, 실제 존재하지 않는 상품이 있으면 IllegalArgumentException 발생")
    void createMenuWithNonexistentMenuProductThrowIllegalArgumentException() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("메뉴 생성 - 실패: 메뉴 구성 상품의 수량이 음수이면 IllegalArgumentException 발생")
    void createMenuWithNegativeMenuProductQuantityThrowIllegalArgumentException() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("메뉴 생성 - 실패: 메뉴의 가격이 구성 상품 가격 총합보다 높으면 IllegalArgumentException 발생")
    void createMenuWithPriceHigherThanSumOfMenuProductPricesThrowIllegalArgumentException() {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("메뉴 생성 - 실패: 메뉴명이 없거나 비속어가 포함되면 IllegalArgumentException 발생")
    void createMenuWithEmptyOrProfanityNameThrowIllegalArgumentException() {
        // given
        // when
        // then
    }

}
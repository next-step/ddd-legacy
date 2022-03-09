package kitchenpos.application;

import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @InjectMocks
    MenuService menuService;
    @Mock
    MenuRepository menuRepository;
    @Mock
    MenuGroupRepository menuGroupRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    PurgomalumClient 비속어_판별기;

    @DisplayName(value = "메뉴를 등록 할 수 있다")
    @Test
    void create_success() {
        //given

        //when

        //then

    }

    @DisplayName(value = "반드시 0원 이상의 메뉴가격을 가져야 한다")
    @Test
    void create_fail_invalid_price() {
        //given

        //when

        //then

    }

    @DisplayName(value = "메뉴는 반드시 하나의 메뉴그룹을 지정하여 등록해야 한다")
    @Test
    void create_fail_menu_should_belongs_to_menu_group() {
        //given

        //when

        //then

    }

    @DisplayName(value = "메뉴는 반드시 하나 이상의 메뉴구성상품(menuProduct)을 포함하고 있어야 한다")
    @Test
    void create_fail_menu_should_have_gt_1_menu_product() {
        //given

        //when

        //then

    }

    @DisplayName(value = "상품(product)의 개수와 메뉴상품구성(menuProduct)의 개수는 일치해야 한다")
    @Test
    void create_fail_menu_product_size_should_eq_product_size() {
        //given

        //when

        //then

    }

    @DisplayName(value = "각 메뉴구성상품(menuProduct)의 양(quantity)은 0이상이어야 한다")
    @Test
    void create_fail_menu_product_quantity_should_gt_0() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴는 존재하는 상품만 포함할 수 있다")
    @Test
    void create_fail_product_no_exist() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴의 가격이 각 메뉴구성상품의 합보다 클 수 없다")
    @Test
    void create_fail_menu_price_shoud_lt_sum_of_menu_product() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴는 반드시 메뉴명을 가지고 있어야 하며, 비속어를 포함할 수 없다")
    @Test
    void create_fail_invalid_name() {
        //given

        //when

        //then
    }

    @DisplayName(value = "변경하려는 메뉴의 가격은 존재해야하며, 0원 이상이어야 한다")
    @Test
    void changePrice_fail_invalid_menu_price() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴 가격을 각 메뉴구성상품 가격의 합보다 크게 변경할 수 없다")
    @Test
    void changePrice_fail_menu_price_gt_sum_of_menu_product_price() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴의 판매상태를 판매중으로 변경할 수 있다")
    @Test
    void display_success() {
        //given

        //when

        //then
    }

    @DisplayName(value = "존재하는 메뉴만 판매상태를 판매중으로 변경할 수 있다")
    @Test
    void display_fail_menu_not_exist() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴 가격이 각 메뉴구성상품 가격의 합보다 큰경우 판매중으로 변경할 수 없다")
    @Test
    void display_fail_menu_price_gt_sum_of_menu_product() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴의 판매상태를 판매중단으로 변경할 수 있다")
    @Test
    void hide_success() {
        //given

        //when

        //then
    }

    @DisplayName(value = "존재하는 메뉴만 판매상태를 판매중단으로 변경할 수 있다")
    @Test
    void hide_fail_menu_not_exist() {
        //given

        //when

        //then
    }

    @DisplayName(value = "전체 메뉴를 조회할 수 있다")
    @Test
    void findAll_success() {
        //given

        //when

        //then
    }
}
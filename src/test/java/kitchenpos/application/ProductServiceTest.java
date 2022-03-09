package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.KitchenposFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static kitchenpos.fixture.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("상품의 가격을 변경했을 때 변경된 상품을 포함한 메뉴의 가격이 메뉴의 모든 상품의 가격을 더한 가격보다 작으면 메뉴를 전시하지 않는다.")
    @Test
    void changePriceWithMenu() {
        // 상품 생성
        Product chicken = chickenProduct();
        chicken = productRepository.save(chicken);

        Product pasta = pastaProduct();
        pasta = productRepository.save(pasta);

        // 메뉴 그룹 생성
        MenuGroup menuGroup = menuGroup();
        menuGroup = menuGroupRepository.save(menuGroup);

        // 메뉴 생성
        Menu menu = KitchenposFixture.menu(menuGroup, chicken, pasta);
        menuRepository.save(menu);

        // when
        chicken.setPrice(BigDecimal.valueOf(15000));
        productService.changePrice(chicken.getId(), chicken);

        // then
        menu = menuRepository.findById(menu.getId()).get();
        assertThat(menu.isDisplayed()).isFalse();
    }
}

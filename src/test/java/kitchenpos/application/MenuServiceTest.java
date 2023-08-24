package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import kitchenpos.integration_test_step.MenuGroupIntegrationStep;
import kitchenpos.integration_test_step.ProductIntegrationStep;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuService 클래스")
@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService sut;

    @Autowired
    private ProductIntegrationStep productIntegrationStep;

    @Autowired
    private MenuGroupIntegrationStep menuGroupIntegrationStep;

    @Autowired
    private DatabaseCleanStep databaseCleanStep;

    @BeforeEach
    void setUp() {
        databaseCleanStep.clean();
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        // given
        Product product = productIntegrationStep.createPersistProduct();
        MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
        MenuProduct menuProduct = MenuProductTestFixture.create()
                .changeProduct(product)
                .getMenuProduct();
        Menu menu = MenuTestFixture.create()
                .changeMenuGroup(menuGroup)
                .changeMenuProducts(Collections.singletonList(menuProduct))
                .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .getMenu();

        // when
        Menu result = sut.create(menu);

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertThat(result.getName()).isEqualTo("테스트 메뉴");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(result.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
        assertThat(result.getMenuProducts())
                .extracting("seq")
                .containsAnyOf(menuProduct.getSeq());
        assertThat(result.isDisplayed()).isTrue();
    }
}
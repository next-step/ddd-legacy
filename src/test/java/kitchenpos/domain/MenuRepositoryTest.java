package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MenuRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("메뉴 아이디 목록으로 메뉴 목록을 조회한다.")
    @Test
    void findAllByIdIn() {
        // given
        var menuGroup = createFixMenuGroup("치킨");

        entityManager.persist(menuGroup);

        var menu1 = createFixMenu("후라이드 치킨", 16000, menuGroup);
        var menu2 = createFixMenu("양념 치킨", 16000, menuGroup);
        var menu3 = createFixMenu("허니 콤보", 19500, menuGroup);

        entityManager.persist(menu1);
        entityManager.persist(menu2);
        entityManager.persist(menu3);

        entityManager.flush();

        // when
        var result = menuRepository.findAllByIdIn(
                List.of(menu1.getId(), menu2.getId()));

        // then
        assertThat(result).containsExactlyInAnyOrder(menu1, menu2);
    }

    @DisplayName("상품 아이디로 메뉴 목록을 조회한다.")
    @Test
    void findAllByProductId() {
        // given
        var product1 = createFixProduct("후라이드 치킨", 16000);
        var product2 = createFixProduct("양념 치킨", 16000);
        var product3 = createFixProduct("허니 콤보", 19500);

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);

        var menuGroup = createFixMenuGroup("치킨");

        entityManager.persist(menuGroup);

        var menu1 = createFixMenu("후라이드 치킨", 16000, menuGroup);
        setProductToMenuProducts(product1, menu1);

        var menu2 = createFixMenu("양념 치킨", 16000, menuGroup);
        setProductToMenuProducts(product2, menu2);

        var menu3 = createFixMenu("허니 콤보", 19500, menuGroup);
        setProductToMenuProducts(product1, menu3);

        entityManager.persist(menu1);
        entityManager.persist(menu2);
        entityManager.persist(menu3);

        entityManager.flush();

        // when
        var result = menuRepository.findAllByProductId(product1.getId());

        // then
        assertThat(result).containsExactlyInAnyOrder(menu1, menu3);
    }

    private static void setProductToMenuProducts(Product product, Menu menu) {
        var menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
        menuProduct.setProductId(product.getId());
        menu.setMenuProducts(List.of(menuProduct));
    }

    private static @NotNull MenuGroup createFixMenuGroup(String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    private static @NotNull Product createFixProduct(String name, int val) {
        var product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setName(name);
        product1.setPrice(BigDecimal.valueOf(val));
        return product1;
    }

    private static @NotNull Menu createFixMenu(String name, int val, MenuGroup menuGroup) {
        var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(val));
        menu.setDisplayed(true);
        menu.setMenuGroup(menuGroup);
        return menu;
    }
}

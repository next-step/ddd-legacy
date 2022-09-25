package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FakeMenuRepositoryTest {

    // SUT

    private final FakeMenuRepository fakeMenuRepository = new FakeMenuRepository();

    @DisplayName("메뉴가 저장되어야 한다.")
    @ValueSource(strings = {
            "run", "low", "possession", "cousin", "sailor",
            "matter", "car", "few", "broadcast", "loose",
    })
    @ParameterizedTest
    void tduwowoa(String name) {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);

        // when
        final Menu savedMenu = this.fakeMenuRepository.save(menu);

        // then
        Assertions.assertThat(savedMenu).isEqualTo(menu);
    }

    @DisplayName("저장된 메뉴는 ID로 찾을 수 있어야 한다.")
    @Test
    void ubfedggr() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("instead");
        final Menu savedMenu = this.fakeMenuRepository.save(menu);

        // when
        final Menu foundMenu = this.fakeMenuRepository.findById(savedMenu.getId())
                .orElse(null);

        // then
        assertThat(foundMenu).isEqualTo(menu);
    }

    @DisplayName("저장되지 않은 메뉴는 ID로 찾을 수 없어야 한다.")
    @Test
    void bcatfglu() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("instead");
        this.fakeMenuRepository.save(menu);

        // when
        final Menu foundMenu = this.fakeMenuRepository.findById(UUID.randomUUID())
                .orElse(null);

        // then
        assertThat(foundMenu).isNull();
    }

    @DisplayName("빈 상태에서 모두 조회시 빈 List가 반환되어야 한다.")
    @Test
    void wgcjljzb() {
        // when
        final List<Menu> menus = this.fakeMenuRepository.findAll();

        // then
        assertThat(menus).isEmpty();
    }

    @DisplayName("모두 조회시 저장된 수 만큼 조회되어야 한다.")
    @ValueSource(ints = {
            31, 30, 26, 29, 19,
            20, 31, 22, 12, 27,
    })
    @ParameterizedTest
    void kqowhpcf(int size) {
        // given
        for (int i = 0; i < size; i++) {
            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName(String.valueOf(i));
            this.fakeMenuRepository.save(menu);
        }

        // when
        final List<Menu> menus = this.fakeMenuRepository.findAll();

        // then
        assertThat(menus).hasSize(size);
    }

    @DisplayName("여러 ID로 찾을 수 있어야 한다.")
    @Test
    void xztanlka() {
        // given
        final List<Menu> menus = IntStream.range(0, 10)
                .mapToObj(n -> {
                    final Menu menu = new Menu();
                    menu.setId(UUID.randomUUID());
                    return this.fakeMenuRepository.save(menu);
                })
                .collect(Collectors.toUnmodifiableList());

        final int[] indices = {3, 6, 9};

        final List<Menu> menusToBeFound = Arrays.stream(indices)
                .mapToObj(menus::get)
                .collect(Collectors.toUnmodifiableList());

        final List<UUID> ids = menusToBeFound.stream()
                .map(Menu::getId)
                .collect(Collectors.toUnmodifiableList());

        // when
        final List<Menu> foundMenus = this.fakeMenuRepository.findAllByIdIn(ids);

        // then
        assertThat(foundMenus).hasSize(3).containsAll(menusToBeFound);
    }

    @DisplayName("메뉴에 포함된 상품 ID로 찾을 수 있어야 한다.")
    @Test
    void emocchhz() {
        // given
        final UUID[] productIds = {
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
        };

        final List<Menu> menus = IntStream.range(0, 10)
                .mapToObj(n -> {
                    final List<MenuProduct> menuProducts = new ArrayList<>();
                    final MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProductId(productIds[n % 3]);
                    menuProducts.add(menuProduct);

                    final Menu menu = new Menu();
                    menu.setId(UUID.randomUUID());
                    menu.setMenuProducts(menuProducts);
                    return this.fakeMenuRepository.save(menu);
                })
                .collect(Collectors.toUnmodifiableList());

        final List<Menu> menusToBeFound = IntStream.range(0, 10)
                .filter(n -> (n % 3) == 1)
                .mapToObj(menus::get)
                .collect(Collectors.toUnmodifiableList());

        // when
        final List<Menu> foundMenus = this.fakeMenuRepository.findAllByProductId(productIds[1]);

        // then
        assertThat(foundMenus).hasSize(3).containsAll(menusToBeFound);
    }
}

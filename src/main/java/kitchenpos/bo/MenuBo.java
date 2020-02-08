package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class MenuBo {
    private final MenuDao menuDao;
    private final MenuGroupDao menuGroupDao;
    private final MenuProductDao menuProductDao;
    private final ProductDao productDao;

    public MenuBo(
            final MenuDao menuDao,
            final MenuGroupDao menuGroupDao,
            final MenuProductDao menuProductDao,
            final ProductDao productDao
    ) {
        this.menuDao = menuDao;
        this.menuGroupDao = menuGroupDao;
        this.menuProductDao = menuProductDao;
        this.productDao = productDao;
    }

    /**
     * 메뉴 생성
     *
     * @param menu
     * @return
     */
    @Transactional
    public Menu create(final Menu menu) {
        final BigDecimal price = menu.getPrice();

        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) { // 가격 유효성 체크 (0원 이상)
            throw new IllegalArgumentException();
        }

        if (!menuGroupDao.existsById(menu.getMenuGroupId())) { // 메뉴그룹 유효성 체크 (존재여부)
            throw new IllegalArgumentException();
        }

        final List<MenuProduct> menuProducts = menu.getMenuProducts();

        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProduct menuProduct : menuProducts) {
            final Product product = productDao.findById(menuProduct.getProductId()) // 제품 조회
                    .orElseThrow(IllegalArgumentException::new); // 존재하지 않으면 에러
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity()))); // 구성된 메뉴제품들의 가격 총합을 구한다.
        }

        if (price.compareTo(sum) > 0) { // 메뉴 가격은 구성된 메뉴제품들의 가격 총합을 초과할 수 없다.
            throw new IllegalArgumentException();
        }

        final Menu savedMenu = menuDao.save(menu); // 메뉴 저장

        final Long menuId = savedMenu.getId();
        final List<MenuProduct> savedMenuProducts = new ArrayList<>();
        for (final MenuProduct menuProduct : menuProducts) {
            menuProduct.setMenuId(menuId);
            savedMenuProducts.add(menuProductDao.save(menuProduct)); // 구성된 메뉴제품 저장
        }
        savedMenu.setMenuProducts(savedMenuProducts);

        return savedMenu;
    }

    /**
     * 전체 메뉴 리스트 조회
     *
     * @return
     */
    public List<Menu> list() {
        final List<Menu> menus = menuDao.findAll(); // 전체 메뉴 조회

        for (final Menu menu : menus) {
            menu.setMenuProducts(menuProductDao.findAllByMenuId(menu.getId())); // 메뉴 내 제품 조회
        }

        return menus;
    }
}

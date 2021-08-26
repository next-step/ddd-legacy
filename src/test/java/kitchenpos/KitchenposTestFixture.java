package kitchenpos;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

public class KitchenposTestFixture {

    public static Product 후라이드 = new Product();
    public static Product 양념치킨 = new Product();
    public static Product 반반치킨 = new Product();
    public static Product 통구이 = new Product();
    public static Product 간장치킨 = new Product();
    public static Product 순살치킨 = new Product();

    public static MenuGroup 두마리메뉴 = new MenuGroup();
    public static MenuGroup 한마리메뉴 = new MenuGroup();
    public static MenuGroup 순살파닭두마리메뉴 = new MenuGroup();
    public static MenuGroup 신메뉴 = new MenuGroup();

    public static MenuProduct 후라이드치킨_후라이드 = new MenuProduct();
    public static MenuProduct 양념치킨_양념치킨 = new MenuProduct();
    public static MenuProduct 반반치킨_반반치킨 = new MenuProduct();
    public static MenuProduct 통구이_통구이 = new MenuProduct();
    public static MenuProduct 간장치킨_간장치킨 = new MenuProduct();
    public static MenuProduct 순살치킨_순살치킨 = new MenuProduct();

    public static Menu 후라이드치킨_Menu = new Menu();
    public static Menu 양념치킨_Menu = new Menu();
    public static Menu 반반치킨_Menu = new Menu();
    public static Menu 통구이_Menu = new Menu();
    public static Menu 간장치킨_Menu = new Menu();
    public static Menu 순살치킨_Menu = new Menu();

    public static Menu 비싼후라이드치킨 = new Menu();

    public static OrderTable 테이블1번 = new OrderTable();
    public static OrderTable 테이블2번 = new OrderTable();
    public static OrderTable 테이블3번 = new OrderTable();
    public static OrderTable 테이블4번 = new OrderTable();
    public static OrderTable 테이블5번 = new OrderTable();
    public static OrderTable 테이블6번 = new OrderTable();
    public static OrderTable 테이블7번 = new OrderTable();
    public static OrderTable 테이블8번 = new OrderTable();

    public static Order 주문1번 = new Order();
    public static Order 주문2번 = new Order();
    public static Order 주문3번 = new Order();

    public static OrderLineItem 주문1번_상세1 = new OrderLineItem();
    public static OrderLineItem 주문2번_상세1 = new OrderLineItem();
    public static OrderLineItem 주문3번_상세1 = new OrderLineItem();

    public static void set() {
        initProduct();
        initMenuGroup();
        initMenuProduct();
        initMenu();

        initOrderTable();
        initOrderLineItem();
        initOrder();
    }

    public static UUID uuidOf(String withoutDashes) {
        BigInteger bi1 = new BigInteger(withoutDashes.substring(0, 16), 16);
        BigInteger bi2 = new BigInteger(withoutDashes.substring(16, 32), 16);
        return new UUID(bi1.longValue(), bi2.longValue());
    }

    public static LocalDateTime dateTimeOf(String withDashes) {
        return LocalDate.parse(withDashes, ofPattern("yyyy-MM-dd")).atStartOfDay();
    }

    private static void initOrder() {
        주문1번.setId(uuidOf("69d78f383bff457cbb7226319c985fd8"));
        주문1번.setDeliveryAddress("서울시 송파구 위례성대로 2");
        주문1번.setOrderDateTime(dateTimeOf("2021-07-27"));
        주문1번.setStatus(OrderStatus.WAITING);
        주문1번.setType(OrderType.DELIVERY);
        주문1번.setOrderTable(null);
        주문1번.setOrderTableId(null);

        주문2번.setId(uuidOf("98da3d3859e04dacbbaeebf6560a43bd"));
        주문2번.setDeliveryAddress(null);
        주문2번.setOrderDateTime(dateTimeOf("2021-07-27"));
        주문2번.setStatus(OrderStatus.COMPLETED);
        주문2번.setType(OrderType.EAT_IN);
        주문2번.setOrderTable(테이블1번);
        주문2번.setOrderTableId(테이블1번.getId());

        주문3번.setId(uuidOf("d7cc15b3e32c4bc8b440d3067b35522e"));
        주문3번.setDeliveryAddress(null);
        주문3번.setOrderDateTime(dateTimeOf("2021-07-27"));
        주문3번.setStatus(OrderStatus.COMPLETED);
        주문3번.setType(OrderType.EAT_IN);
        주문3번.setOrderTable(테이블1번);
        주문3번.setOrderTableId(테이블1번.getId());
    }

    private static void initOrderLineItem() {
        주문1번_상세1.setSeq(1L);
        주문1번_상세1.setQuantity(1L);
        주문1번_상세1.setMenu(후라이드치킨_Menu);
        주문1번_상세1.setMenuId(후라이드치킨_Menu.getId());
        주문1번_상세1.setPrice(후라이드치킨_Menu.getPrice());

        주문2번_상세1.setSeq(2L);
        주문2번_상세1.setQuantity(1L);
        주문2번_상세1.setMenu(후라이드치킨_Menu);
        주문2번_상세1.setMenuId(후라이드치킨_Menu.getId());
        주문2번_상세1.setPrice(후라이드치킨_Menu.getPrice());

        주문3번_상세1.setSeq(3L);
        주문3번_상세1.setQuantity(1L);
        주문3번_상세1.setMenu(후라이드치킨_Menu);
        주문3번_상세1.setMenuId(후라이드치킨_Menu.getId());
        주문3번_상세1.setPrice(후라이드치킨_Menu.getPrice());
    }

    private static void initOrderTable() {
        테이블1번.setId(uuidOf("8d71004329b6420e8452233f5a035520"));
        테이블1번.setEmpty(true);
        테이블1번.setName("1번");
        테이블1번.setNumberOfGuests(0);

        테이블2번.setId(uuidOf("6ab59e8106eb441684e99faabc87c9ca"));
        테이블2번.setEmpty(true);
        테이블2번.setName("2번");
        테이블2번.setNumberOfGuests(0);

        테이블3번.setId(uuidOf("ae92335ccd264626b7979e4ae8c4efbd"));
        테이블3번.setEmpty(true);
        테이블3번.setName("3번");
        테이블3번.setNumberOfGuests(0);

        테이블4번.setId(uuidOf("a9858d4b80d0428881f48f41596a23fb"));
        테이블4번.setEmpty(true);
        테이블4번.setName("4번");
        테이블4번.setNumberOfGuests(0);

        테이블5번.setId(uuidOf("3faec3ab5217405daaa2804f87697f84"));
        테이블5번.setEmpty(true);
        테이블5번.setName("5번");
        테이블5번.setNumberOfGuests(0);

        테이블6번.setId(uuidOf("815b8395a2ad4e3589dc74c3b2191478"));
        테이블6번.setEmpty(true);
        테이블6번.setName("6번");
        테이블6번.setNumberOfGuests(0);

        테이블7번.setId(uuidOf("7ce8b3a235454542ab9cb3d493bbd4fb"));
        테이블7번.setEmpty(true);
        테이블7번.setName("7번");
        테이블7번.setNumberOfGuests(0);

        테이블8번.setId(uuidOf("7bdb1ffde36e4e2b94e3d2c14d391ef3"));
        테이블8번.setEmpty(true);
        테이블8번.setName("8번");
        테이블8번.setNumberOfGuests(0);
    }

    private static void initMenu() {
        후라이드치킨_Menu.setId(uuidOf("f59b1e1cb145440aaa6f6095a0e2d63b"));
        후라이드치킨_Menu.setDisplayed(true);
        후라이드치킨_Menu.setName("후라이드치킨");
        후라이드치킨_Menu.setPrice(BigDecimal.valueOf(16000L));
        후라이드치킨_Menu.setMenuGroup(한마리메뉴);
        후라이드치킨_Menu.setMenuGroupId(한마리메뉴.getId());
        후라이드치킨_Menu.setMenuProducts(Collections.singletonList(후라이드치킨_후라이드));

        양념치킨_Menu.setId(uuidOf("e1254913860846aab23aa07c1dcbc648"));
        양념치킨_Menu.setDisplayed(true);
        양념치킨_Menu.setName("양념치킨");
        양념치킨_Menu.setPrice(BigDecimal.valueOf(16000L));
        양념치킨_Menu.setMenuGroup(한마리메뉴);
        양념치킨_Menu.setMenuGroupId(한마리메뉴.getId());
        양념치킨_Menu.setMenuProducts(Collections.singletonList(양념치킨_양념치킨));

        반반치킨_Menu.setId(uuidOf("191fa247b5f34b51b175e65db523f754"));
        반반치킨_Menu.setDisplayed(true);
        반반치킨_Menu.setName("반반치킨");
        반반치킨_Menu.setPrice(BigDecimal.valueOf(16000L));
        반반치킨_Menu.setMenuGroup(한마리메뉴);
        반반치킨_Menu.setMenuGroupId(한마리메뉴.getId());
        반반치킨_Menu.setMenuProducts(Collections.singletonList(반반치킨_반반치킨));

        통구이_Menu.setId(uuidOf("33e558df7d934622b50efcc4282cd184"));
        통구이_Menu.setDisplayed(true);
        통구이_Menu.setName("통구이");
        통구이_Menu.setPrice(BigDecimal.valueOf(16000L));
        통구이_Menu.setMenuGroup(한마리메뉴);
        통구이_Menu.setMenuGroupId(한마리메뉴.getId());
        통구이_Menu.setMenuProducts(Collections.singletonList(통구이_통구이));

        간장치킨_Menu.setId(uuidOf("b9c670b04ef5409083496868df1c7d62"));
        간장치킨_Menu.setDisplayed(true);
        간장치킨_Menu.setName("간장치킨");
        간장치킨_Menu.setPrice(BigDecimal.valueOf(17000L));
        간장치킨_Menu.setMenuGroup(한마리메뉴);
        간장치킨_Menu.setMenuGroupId(한마리메뉴.getId());
        간장치킨_Menu.setMenuProducts(Collections.singletonList(간장치킨_간장치킨));

        순살치킨_Menu.setId(uuidOf("a64af6cac34d4cd882fe454abf512d1f"));
        순살치킨_Menu.setDisplayed(true);
        순살치킨_Menu.setName("순살치킨");
        순살치킨_Menu.setPrice(BigDecimal.valueOf(17000L));
        순살치킨_Menu.setMenuGroup(한마리메뉴);
        순살치킨_Menu.setMenuGroupId(한마리메뉴.getId());
        순살치킨_Menu.setMenuProducts(Collections.singletonList(순살치킨_순살치킨));

        비싼후라이드치킨.setId(uuidOf("f76c720e8c1346739a24cf385654159d"));
        비싼후라이드치킨.setDisplayed(false);
        비싼후라이드치킨.setName("비싼후라이드치킨");
        비싼후라이드치킨.setPrice(BigDecimal.valueOf(16001L));
        비싼후라이드치킨.setMenuGroup(한마리메뉴);
        비싼후라이드치킨.setMenuGroupId(한마리메뉴.getId());
        비싼후라이드치킨.setMenuProducts(Collections.singletonList(후라이드치킨_후라이드));
    }

    private static void initMenuProduct() {
        후라이드치킨_후라이드.setSeq(1L);
        후라이드치킨_후라이드.setQuantity(1L);
        후라이드치킨_후라이드.setProductId(후라이드.getId());
        후라이드치킨_후라이드.setProduct(후라이드);

        양념치킨_양념치킨.setSeq(2L);
        양념치킨_양념치킨.setQuantity(1L);
        양념치킨_양념치킨.setProductId(양념치킨.getId());
        양념치킨_양념치킨.setProduct(양념치킨);

        반반치킨_반반치킨.setSeq(3L);
        반반치킨_반반치킨.setQuantity(1L);
        반반치킨_반반치킨.setProductId(반반치킨.getId());
        반반치킨_반반치킨.setProduct(반반치킨);

        통구이_통구이.setSeq(4L);
        통구이_통구이.setQuantity(1L);
        통구이_통구이.setProductId(통구이.getId());
        통구이_통구이.setProduct(통구이);

        간장치킨_간장치킨.setSeq(5L);
        간장치킨_간장치킨.setQuantity(1L);
        간장치킨_간장치킨.setProductId(간장치킨.getId());
        간장치킨_간장치킨.setProduct(간장치킨);

        순살치킨_순살치킨.setSeq(6L);
        순살치킨_순살치킨.setQuantity(1L);
        순살치킨_순살치킨.setProductId(순살치킨.getId());
        순살치킨_순살치킨.setProduct(순살치킨);
    }

    private static void initMenuGroup() {
        두마리메뉴.setId(uuidOf("f1860abc2ea1411bbd4abaa44f0d5580"));
        두마리메뉴.setName("두마리메뉴");

        한마리메뉴.setId(uuidOf("cbc75faefeb04bb18be2cb8ce5d8fded"));
        한마리메뉴.setName("한마리메뉴");

        순살파닭두마리메뉴.setId(uuidOf("5e9879b761124791a4cef22e94af8752"));
        순살파닭두마리메뉴.setName("순살파닭두마리메뉴");

        신메뉴.setId(uuidOf("d9bc21accc104593b5064a40e0170e02"));
        신메뉴.setName("신메뉴");
    }

    private static void initProduct() {
        후라이드.setId(uuidOf("3b52824434f7406bbb7e690912f66b10"));
        후라이드.setName("후라이드");
        후라이드.setPrice(BigDecimal.valueOf(16000L));

        양념치킨.setId(uuidOf("c5ee925c3dbb4941b825021446f24446"));
        양념치킨.setName("양념치킨");
        양념치킨.setPrice(BigDecimal.valueOf(16000L));

        반반치킨.setId(uuidOf("625c6fc4145d408f8dd533c16ba26064"));
        반반치킨.setName("반반치킨");
        반반치킨.setPrice(BigDecimal.valueOf(16000L));

        통구이.setId(uuidOf("4721ee722ff3417fade3acd0a804605b"));
        통구이.setName("통구이");
        통구이.setPrice(BigDecimal.valueOf(16000L));

        간장치킨.setId(uuidOf("0ac16db71b024a87b9c1e7d8f226c48d"));
        간장치킨.setName("간장치킨");
        간장치킨.setPrice(BigDecimal.valueOf(17000L));

        순살치킨.setId(uuidOf("7de4b8affa0f4391aaa9c61ea9b40f83"));
        순살치킨.setName("순살치킨");
        순살치킨.setPrice(BigDecimal.valueOf(17000L));
    }
}

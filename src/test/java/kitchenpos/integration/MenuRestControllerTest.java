package kitchenpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.config.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.util.MockMvcUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.fixture.MenuFixture.createMenuWithId;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupWithId;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@IntegrationTest
class MenuRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PurgomalumClient purgomalumClient;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    MenuRepository menuRepository;

    @Test
    void createTest() throws Exception {
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
        Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
        MenuProduct menuProduct = createMenuProduct(product, 1);

        MvcResult result = mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createMenuWithId(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(menuProduct)))))
                .andReturn();

        Menu menu = MockMvcUtil.readValue(objectMapper, result, Menu.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/menus/" + menu.getId()),
                () -> assertThat(menu.getName()).isEqualTo("후라이드치킨"),
                () -> assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(16000)),
                () -> assertThat(menu.getMenuGroup().getId()).isEqualTo(menuGroup.getId()),
                () -> assertThat(menu.getMenuProducts()).hasSize(1),
                () -> assertThat(menu.getMenuProducts().stream().map(it -> it.getProduct().getId())).contains(product.getId())
        );
    }

    @Test
    void changePriceTest() throws Exception {
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
        Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
        MenuProduct menuProduct = createMenuProduct(product, 1);

        Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(menuProduct)));

        menu.setPrice(BigDecimal.valueOf(15000));

        MvcResult result = mockMvc.perform(put("/api/menus/" + menu.getId() + "/price")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(menu)))
                .andReturn();

        Menu updatedMenu = MockMvcUtil.readValue(objectMapper, result, Menu.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedMenu.getPrice()).isEqualTo(BigDecimal.valueOf(15000))
        );
    }

    @Test
    void displayTest() throws Exception {
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
        Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
        MenuProduct menuProduct = createMenuProduct(product, 1);

        Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), false, List.of(menuProduct)));

        MvcResult result = mockMvc.perform(put("/api/menus/" + menu.getId() + "/display")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(menu)))
                .andReturn();

        Menu updatedMenu = MockMvcUtil.readValue(objectMapper, result, Menu.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedMenu.isDisplayed()).isTrue()
        );
    }

    @Test
    void hideTest() throws Exception {
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
        Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
        MenuProduct menuProduct = createMenuProduct(product, 1);

        Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(menuProduct)));

        MvcResult result = mockMvc.perform(put("/api/menus/" + menu.getId() + "/hide")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(menu)))
                .andReturn();

        Menu updatedMenu = MockMvcUtil.readValue(objectMapper, result, Menu.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedMenu.isDisplayed()).isFalse()
        );
    }

    @Test
    void findAllTest() throws Exception {
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
        Product product = productRepository.save(createProductWithId("후라이드치킨", BigDecimal.valueOf(16000)));
        MenuProduct menuProduct = createMenuProduct(product, 1);

        Menu menu = menuRepository.save(createMenuWithId(menuGroup, "후라이드치킨", BigDecimal.valueOf(16000), true, List.of(menuProduct)));

        MvcResult result = mockMvc.perform(get("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        List<Menu> menus = MockMvcUtil.readListValue(objectMapper, result, Menu.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(menus).hasSize(1),
                () -> assertThat(menus.stream().map(Menu::getId)).contains(menu.getId())
        );
    }
}

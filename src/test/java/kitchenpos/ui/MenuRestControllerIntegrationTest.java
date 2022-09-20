package kitchenpos.ui;

import static kitchenpos.fixtures.MenuFixtures.createMenu;
import static kitchenpos.fixtures.MenuFixtures.createMenuGroup;
import static kitchenpos.fixtures.MenuFixtures.createMenuProduct;
import static kitchenpos.fixtures.MenuFixtures.createProduct;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MenuRestControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private MenuGroupRepository menuGroupRepository;

  @Autowired
  private ProductRepository productRepository;

  @AfterEach
  void tearDown() {
    // menuRepository.deleteAll();
  }

  @DisplayName("유효한 메뉴 등록 요청에 응답 201 Created 및 등록된 메뉴를 반환한다")
  @Test
  void givenValidMenu_whenCreateMenu_thenStatus201WithMenu() throws Exception {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));

    MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
    Product savedProduct1 = productRepository.save(product1);
    Product savedProduct2 = productRepository.save(product2);

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        savedMenuGroup,
        List.of(
            createMenuProduct(savedProduct1, 1),
            createMenuProduct(savedProduct2, 1)
        )
    );

    mvc.perform(
            post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(menu.getName()))
        .andExpect(jsonPath("$.price").value(menu.getPrice().longValue()))
        .andExpect(jsonPath("$.displayed").value(menu.isDisplayed()));
  }

  @DisplayName("메뉴가격변경 요청에 응답으로 HTTP 200 상태값과 함께 변경된 메뉴를 반환한다")
  @Test
  void givenValidChangeMenu_whenChangePrice_thenStatus200WithChangedMenu() throws Exception {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));

    MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
    Product savedProduct1 = productRepository.save(product1);
    Product savedProduct2 = productRepository.save(product2);

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        savedMenuGroup,
        List.of(
            createMenuProduct(savedProduct1, 1),
            createMenuProduct(savedProduct2, 1)
        )
    );

    Menu savedMenu = menuRepository.save(menu);

    Menu changePriceMenu = new Menu();
    changePriceMenu.setPrice(BigDecimal.valueOf(22000));

    mvc.perform(
            put("/api/menus/{menuId}/price", savedMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePriceMenu)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(savedMenu.getId().toString()))
        .andExpect(jsonPath("$.name").value(menu.getName()))
        .andExpect(jsonPath("$.price").value(changePriceMenu.getPrice().longValue()))
        .andExpect(jsonPath("$.displayed").value(menu.isDisplayed()));
  }

  @DisplayName("메뉴 진열 요청 응답으로 HTTP 200 상태값과 함께 진열된 메뉴를 반환한다")
  @Test
  void givenMenu_whenDisplayMenu_thenStatus200WithDisplayedMenu() throws Exception {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));

    MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
    Product savedProduct1 = productRepository.save(product1);
    Product savedProduct2 = productRepository.save(product2);

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        false,
        savedMenuGroup,
        List.of(
            createMenuProduct(savedProduct1, 1),
            createMenuProduct(savedProduct2, 1)
        )
    );

    Menu savedMenu = menuRepository.save(menu);

    mvc.perform(
            put("/api/menus/{menuId}/display", savedMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(savedMenu.getId().toString()))
        .andExpect(jsonPath("$.name").value(menu.getName()))
        .andExpect(jsonPath("$.price").value(menu.getPrice().longValue()))
        .andExpect(jsonPath("$.displayed").value(true));
  }

  @DisplayName("메뉴 숨김 요청 응답으로 HTTP 200 상태값과 함께 숨겨진 메뉴를 반환한다")
  @Test
  void givenMenu_whenHideMenu_thenStatus200WithHiddenMenu() throws Exception {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));

    MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
    Product savedProduct1 = productRepository.save(product1);
    Product savedProduct2 = productRepository.save(product2);

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        savedMenuGroup,
        List.of(
            createMenuProduct(savedProduct1, 1),
            createMenuProduct(savedProduct2, 1)
        )
    );

    Menu savedMenu = menuRepository.save(menu);

    mvc.perform(
            put("/api/menus/{menuId}/hide", savedMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(savedMenu.getId().toString()))
        .andExpect(jsonPath("$.name").value(menu.getName()))
        .andExpect(jsonPath("$.price").value(menu.getPrice().longValue()))
        .andExpect(jsonPath("$.displayed").value(false));
  }

}

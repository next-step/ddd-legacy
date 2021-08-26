package kitchenpos.ui;

import static java.util.Collections.singletonList;
import static kitchenpos.KitchenposTestFixture.한마리메뉴;
import static kitchenpos.KitchenposTestFixture.후라이드치킨_후라이드;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class MenuRestControllerTest extends IntegrationTest {

    Menu 후라이드특가_Menu;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        후라이드특가_Menu = new Menu();
        후라이드특가_Menu.setName("후라이드특가");
        후라이드특가_Menu.setPrice(BigDecimal.valueOf(15000L));
        후라이드특가_Menu.setDisplayed(true);
        후라이드특가_Menu.setMenuGroup(한마리메뉴);
        후라이드특가_Menu.setMenuGroupId(한마리메뉴.getId());
        후라이드특가_Menu.setMenuProducts(singletonList(후라이드치킨_후라이드));
    }

    @DisplayName("메뉴를 생성한다")
    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가_Menu)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @DisplayName("메뉴가격이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyPrice() throws Exception {
    }

    @DisplayName("메뉴가격은 음수가 될 수 없다")
    @Test
    void createFailedByNegativePrice() throws Exception {
    }

    @DisplayName("실제 존재하는 메뉴 그룹에만 포함시킬 수 있다")
    @Test
    void createFailedByNoSuchMenuGroup() throws Exception {
    }

    @DisplayName("메뉴제품목록이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyMenuProducts() throws Exception {
    }

    @DisplayName("메뉴제품목록에는 중복이 허용되지 않는다")
    @Test
    void createFailedByDuplicateMenuProducts() throws Exception {
    }

    @DisplayName("메뉴제품의 제품수량은 음수가 될 수 없다")
    @Test
    void createFailedByNegativeMenuProductQuantity() throws Exception {
    }

    @DisplayName("메뉴제품으로는 실제 존재하는 제품만 등록된다")
    @Test
    void createFailedByNoSuchProduct() throws Exception {
    }

    @DisplayName("메뉴가격이 메뉴제품목록의 총 가격보다 크면 안된다")
    @Test
    void createFailedByExceedingTotalPrice() throws Exception {
    }

    @DisplayName("메뉴명이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyMenuName() throws Exception {
    }

    @DisplayName("메뉴명에 비속어가 포함되어서는 안된다")
    @Test
    void createFailedByIncludingProfanity() throws Exception {
    }

    @DisplayName("메뉴가격을 수정한다")
    @Test
    void changePrice() throws Exception {
    }

    @DisplayName("메뉴가격이 반드시 전달되어야 한다")
    @Test
    void changePriceFailedByEmptyPrice() throws Exception {
    }

    @DisplayName("메뉴가격은 음수가 될 수 없다")
    @Test
    void changePriceFailedByNegativePrice() throws Exception {
    }

    @DisplayName("실제 존재하는 메뉴만 수정할 수 있다")
    @Test
    void changePriceFailedByNoSuchMenu() throws Exception {
    }

    @DisplayName("메뉴가격이 단품가격의 총 합보다 크면 안된다")
    @Test
    void changePriceFailedByExceedingTotalPrice() throws Exception {
    }

    @DisplayName("메뉴를 노출한다")
    @Test
    void display() throws Exception {
    }

    @DisplayName("실제 존재하는 메뉴만 노출할 수 있다")
    @Test
    void displayFailedByNoSuchMenu() throws Exception {
    }

    @DisplayName("메뉴가격이 단품가격의 총 합보다 크면 안된다")
    @Test
    void displayFailedByExceedingTotalPrice() throws Exception {
    }

    @DisplayName("메뉴를 숨긴다")
    @Test
    void hide() throws Exception {
    }

    @DisplayName("실제 존재하는 메뉴만 숨길 수 있다")
    @Test
    void hideFailedByNoSuchMenu() throws Exception {
    }

    @DisplayName("메뉴 목록을 조회한다")
    @Test
    void findAll() throws Exception {
    }
}

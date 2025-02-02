package kitchenpos.menugroup

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kitchenpos.application.MenuGroupService
import kitchenpos.domain.MenuGroup
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.springframework.beans.factory.annotation.Autowired

class CreateMenuGroupDefs : CucumberTest() {
    @Autowired
    private lateinit var menuGroupService: MenuGroupService

    private var menuGroup: MenuGroup = MenuGroup();

    @Given("메뉴 그룹 이름은 {string}")
    fun 메뉴그룹이름은String이다(name: String?) {
        menuGroup = MenuGroup()
        menuGroup.name = name
    }

    @Then("메뉴 그룹 생성 성공")
    fun 메뉴그룹생성성공() {
        menuGroupService.create(menuGroup)
        val menuGroups = menuGroupService.findAll()
        assertThat(menuGroups).hasSize(1)
    }

    @Then("메뉴 그룹 생성 실패")
    fun 메뉴그룹생성실패() {
        assertThatIllegalArgumentException()
            .isThrownBy { menuGroupService.create(menuGroup) }
    }
}

package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
class MenuGroupServiceTest : BehaviorSpec() {

    @Autowired
    lateinit var menuGroupService: MenuGroupService

    @Autowired
    lateinit var menuGroupRepository: MenuGroupRepository


    init {
        Given("MenuGroupService가 주어졌을 때") {
            When("메뉴 그룹의 이름이 없는 경우") {
                Then("예외를 던진다") {
                    val menuGroup = MenuGroup()

                    shouldThrow<IllegalArgumentException> {
                        menuGroupService.create(menuGroup)
                    }
                }
            }

            When("메뉴 그룹을 생성할 때") {
                val menuGroup = MenuGroup().apply {
                    name = "menuGroup"
                }

                Then("메뉴 그룹이 정상적으로 생성된다") {
                    val savedMenuGroup = menuGroupService.create(menuGroup)

                    savedMenuGroup shouldNotBe null
                    savedMenuGroup.id shouldNotBe null
                    savedMenuGroup.name shouldBe "menuGroup"
                }
            }
        }

        Given("메뉴 그룹이 저장되어 있을 때") {
            beforeEach {
                val menuGroup1 = MenuGroup().apply {
                    id = UUID.randomUUID()
                    name = "menuGroup1"
                }
                val menuGroup2 = MenuGroup().apply {
                    id = UUID.randomUUID()
                    name = "menuGroup2"
                }
                menuGroupRepository.saveAll(listOf(menuGroup1, menuGroup2))
            }

            When("모든 메뉴 그룹을 조회하면") {
                Then("저장된 메뉴 그룹을 반환한다") {
                    val menuGroups = menuGroupService.findAll()

                    menuGroups shouldHaveSize 2
                }
            }
        }
    }
}

package kitchenpos.model;

public class MenuGroup {
    private Long id; // 메뉴그룹 id
    private String name; // 메뉴그룹명

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}

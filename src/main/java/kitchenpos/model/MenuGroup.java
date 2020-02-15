package kitchenpos.model;

public class MenuGroup {
    private final Long id;
    private final String name;

    private MenuGroup(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private Long id;
        private String name;

        public Builder id (Long id){
            this.id = id;
            return this;
        }

        public Builder name (String name){
            this.name = name;
            return this;
        }

        public MenuGroup build(){
            return new MenuGroup(this);
        }
    }
}

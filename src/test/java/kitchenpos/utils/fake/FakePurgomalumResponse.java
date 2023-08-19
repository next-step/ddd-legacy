package kitchenpos.utils.fake;

public enum FakePurgomalumResponse {
    비속어("비속어", true)
    , 보통어("보통어", true) ;

    private final String text;
    private final boolean containsProfanity;

    FakePurgomalumResponse(String text, boolean containsProfanity) {
        this.text = text;
        this.containsProfanity = containsProfanity;
    }

    public boolean containsProfanity(){
        return containsProfanity;
    }
}

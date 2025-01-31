package kitchenpos.test;

import kitchenpos.infra.ProfanityChecker;

public class FakePurgomalumClient implements ProfanityChecker {

    private boolean isProfanity = false;

    @Override
    public boolean containsProfanity(String text) {
        return isProfanity;
    }

    public void setProfanity(boolean isProfanity) {
        this.isProfanity = isProfanity;
    }
}

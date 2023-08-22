package calculator;

enum MatcherGroup {
    CUSTOM_DELIMITER(1), STRING_TO_SPLIT(2);

    final private int group;

    MatcherGroup(int group) {
        this.group = group;
    }

    public int getGroup() {
        return group;
    }
}

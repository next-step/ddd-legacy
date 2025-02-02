package calculate.domain;

public class StringCalculate {

    private final NumberGroups numberGroups;

    public StringCalculate(final String sentence) {
        this(new NumberGroups(sentence));
    }

    public StringCalculate(final NumberGroups numberGroups) {
        this.numberGroups = numberGroups;
    }

    public int sum() {
        return numberGroups.sum();
    }

    public NumberGroups getNumbers() {
        return numberGroups;
    }

}

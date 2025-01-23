package calculator;

public class NumberExtractor {

    public Integer[] extract(String numberText, DelimiterGroup delimiterGroup) {
        String[] splittedString = numberText.split(delimiterGroup.getDelimiterPattern());
        validateNumbers(splittedString);

        Integer[] integerArray = new Integer[splittedString.length];
        for (int i = 0; i < splittedString.length; i++) {
            integerArray[i] = Integer.parseInt(splittedString[i]);
        }

        return integerArray;
    }

    private void validateNumbers(String[] splittedString) {
        for (String str : splittedString) {
            if (!isDigit(str)) {
                throw new IllegalArgumentException("유효하지 않은 문자 : %s".formatted(str));
            }
        }
    }

    private boolean isDigit(String str) {
        return str.matches("\\d+");
    }

}

package stringCalculator;

public class Calculator {

    public int getValidNumber(String targetChar) {
        if (!isNumber(targetChar)) {
            throw new RuntimeException("invalid character, it should be number and over 0, given: " + targetChar);
        }

        return Integer.parseInt(targetChar);
    }

    private boolean isNumber(String singleChar) {
        if (singleChar == null || singleChar.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(singleChar);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

package calculator;

public class Number {

    /* convertNumber : String > Integer 변환 */
    public static Integer convertNumber(String input) {

        Integer parseInt = parseInteger(input);
        if (parseInt != null) {
            return parseInt;
        }

        return null;
    }

    public static Integer parseInteger(String input) {
        try {
            Integer num = Integer.parseInt(input);
            if (num >= 0) {
                return Integer.parseInt(input);
            } else {        // 음수인 경우, RuntimeException 발생
                throw new RuntimeException("input number is negative : " + num);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("non-numeric found : " + input);
        }
    }
}

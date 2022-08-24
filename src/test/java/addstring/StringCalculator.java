package addstring;

import java.util.Arrays;

public class StringCalculator {

    private static final String WRAPPED_STRING = "//\n";
    private String separator = ",:";

    public int add(String s) {
        if (s == null || s.isEmpty()){
            return 0;
        }

        String stringToCalculate = checkPolicy(s);

        String[] stringNumberArray = splitStringToArrayBySeparator(stringToCalculate, this.separator);
        return Arrays.stream(stringNumberArray)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    public String checkPolicy(String s){
        String[] parsedString = splitStringToArrayBySeparator(s, WRAPPED_STRING);
        if (parsedString[0].equals(s)){
            return s;
        }

        int lastIndex = parsedString.length - 1;
        String customSeparator = parsedString[lastIndex-1];
        String stringToCalculate = parsedString[lastIndex];
        this.separator = this.separator.concat(customSeparator);
        return stringToCalculate;
    }

    private String[] splitStringToArrayBySeparator(String s, String separator){
        String separatorPolicy = String.format("[%s]", separator);
        return s.split(separatorPolicy);
    }

}

package addstring;

import java.util.Arrays;

public class StringCalculator {

    private static final String SEPARATOR_POLICY = "[,:]";

    public int add(String s) {
        if (s == null || s.isEmpty()){
            return 0;
        }

        String[] strings = separateStringToArray(s);
        return Arrays.stream(strings)
                .mapToInt(Integer::valueOf)
                .sum();
    }

    private String[] separateStringToArray(String s){
        return s.split(SEPARATOR_POLICY);
    }

}

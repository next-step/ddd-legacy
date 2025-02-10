package calculator;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String[] arr = text.split(",");
        int total = 0;
        for (int i = 0; i < arr.length; i++) {
            total += Integer.parseInt(arr[i]);
        }
        return total;
    }
}

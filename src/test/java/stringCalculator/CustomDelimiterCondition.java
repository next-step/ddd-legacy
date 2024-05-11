package stringCalculator;

public class CustomDelimiterCondition {
        final private String start;
        final private String end;

        public CustomDelimiterCondition(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }
}

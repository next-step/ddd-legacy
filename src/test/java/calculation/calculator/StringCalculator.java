package calculation.calculator;

import calculation.convert.ConvertStringsToNumbers;

public class StringCalculator {

	public static int add(String formula) {
		/**
		 * 문자열을 받아와 separation 호출
		 */
		ConvertStringsToNumbers converter = new ConvertStringsToNumbers(formula);
		/**
		 * result 값 넣기
		 */
		return converter.getResult();
	}
}

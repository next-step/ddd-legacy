package calculation.number;

import static calculation.calculator.CalculatorCheckValidation.isNum;

public class Number {

	private final static String NEGATIVE_NUMBERS_ARE_NOT_ALLOWED = "음수는 허용되지 않습니다. ";
	private final static String SPECIAL_CHARACTER_ARE_NOT_ALLOWED = "지정한 표현식 말고는 특수문자를 사용할 수 없습니다. ";

	private final int number;

	public Number(int number) {
		if (number < 0) {
			throw new RuntimeException(NEGATIVE_NUMBERS_ARE_NOT_ALLOWED + "입력값 : " + number);
		}
		this.number = number;
	}

	public static Number convert(String text) {
		if (isNum(text)) {
			int parseInt = Integer.parseInt(text);
			return new Number(parseInt);
		}
		throw new RuntimeException(SPECIAL_CHARACTER_ARE_NOT_ALLOWED +"입력값 : " + text);
	}

	public int getNumber() {
		return number;
	}

}

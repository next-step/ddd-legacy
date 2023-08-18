package calculator;

public class StringCalculator {

    public int add(String text) {
        Validation valid = new Validation();
        Operation operation = new Operation();

        //선처리1: 입력이 없을 경우는 0
        if(valid.isNull(text)){
            return 0;
        }

        //선처리2: 입력 문자열이 하나의 숫자인 경우 그대로 리턴.
        if(valid.isInt(text)){
            int sum = Integer.parseInt(text);
            if(sum < 0){
                throw new RuntimeException("입력문자가 음수입니다.");
            }
            return sum;
        }

        // 추가 구분자 확인. 구분자 존재시 문장에서 구분자 문장제거
        String removeOperText = operation.sepOperatorFromText(text);
        String operator = operation.getOperator();

        //가지고있는 구분자로 숫자 나누기
        String[] tokens= removeOperText.split(operator);

        //나눈 숫자더하기. 과정에서 숫자가 아니거나 음수 발생시 에러처리
        return operation.addition(tokens);

    }

}
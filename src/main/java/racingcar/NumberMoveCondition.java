package racingcar;

class NumberMoveCondition implements MoveCondition {
	// movable()에 파라미터를 넣을 수 없는데 이는 스마트인터페이스 패턴으로 해결할 수 있음

	private final int condition;

	public NumberMoveCondition(final int condition) {
		this.condition = condition;
	}

	@Override
	public boolean movable() {
		return condition >= 4;
	}
}
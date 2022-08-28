package racingcar.source;

import racingcar.source.strategy.MoveStrategy;

public class Car {
    private final CarName name;
    private int position;
    public Car(final CarName name, int position) {
        this.name = name;
        this.position = position;

    }

    public void move(MoveStrategy moveStrategy) {
      if(moveStrategy.movable()){
          position += 1;
      }
    }


    public int getPosition() {
        return this.position;
    }
}

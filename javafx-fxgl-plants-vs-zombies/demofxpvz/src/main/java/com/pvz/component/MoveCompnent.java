package com.pvz.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

public class MoveCompnent extends Component {
    private LocalTimer move;
    private double speed;

    public MoveCompnent(LocalTimer move, double speed) {
        this.move = move;
        this.speed = speed;
    }

    @Override
    public void onUpdate(double tpf) {

    }
}

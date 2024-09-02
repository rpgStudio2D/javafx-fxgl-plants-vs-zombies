package com.pvz.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import com.pvz.data.PlantsInfo;
import javafx.scene.image.Image;

//种子事件 创建时添加点击事件
public class SeedComponent extends Component {



    @Override
    public void onAdded() {
        PlantsInfo plantsInfo = entity.getObject("plantsinfo");
        Image image = FXGL.image(String.format(plantsInfo.getImages(), 1),39, 43 );
        Texture texture = new Texture(image);
        texture.setTranslateX(8);
        texture.setTranslateY(9);
        entity.getViewComponent().addChild(texture);
        entity.getViewComponent().addOnClickHandler(
                even->{
                    if (FXGL.geti("sun") - plantsInfo.getCost() >= 0){ //点击种子时判断阳光是否够种植
                        FXGL.set("putplants", 1); //在放植物
                        FXGL.set("choseseed", entity.getObject("plantsinfo"));
                        FXGL.play("paper.wav");
                        System.out.println("选中了"+plantsInfo.getName() + "的种子");
                    }else{
                        FXGL.play("buzzer.wav");
                        System.out.println("阳光不够");
                    }
                }
        );
    }
}

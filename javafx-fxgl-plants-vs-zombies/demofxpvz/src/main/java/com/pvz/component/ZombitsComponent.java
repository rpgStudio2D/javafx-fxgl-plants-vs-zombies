package com.pvz.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.OfflineTimer;
import com.pvz.data.EntityType;
import com.pvz.data.ZombitsInfo;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalTime;
import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.play;

// Zombits 组件
public class ZombitsComponent extends Component {
    //生产僵尸 位置
    private static final int LCATION[][] = {//僵尸生成位置
            {950, 45},
            {950, 145},
            {950, 245},
            {950, 345},
            {950, 445}
    };
    AnimatedTexture animatedTexture;
    private ZombitsInfo zombitsInfo; //僵尸基本属性
    private int positioning; //记录僵尸位置（哪条路）
    private LocalTimer move; //移动时间
    private boolean eaton;
    private LocalTimer eat;
    @Override
    public void onAdded() {// 创建这个实体组件  创建实体并初始僵尸基本信息
        eat = FXGL.newLocalTimer();
        zombitsInfo = entity.<ZombitsInfo>getObject("zombitsinfo");

        ZombitsInfo zombitsInfo = entity.getObject("zombitsinfo");
        // 序列帧动画
        ArrayList<Image> imageArrayList = new ArrayList<Image>();
        for (int i = 0; i < 47; i++) {
            imageArrayList.add(FXGL.image(String.format(zombitsInfo.getImage(), i + 1)));
        }

        AnimationChannel animationChannel = new AnimationChannel(imageArrayList, Duration.seconds(3));
        animatedTexture = new AnimatedTexture(animationChannel);

        animatedTexture.loop();
        entity.getViewComponent().addChild(animatedTexture);
        int lcation_index = entity.getInt("positioning");
        entity.setPosition(LCATION[lcation_index][0], LCATION[lcation_index][1]);
        entity.setType(EntityType.ZOMBITS);
        int[] count = FXGL.<int[]>geto("attack_switch"); //植物攻击依据

//        Canvas canvas = new Canvas(100, 100);
//        GraphicsContext g2d = canvas.getGraphicsContext2D();
//        g2d.setFill(Color.web("#ffec30"));
//        g2d.fillRect(10,10,10,80);

        positioning = lcation_index; //记录僵尸在那一条路线
        count[lcation_index] += 1; //当前路径僵尸＋1
        move = FXGL.newLocalTimer(); //放下时计时器开始
    }


    @Override
    public void onRemoved() { //僵尸死亡时
        int[] count = FXGL.<int[]>geto("attack_switch"); //获取全局 僵尸计数


        //播放僵尸死亡动画
        ArrayList<Image> images = new ArrayList<Image>();
        //获取json 数据 中 死亡动画关键帧
        String filedir = zombitsInfo.getDieimage();
        int start = zombitsInfo.getDiestart();
        int end = zombitsInfo.getDieend();
        while(start <= end){
            images.add(FXGL.image(String.format(filedir, start)));
            start++;
        }
        AnimationChannel animationChannel = new AnimationChannel(images, Duration.seconds(2));
        AnimatedTexture animatedTexture = new AnimatedTexture(animationChannel);


        animatedTexture.play();
        Entity entity1 = FXGL.entityBuilder()
                .at(entity.getPosition())
                .view(animatedTexture)
                .with(new ExpireCleanComponent(Duration.seconds(2))) //过期清理组件
                .buildAndAttach();

        count[positioning] -= 1; //当前路径僵尸-1

    }

    @Override
    public void onUpdate(double tpf) {
        if (!eaton) {
            if (move.elapsed(Duration.millis(150))) { //僵尸移动
                entity.translateX(-2);
                move.capture();
            }
        }else {
            if (eat.elapsed(Duration.seconds(1))){
                play(String.format("eat%d.wav", FXGL.random(0,2)));
                eat.capture();
            }
        }
    }

    public void  setEaton(boolean i){
        eaton = i;
    }

    public AnimatedTexture getAnimatedTexture(){
        return animatedTexture;
    }
}


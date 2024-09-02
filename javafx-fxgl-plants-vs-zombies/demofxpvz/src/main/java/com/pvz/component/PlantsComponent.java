package com.pvz.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.pvz.data.EntityType;
import com.pvz.data.PlantsInfo;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;

/*
	The Plants vs. Zombies game，Using JavaFx-FXGL technology, suitable for teaching。Source code developer communication QQ group 685806772
*/
// 植物组件
public class PlantsComponent extends Component{
    //创建实体 组件
    private int hp;
    private PlantsInfo plantsInfo;//植物基本信息
    private LocalTimer localTime; //植物计时器  攻击间隔
    private int positioning;
    @Override
    public void onAdded() {
        localTime = FXGL.newLocalTimer(); //创建实体时 初始化计时器
        // 获取对象属性
        plantsInfo = entity.getObject("plantsinfo");
        positioning = entity.getInt("positioning");

        hp = plantsInfo.getHealth();
        int[][] plantscount = FXGL.geto("plantscount");//更新植物所在路 植物总数加1
        plantscount[positioning][0] += 1;
        if(plantscount[positioning][0] > plantscount[positioning][1]) //1 是过去植物总数 如果 比过去植物总数少就更新过去记录
            plantscount[positioning][1] = plantscount[positioning][0];
        // 序列帧动画
        ArrayList<Image> imagelist = new ArrayList<>();
        int count = plantsInfo.getImageCount(); // 这是图片数量
        for (int i = 0; i < count; i++) {
            imagelist.add(FXGL.image(String.format(plantsInfo.getImages(), i + 1)));
        }
        AnimationChannel animationChannel = new AnimationChannel(imagelist, Duration.seconds(3)); //动画效果
        AnimatedTexture animatedTexture = new AnimatedTexture(animationChannel);    //动画
        animatedTexture.loop(); //循环播放
        entity.getViewComponent().addChild((Node)animatedTexture);//添加视图
    }

    @Override
    public void onUpdate(double tpf) {


        //判断 游戏变量 中 路径上僵尸不为0
        if (FXGL.<int[]>geto("attack_switch")[positioning] != 0) {
            //下面一大串是生成 子弹的 丑就完了
            double dr = ((PlantsInfo) entity.getObject("plantsinfo")).getAttckSpeed(); //获取植物攻击力速度
            double atk = ((PlantsInfo) entity.getObject("plantsinfo")).getAttckSpeed();  //获取植物攻击力

            Image image = FXGL.image(((PlantsInfo) entity.getObject("plantsinfo")).getBulletImage()); //获取当前植物实体 的子弹图片

            if (localTime.elapsed(Duration.seconds(dr))) {//当前植物触发技能的间隔时间
                System.out.println("发射子弹");
                FXGL.play("biu.wav");

                Entity bt = FXGL.entityBuilder()
                        .type(EntityType.BULLET)
                        .at(entity.getX() + 50, entity.getY() + 8)
                        .view(new Texture(image))
                        .bbox(BoundingShape.box(image.getWidth() / 2 - 20, 20))
                        .with(new ProjectileComponent(new Point2D(1, 0), 350))
                        .with(new CollidableComponent(true))
                        .with(new OffscreenCleanComponent()) //超过屏幕自动清除
                        .buildAndAttach();
                localTime.capture(); //计时器清零
            }
        }


    }

    public void setHp(int i){
        hp = i;
    }
    public int getHp(){
        return hp;
    }
}

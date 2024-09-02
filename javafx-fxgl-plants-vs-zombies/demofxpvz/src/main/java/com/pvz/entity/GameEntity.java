package com.pvz.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.pvz.component.*;
import com.pvz.data.EntityType;
import com.pvz.data.ZombitsInfo;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;




/*
	The Plants vs. Zombies game，Using JavaFx-FXGL technology, suitable for teaching。Source code developer communication QQ group 685806772
*/
public class GameEntity implements EntityFactory {


    //种子槽位
    private static int Seed_Interval = 0; //种子槽  x轴间隔
    @Spawns("seed")
    public Entity creatSeed(SpawnData data){//传入植物数据
             Entity entity;
             Texture texture = texture("UI/SeedPacket_Larger.png", 50, 70);
             entity = FXGL.entityBuilder(data)
                    .at(330 + Seed_Interval, 8)
                    .view(texture)
                     .with(new SeedComponent())
                    .build();
             Seed_Interval += 60;
            return entity;
    }


    //花园背景
    @Spawns("garden")
    public Entity newGarden(SpawnData data) {

        return entityBuilder(data)
                .type(EntityType.UI)
                .view("bg.png")
                .with(new MenuComponent())//添加自定义 界面组件
                .build();
    }

    //僵尸
    @Spawns("zombits")
    public Entity newZombit(SpawnData data) {


        //创建实体
        return entityBuilder(data)
                .type(EntityType.ZOMBITS)
                .bbox(BoundingShape.box(50, 90))
                .with(new CollidableComponent(true)) //可碰撞组件
                .with(new ZombitsComponent())
                .build();
    }


    //植物
    @Spawns("plant")
    public Entity newPlant(SpawnData data) {
//        Canvas canvas = new Canvas(100, 100);
//        GraphicsContext g2d = canvas.getGraphicsContext2D();
//        g2d.setFill(Color.web("#ffec30"));
//        g2d.fillRect(10,10,10,10);
        return entityBuilder(data)
                .type(EntityType.PLANT)
                .bbox(BoundingShape.box(20,10))
                .with(new CollidableComponent(true))//添加可碰撞组件
                .with(new PlantsComponent())
                .build();
    }


    //物品
    @Spawns("articles")
    public Entity creatArticles(SpawnData data){
            return FXGL.entityBuilder(data)
                    .build();
    }


}

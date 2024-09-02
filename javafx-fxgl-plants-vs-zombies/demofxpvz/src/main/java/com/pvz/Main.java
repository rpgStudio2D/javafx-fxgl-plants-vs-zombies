/*
	The Plants vs. Zombies game，Using JavaFx-FXGL technology, suitable for teaching。Source code developer communication QQ group 685806772
*/
package com.pvz;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.pvz.component.PlantsComponent;
import com.pvz.component.ZombitsComponent;
import com.pvz.data.EntityType;
import com.pvz.data.Gardeninfo;
import com.pvz.data.PlantsInfo;
import com.pvz.data.ZombitsInfo;
import com.pvz.entity.GameEntity;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;



public class Main extends GameApplication {
    private static final int LCATION[][] = {//僵尸生成位置
            {950, 35},
            {950, 135},
            {950, 235},
            {950, 335},
            {950, 435}
    };
    private static boolean[][] PLANTS_EXIST = new boolean[9][5]; // 5 * 9 判断该草地是否存在植
    private static boolean PUT_PLANT = false;   // 点击植物槽的操作
    private boolean CLICK = false;
    private LocalTimer time;
    private static Entity[][] GRASS = new Entity[9][5];//

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("PlantVsZombit");
        settings.setWidth(1400);
        settings.setHeight(600);

        settings.setAppIcon("plant/wandou/wandou (1).png");
    }

    @Override
    protected void initGame() {
        time = FXGL.newLocalTimer();
        //FXGL.loopBGM("Laura Shigihara - Zombies On Your Lawn.mp3");
        FXGL.loopBGM("Laura Shigihara - Zombies On Your Lawn.mp3");
        getGameWorld().addEntityFactory(new GameEntity()); //指定实体创建工厂
        Gardeninfo gardeninfo = getAssetLoader().loadJSON("data/Garden.json", Gardeninfo.class).get();
        spawn("garden", new SpawnData().put("gardeninfo", gardeninfo));//花园实体 传入json 数据


        //生成一个 5 * 9 的标记
        for (int x = 255, i = 0; i < 9; x += 82, i++) {
            for (int y = 85, j = 0; j < 5; y += 100, j++) {
                Component ComponentListener;
                GRASS[i][j] = entityBuilder()
                        .at(x, y)
                        .bbox(BoundingShape.box(80, 90))
                        .buildAndAttach();
                ViewComponent vc = GRASS[i][j].getViewComponent();
            }
        }
    }

    @Override
    protected void initUI() {
        Text text = FXGL.addVarText("sun", 275, 80);
        text.setFill(Color.web("Black"));

        //Seed Back
        Texture texture = FXGL.texture("UI/SeedBank.png");
        Entity SeedBank = entityBuilder()
                .at(255, 0)
                .view("UI/SeedBank.png")
                .buildAndAttach();


        //植物种子槽位
        seedUi();


    }

    @Override
    protected void initPhysics() {//物理引擎

        //子弹碰撞事件
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ZOMBITS) { //添加碰撞 参数是枚举类型对应实体type 枚举自定义
               @Override
               protected void onCollisionBegin(Entity a, Entity b) {
                   FXGL.play("attack.wav");
                   ZombitsInfo zombitsinfo = (ZombitsInfo) b.getObject("zombitsinfo");
                   int hp = zombitsinfo.getHp();
                   if (hp - 30 <= 0) {//生命值小于 0 //移除
                       b.removeFromWorld();
                   } else { //更新生命值
                       zombitsinfo.setHp(hp - 30);
                   }
                   System.out.println("剩余生命" + hp);
                   a.removeFromWorld();
               }
           }
        );

        //僵尸的碰撞事件
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLANT, EntityType.ZOMBITS) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {

                //添加吃植物  动画图层
                b.getComponent(ZombitsComponent.class).setEaton(true);
                ZombitsInfo zombitsInfo = b.getObject("zombitsinfo"); //获取原本僵尸信息 从中读取 动画路径
                ArrayList<Image> images = new ArrayList<Image>();
                for (int i = zombitsInfo.getEatimagestart(); i < zombitsInfo.getEatimageend(); i++) {
                    images.add(FXGL.image(String.format(zombitsInfo.getEatimage(), i)));
                }
                AnimationChannel animationChannel = new AnimationChannel(images,Duration.seconds(2));
                AnimatedTexture animatedTexture = new AnimatedTexture(animationChannel);

                animatedTexture.loop();

                b.getViewComponent().clearChildren(); // 移除原本动画
                b.getViewComponent().addChild(animatedTexture);
                PlantsInfo plantsInfo = a.getObject("plantsinfo");
                int hp = a.getComponent(PlantsComponent.class).getHp();
                hp -= zombitsInfo.getAtk();
                System.out.println("剩余血量" + hp);
                if (hp > 0)
                    a.getComponent(PlantsComponent.class).setHp(hp);
                else{
                    FXGL.getGameWorld().getEntitiesByType(EntityType.ZOMBITS).forEach(event->{
                        if (event.getY() == b.getY()){
                            event.getViewComponent().clearChildren();
                            AnimatedTexture animatedTexture1 = event.getComponent(ZombitsComponent.class).getAnimatedTexture();
                            event.getViewComponent().addChild(animatedTexture1);
                            event.getComponent(ZombitsComponent.class).setEaton(false);
                        }
                    });
                    a.removeFromWorld();
                }
            }
        });

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {//游戏的全局变量
        vars.put("sun", 1000); //阳光
        vars.put("putplants", 0);
        vars.put("choseseed", new PlantsInfo());
        int[] attack_switch = new int[5];  //记录每条路僵尸  植物发射子弹的阻塞
        vars.put("attack_switch", attack_switch);

        int[][] plantscount = new int[5][2]; //判断每条路上植物总数
        vars.put("plantscount", plantscount);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (time.elapsed(Duration.seconds(1))) {
            creatZombits();
            time.capture();
        }
    }


    @Override
    protected void initInput() {
        //选中草坪生成植物
        getInput().addAction(new UserAction("鼠标左键选中") {
            @Override
            protected void onAction() {//一大串都是种植物 能力有限 就这么丑  可以优化
                Point2D point = getInput().getMousePositionWorld();
                if (!time.elapsed(new Duration(500)))
                    return;
                time.capture();
                //判断点击位置
                for (int x = 255, i = 0; i < 9; x += 82, i++) {
                    for (int y = 85, j = 0; j < 5; y += 100, j++) {
                        if (point.getX() >= x && point.getX() <= x + 60 && point.getY() >= y && point.getY() <= y + 80 && 1 == geti("putplants")) {
                            if (PLANTS_EXIST[i][j]) {//
                                set("putplants", 0);//清除种植状态
                                System.out.println("这个地方已经有植物了不能种植");
                                return;
                            }
                            play("plant.wav");
                            PlantsInfo plantsInfo = geto("choseseed"); //获取选中的种子
                            Entity entity = spawn("plant", new SpawnData().put("plantsinfo", plantsInfo).put("positioning", j));//创建相应植物
                            entity.setPosition(x + 8, y + 12);//显示位置
                            PLANTS_EXIST[i][j] = true; //标记草地

                            set("sun", geti("sun") - plantsInfo.getCost());//阳光减少
                            set("putplants", 0);
                            System.out.println("种植了" + plantsInfo.getName());
                            System.out.printf("当前阳光剩余" + geti("sun"));
                            return;
                        }
                    }
                }
            }
        }, MouseButton.PRIMARY);


    }

    public static void main(String[] args) {
        launch(args);
    }

    //生成 种子槽
    public static void seedUi() {
        PlantsInfo plantsInfo = getAssetLoader().loadJSON("data/Wandou.json", PlantsInfo.class).get();
        for (int i = 0; i < 6; i++) {
            spawn("seed", new SpawnData().put("plantsinfo", plantsInfo));
        }
    }

    //生成僵尸
    public static Entity creatZombits() {
        ZombitsInfo zombitsinfo = getAssetLoader().loadJSON("data/Zombits.json", ZombitsInfo.class).get();
        int lcation_index = FXGL.random(0,4);
        return spawn("zombits", new SpawnData().put("zombitsinfo", zombitsinfo).put("positioning", lcation_index));
    }

    // 生成植物   传入植物json 数据
    public static Entity putPlant(String filedir) {
        PlantsInfo plantinfo = getAssetLoader().loadJSON(filedir, PlantsInfo.class).get();
        return spawn("plant", new SpawnData().put("plantsinfo", plantinfo));
    }
}
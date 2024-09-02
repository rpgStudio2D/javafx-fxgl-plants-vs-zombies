package com.pvz.component;

import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.entity.component.Component;
import com.pvz.data.Gardeninfo;
import javafx.scene.image.Image;

//背景图片创建组
public class MenuComponent extends Component {//自定义组件
    @Override
    public void onAdded() {//重写组件
        Gardeninfo gardeninfo = this.entity.getObject("gardeninfo"); //获取实体中的数据 可用构造函数传递
        this.entity.setPosition(gardeninfo.x(), gardeninfo.y()); //设置显示位置
    }

}

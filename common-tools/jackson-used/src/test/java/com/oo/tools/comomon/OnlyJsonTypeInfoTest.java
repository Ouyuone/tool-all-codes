package com.oo.tools.comomon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oo.tools.comomon.domain.onlyJsonTypeInfo.ClsCircle;
import com.oo.tools.comomon.domain.onlyJsonTypeInfo.ClsRectangle;
import com.oo.tools.comomon.domain.onlyJsonTypeInfo.ClsShape;
import com.oo.tools.comomon.domain.onlyJsonTypeInfo.ClsView;
import junit.framework.TestCase;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/11/28 16:55:25
 */
public class OnlyJsonTypeInfoTest extends TestCase {
    ObjectMapper objectMapper;
    @Override
    protected void setUp() throws Exception {
       objectMapper = new ObjectMapper();
    }
    
    @SneakyThrows
    public void testOnlyJsonTypeInfo()
    {
        ClsRectangle rectangle = new ClsRectangle(7,9); //构建正方形对象
        ClsCircle circle = new ClsCircle(8); //构建长方形对象
        List<ClsShape> shapes = new ArrayList<>();  //List<多种形状>
        shapes.add(circle);
        shapes.add(rectangle);
        ClsView view = new ClsView();  //将List放入画面View
        view.setShapes(shapes);
        
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("-- 序列化 --");
        String jsonStr = mapper.writeValueAsString(view);
        System.out.println(jsonStr);
        
        System.out.println("-- 反序列化 --");
        ClsView deserializeView = mapper.readValue(jsonStr, ClsView.class);
        System.out.println(deserializeView);
    
    }
}

package org.simpleframework.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.mock.Mock1;

import java.util.ArrayList;
import java.util.List;

public class AspectListExecutorTest {
    @DisplayName("Aspect排序")
    @Test
    public void sortTest(){
        List<AspectInfo> aspectInfoList = new ArrayList<>();
        // aop1.0
        // aspectInfoList.add(new AspectInfo(3,new Mock1()));
        // aspectInfoList.add(new AspectInfo(1,new Mock1()));
        // aspectInfoList.add(new AspectInfo(2,new Mock1()));
        // aspectInfoList.add(new AspectInfo(4,new Mock1()));

        aspectInfoList.add(new AspectInfo(3,new Mock1(),null));
        aspectInfoList.add(new AspectInfo(1,new Mock1(),null));
        aspectInfoList.add(new AspectInfo(2,new Mock1(),null));
        aspectInfoList.add(new AspectInfo(4,new Mock1(),null));


        AspectListExecutor aspectListExecutor = new AspectListExecutor(AspectListExecutorTest.class,aspectInfoList);
        List<AspectInfo> sortedAspectInfoList = aspectListExecutor.getSortedAspectInfoList();
        for(AspectInfo ai:sortedAspectInfoList){
            System.out.println(ai.getOrderIndex());
        }
    }
}

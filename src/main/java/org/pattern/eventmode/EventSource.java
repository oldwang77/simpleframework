package org.pattern.eventmode;

import java.util.ArrayList;
import java.util.List;

public class EventSource {
    // 监听器是有多个的，所以我们用List存储
    private List<EventListener> listenerList = new ArrayList<>();

    // 提供对外暴露的注册方法
    public void register(EventListener listener){
        listenerList.add(listener);
    }

    // 发布事件
    public void publishEvent(Event event){
        for(EventListener listener: listenerList){
            listener.processEvent(event);
        }
    }
}

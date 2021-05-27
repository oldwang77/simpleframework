package com.imooc.controller.superadmin;

import com.imooc.service.solo.HeadLineService;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;

@Controller
public class HeadLineOperationController {

    @Autowired(value = "HeadLineServiceImpl")
    private HeadLineService headLineService;

    public void removeHeadLine(String s1,String s2){
        headLineService.removeHeadLine(1);
        System.out.println("删除HeadLine");
    }

}

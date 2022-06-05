package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.ExtractApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExtractApplication.class)
public class ViceAbilityServiceTest {
    @Resource
    private ViceAbilityService viceAbilityService;

    @Test
    public void testGetVisitNum(){
        System.out.println(viceAbilityService.getVisitNum());
    }

    @Test
    public void testGetExtractNum(){
        System.out.println(viceAbilityService.getExtractNum());
    }

    @Test
    public void testUpdateVisitNum(){
        viceAbilityService.updateVisitNum();
    }

    @Test
    public void testUpdateExtractNum(){
        viceAbilityService.updateExtractNum();
    }

}

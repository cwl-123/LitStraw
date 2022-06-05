package com.micerlabs.LitStraw.Controller;

import com.micerlabs.LitStraw.Service.ViceAbilityService;
import com.micerlabs.LitStraw.VO.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/help")
@Validated
public class ViceAbilityController {
    @Resource
    private ViceAbilityService viceAbilityService;

    @GetMapping("/getVisitNum")
    @ResponseBody
    public Result getVisitNum() {
        return Result.OK().data(viceAbilityService.getVisitNum()).build();
    }


    @GetMapping("/getExtractNum")
    @ResponseBody
    public Result getExtractNum() {
        return Result.OK().data(viceAbilityService.getExtractNum()).build();
    }


    @PutMapping("/updateVisitNum")
    @ResponseBody
    public Result updateVisitNum() {
        viceAbilityService.updateVisitNum();
        return Result.OK().build();
    }

    @PutMapping("/updateExtractNum")
    @ResponseBody
    public Result updateExtractNum() {
        viceAbilityService.updateExtractNum();
        return Result.OK().build();
    }

}

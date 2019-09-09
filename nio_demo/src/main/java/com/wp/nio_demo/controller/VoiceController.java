package com.wp.nio_demo.controller;

import com.wp.nio_demo.vo.BillVo;
import com.wp.nio_demo.vo.RecvBillResp;
import com.wp.nio_demo.vo.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/voice")
public class VoiceController {


    @PostMapping(value = "/test", consumes = MediaType.TEXT_XML_VALUE , produces = MediaType.TEXT_XML_VALUE)
    public RecvBillResp recvBill(@RequestBody BillVo billVo){
        log.info(billVo.toString()+"=================");
        RecvBillResp resp = new RecvBillResp();
        resp.setAppid("12312");
        resp.setDesc("dfsdf");
        resp.setResult("4434234");
        return resp;
    }
}

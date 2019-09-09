package com.wp.nio_demo.vo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "vcresp")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecvBillResp {

    private String appid;

    /**
     * 处理结果：0成功，其他失败，
     */
    private String result;

    private String desc;
}

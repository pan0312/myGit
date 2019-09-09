package com.wp.nio_demo.vo;

import lombok.Data;

@Data
public class Bill {

    /**
     * 话单ID
     */
    private String recid;

    private String appid;

    /**
     * 主叫号码
     */
    private String caller;

    /**
     * 被叫号码（接听方）
     */
    private String called;

    /**
     * 接通时间，格式： YYYY-MM-DD HH:mm:ss
     */
    private String starttime;

    /**
     * 结束时间，格式： YYYY-MM-DD HH:mm:ss
     */
    private String endtime;

    /**
     * 话单状态(2呼叫完成，3被叫未接通)
     */
    private String status;

    /**
     * 错误码(当status=3时，用来标识前置机的错误信息。)
     */
    private String errorcode;

    /**
     * 话单入库时间
     */
    private String recdate;

    /**
     * 用户输入按键0-9,当请求外呼参数wait>0时，
     * 该值为用户输入的按键，如果[1.3版本加入]answer=-1代表用户无输入
     */
    private String answer;
}

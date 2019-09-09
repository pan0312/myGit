package com.wp.nio_demo.vo;

import lombok.Data;
import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlRootElement(name = "vc")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillVo {

    private String appid;

    @XmlElementWrapper(name = "bills")
    @XmlElement(name = "bill")
    private List<Bill> bills;
}

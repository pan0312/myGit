package com.wp.nio_demo.vo;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "vc")
public class Test {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "age")
    private String age;
}

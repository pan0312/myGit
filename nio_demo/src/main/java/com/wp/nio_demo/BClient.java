package com.wp.nio_demo;

import java.io.IOException;

public class BClient {

    public static void main(String[] args)
            throws IOException {
        new NioClient().start("BClient");
    }

}

package com.wp.nio_demo;

import java.io.IOException;

public class AClient {

    public static void main(String[] args)
            throws IOException {
        new NioClient().start("AClient");
    }

}

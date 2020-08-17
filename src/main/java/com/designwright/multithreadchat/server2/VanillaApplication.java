package com.designwright.multithreadchat.server2;

import com.designwright.multithreadchat.server2.loader.ApplicationLoader;
import com.designwright.multithreadchat.server2.loader.ApplicationStart;

@ApplicationStart
public class VanillaApplication {

    public static void main(String[] args) {
        new ApplicationLoader(VanillaApplication.class).start(args);
    }

}
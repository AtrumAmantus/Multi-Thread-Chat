package com.designwright.multithreadchat;

import com.designwright.core.loader.ApplicationLoader;
import com.designwright.core.container.ApplicationStart;

@ApplicationStart
public class VanillaApplication {

    public static void main(String[] args) {
        new ApplicationLoader(VanillaApplication.class).start(args);
    }

}
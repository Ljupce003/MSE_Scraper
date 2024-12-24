package org.example.dians.Component;

import org.springframework.stereotype.Component;

@Component
public class PythonRunnerFlag {
    public PythonRunnerFlag() {
        this.flag = false;
    }

    public static boolean flag;

    public  boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}

package com.leon.datalink.plugin;

public interface OutputChannel {

    void out(Object data);

    void out(int portIndex,Object data);

}

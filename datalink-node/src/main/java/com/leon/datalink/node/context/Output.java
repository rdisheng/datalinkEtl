package com.leon.datalink.node.context;


public interface Output {

   void out(Object data);

   void out(Integer portIndex,Object data);

}

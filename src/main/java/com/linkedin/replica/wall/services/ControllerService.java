package com.linkedin.replica.wall.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.impl.ControllerCommand;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.exceptions.WallException;


public class ControllerService {
    public static void serve(JsonObject body) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
        // iterate over request JSON body
        Iterator<?> keySetIter = body.keySet().iterator();
        String key, methodName;
        HashMap<String, Object> args = new HashMap<String, Object>();
        ControllerCommand controllerCommand = new ControllerCommand(args);

        while(keySetIter.hasNext()){
            key = keySetIter.next().toString();
            methodName = getControllerServiceMethodName(key);

            args.put("methodName", methodName);
            args.put("param", body.get(key));
            controllerCommand.execute();
        }

        // write any changes to configuration files
        Configuration.getInstance().commit();
    }

    /**
     * Maps requestBodykey to actual method name in controllerService class
     *
     * @param requestBodykey
     * 	key in JSON body. eg. setMaxThreadCount
     * @return
     * 	ControllerService method name
     */
    public static String getControllerServiceMethodName(String requestBodykey){
        Configuration config = Configuration.getInstance();
        // get mapping configuration key
        String key = config.getControllerConfigProp("controller.request.body."+requestBodykey.toLowerCase());
        if(key == null)
            throw new WallException(String.format("Invalid key: %s", requestBodykey));

        // ControllerService method name
        return config.getControllerConfigProp(key);
    }
}
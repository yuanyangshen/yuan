package com.shenyy.yuan.threadpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskManager {

    public static Map<String,ExcutePoolManager> taskPoolMap = new ConcurrentHashMap<>();



}

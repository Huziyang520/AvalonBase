package com.avalon.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AvalonBase 支持库模组全局常量。
 * mod_id 为 avalonbase，可被其他模组作为前置依赖引用。
 */
public class Constants {

    public static final String MOD_ID = "avalonbase";
    public static final String MOD_NAME = "AvalonBase";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
}

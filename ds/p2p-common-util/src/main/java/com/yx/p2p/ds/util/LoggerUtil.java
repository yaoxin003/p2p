package com.yx.p2p.ds.util;

import com.yx.p2p.ds.easyui.Result;
import org.slf4j.Logger;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/11/8:38
 */
public class LoggerUtil {

    public static Result addExceptionLog(Exception e,Logger logger) {
        String eStr = e.toString();
        Result result = Result.error(eStr);
        logger.error(eStr,e);
        return result;
    }

}

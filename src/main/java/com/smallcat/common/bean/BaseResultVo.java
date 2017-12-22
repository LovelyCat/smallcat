package com.smallcat.common.bean;

import java.io.Serializable;

/**
 * 通用返回对象 Vo (状态、信息)
 */
public class BaseResultVo implements Serializable {
    private static final long serialVersionUID = 4147707143358569260L;


    private int status = 500;
    private String msg = "";


    /**
     * 获取返回状态  200成功
     * @return
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * 设置请求状态  200成功
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }




    /**
     * 获取返回信息
     * 状态 = 200时，成功信息；
     * 状态 != 200时，错误信息;
     * @return
     */
    public String getMsg() {
        return msg;
    }
    /**
     * 设置返回信息
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BaseResultVo() {
    }
    public BaseResultVo(int status) {
        this.status = status;
    }

    public BaseResultVo(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}


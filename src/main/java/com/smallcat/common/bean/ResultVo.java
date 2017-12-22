package com.smallcat.common.bean;

import java.io.Serializable;

public class ResultVo<T extends Serializable> extends BaseResultVo implements Serializable {
    private static final long serialVersionUID = 4712972757347990461L;

    private T data = null;


    /**
     * 获取返回对象
     * @return
     */
    public T getData() {
        return data;
    }

    /**
     * 设置返回对象
     * @param data
     */
    public void setData(T data) {
        this.data = data;
    }

    public ResultVo() {
        super();
    }

    public ResultVo(int status) {
        super(status);
    }

    public ResultVo(int status, String msg) {
        super(status, msg);
    }
}

package com.potatoandtomato.common.utils;

/**
 * Created by SiongLeng on 29/4/2016.
 */
public abstract class RunnableArgs implements Runnable {

    Object[] m_args;

    public RunnableArgs() {
    }

    public void run(Object... args) {
        setArgs(args);
        run();
    }

    public void setArgs(Object... args) {
        m_args = args;
    }

    public int getArgCount() {
        return m_args == null ? 0 : m_args.length;
    }

    public Object[] getArgs() {
        return m_args;
    }
}
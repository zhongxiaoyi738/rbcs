package com.hsbc.common.utils.sm4;

public class SM4Context {

    public int mode = 1;
    public long[] sk = new long[32];
    public boolean isPadding = true;

    SM4Context() {

    }
}

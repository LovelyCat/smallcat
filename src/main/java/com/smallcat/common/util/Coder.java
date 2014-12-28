package com.smallcat.common.util;

public interface Coder {

	public String encode(byte[] data);

	public byte[] decode(String string);

}
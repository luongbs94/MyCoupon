package com.ln.model;

import com.activeandroid.serializer.TypeSerializer;

/**
 * Created by Nhahv on 8/18/2016.
 * <></>
 */

final public class ByteImages extends TypeSerializer {
    @Override
    public Class<?> getDeserializedType() {
        return byte[].class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public String serialize(Object data) {
        if (data == null) {
            return null;
        }
        return new String((byte[]) data);
    }

    @Override
    public byte[] deserialize(Object data) {
        if (data != null) {
            String str = (String) data;
            return str.getBytes();
        }
        return null;
    }
}

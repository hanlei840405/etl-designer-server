package com.nxin.framework.etl.designer.converter.bean;

import java.util.List;

public abstract class BeanConverter<T, S> {

    public abstract T convert(S s, boolean deep);

    public abstract List<T> convert(List<S> s);
}

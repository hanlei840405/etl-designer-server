package com.nxin.framework.etl.designer.event;

import com.nxin.framework.etl.designer.entity.analysis.Layout;
import com.nxin.framework.etl.designer.entity.basic.User;
import org.springframework.context.ApplicationEvent;

public class LayoutEvent extends ApplicationEvent {
    private User user;

    public LayoutEvent(Layout layout) {
        super(layout);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

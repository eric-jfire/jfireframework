package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync;

import com.jfireframework.jnet.server.CompletionHandler.weapon.WeaponWriteHandler;

public interface WeaponSyncWriteWithPushHandler extends WeaponWriteHandler
{
    /**
     * 当前可以放入的数据位置。从0开始。如果返回-1，意味着当前没有位置可以放入数据
     * 
     * @return
     */
    public long availablePut();
}

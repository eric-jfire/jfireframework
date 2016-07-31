package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync;

import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.WeaponWriteHandler;

public interface WeaponSyncWriteHandler extends WeaponWriteHandler
{
    public boolean availablePut();
}

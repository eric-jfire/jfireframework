package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync;

import com.jfireframework.jnet.server.CompletionHandler.WeaponReadHandler;

public interface WeaponCapacityReadHandler extends WeaponReadHandler
{
    public long cursor();
}

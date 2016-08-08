package com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync;

import com.jfireframework.jnet2.server.CompletionHandler.WeaponReadHandler;

public interface WeaponCapacityReadHandler extends WeaponReadHandler
{
    public long cursor();
}

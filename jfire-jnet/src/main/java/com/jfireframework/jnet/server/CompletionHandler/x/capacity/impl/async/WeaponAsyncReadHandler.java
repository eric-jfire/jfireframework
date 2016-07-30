package com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl.async;

import com.jfireframework.jnet.server.CompletionHandler.x.capacity.WeaponReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl.sync.WeaponSyncWriteHandler;

public interface WeaponAsyncReadHandler extends WeaponReadHandler
{
    public void endAsyncTryPublish();
    
    public WeaponSyncWriteHandler writeHandler();
}

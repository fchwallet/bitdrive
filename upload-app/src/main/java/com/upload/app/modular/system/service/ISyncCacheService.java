package com.upload.app.modular.system.service;

public interface ISyncCacheService {

	Boolean getLock(String lockName, int expireTime);

	void releaseLock(String lockName);
}

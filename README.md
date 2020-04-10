# bitdrive
offchian data sotrage services


### 通用：
>BaseURL: http://116.62.126.223   
```
时间戳过时时间15秒                              
  
加密signature：  
带有page字段的page不需要带入加密参数,sha256_HMAC(所有参数 ,"接口名称", key)所有参数依次	  
   
错误码：  
{"code":"100101","data":"","msg":"验证时间超时"}  
{"code":"100102","data":"","msg":"验证错误"}  
```	  

### 上传  
>接口名称: /api/upload  
```
参数:  
	access_key	string		   
	tnonce		string	时间戳   
	signature	string	加密后的signature（sha256_HMAC加密）   
	fileName	string   
   
signature参数格式：   
        TreeMap<String, String> queryParas = new TreeMap<>();  
        queryParas.put("access_key", "用户access_key");  
        queryParas.put("tnonce", 当前时间戳);  
        String signature = EncryptUtil.sha256_HMAC(queryParas, "/api/upload", "用户key");  
```
  
### 获取存储列表  
>接口名称: /api/getList  
```
参数  
	access_key	string		  
	tnonce		string	时间戳  
	signature	string	加密后的signature（sha256_HMAC加密）  
  
signature参数格式  
        TreeMap<String, String> queryParas = new TreeMap<>();  
        queryParas.put("access_key", "用户access_key");  
        queryParas.put("tnonce", 当前时间戳);  
        String signature = EncryptUtil.sha256_HMAC(queryParas, "/api/getList", "用户key");   
```
    
### 下载  
>接口名称: /api/download  
```
参数
	access_key	string		  
	tnonce		string	时间戳  
	signature	string	加密后的signature（sha256_HMAC加密）  
	fileId		string  
	    
signature参数格式：  
        TreeMap<String, String> queryParas = new TreeMap<>();  
        queryParas.put("access_key", "用户access_key");  
        queryParas.put("tnonce", 当前时间戳);  
        String signature = EncryptUtil.sha256_HMAC(queryParas, "/api/download", "用户key");  
```
  
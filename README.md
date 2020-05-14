# freedriveJ
java freedrive offchian data sotrage services


### 通用  
>BaseURL: http://116.62.126.223   
```
时间戳过时时间15秒                              
  
加密signature：  
带有page字段的page不需要带入加密参数,sha256_HMAC(所有参数 ,"接口名称", key)所有参数依次	  
   
错误码：  
{"code":"100101","验证时间超时"}  
{"code":"100102","验证错误"}  
{"code":"200212","上传失败，请联系客服解决"}
{"code":"200211","文件大于15M"}

```	  

### 存数据到freedrive  
>接口名称: /api/put
```
参数:  
	access_key	string		   
	tnonce		string	时间戳   
	signature	string	加密后的signature（sha256_HMAC加密）   
	fch_addr 	string  fch 地址，可多个
	metadata	string   
	data 		string
   
返回结果：         
    {
        "code": 200,
	"drive_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
    }   
    
```
  
### 从freedrive获取存储内容
>接口名称: /api/get
```
参数  
	access_key	string		  
	tnonce		string	时间戳  
	signature	string	加密后的signature（sha256_HMAC加密）  
	fch_addr	string  fch地址
	drive_id	string  drive_id
  
返回结果：
    {
        "code":200,
	"metadata": {},
	"data": {}
    }    
```
    
### 获取FCH地址的存储列表  
>接口名称: /api/get_drive_id
```
参数
	access_key	string		  
	tnonce		string	时间戳  
	signature	string	加密后的signature（sha256_HMAC加密）  
	fch_addr	string  fch 地址
	    
返回结果：
	{
	   "code":200,
	   "drive_id": ["f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6", "f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6"]	
	}
```

部署配置：
    修改application,yml 数据库地址，系统地址，utxo接口，节点配置即可

# freedriveJ
java freedrive offchian data sotrage services


### 通用  
>BaseURL: http://116.62.126.223:8442   
```
错误码：  
{"code":"100101","验证时间超时"}  
{"code":"100102","验证错误"}  
{"code":"200212","上传失败，请联系客服解决"}
{"code":"200211","data大于5M"}

```	  

### 存数据到freedrive  
>接口名称: /api/put
```
参数  
{
"fch_addr": ["F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT"], 
"metadata":"04464549500102010306555044415445321e6dc8d860dd8a54ce8e6bfe15f52d05a9e594dc75c112156354bd6b461e340932681cc9f485f8e8f47f622de0035b05bfc161869fb46ccc915c36e2366d77ef8d",
"data": "010101010",
"signature": sign(data)
}   
   
返回结果：         
{
  "code": 200,
  "drive_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}   
    
```



### 更新drive_id的内容  
>接口名称: /api/update
```
参数  
{
"fch_addr": ["F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT"], 
"metadata":"04464549500102010306555044415445321e6dc8d860dd8a54ce8e6bfe15f52d05a9e594dc75c112156354bd6b461e340932681cc9f485f8e8f47f622de0035b05bfc161869fb46ccc915c36e2366d77ef8d",
"data": "010101010",
"signature": sign(data),
"drive_id":  需要更新的drive_id
}   
   
返回结果：         
{
  "code": 200,
  "update_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}   
    
```



### 从freedrive获取存储内容
>接口名称: /api/get
```

参数  
{
"fch_addr": "F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT", 
"drive_id":  "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}   
返回结果：
{
    "code":200,
    "put":
    {
      "metadata": {},
      "data": {}
    }
    "update":
    [
      { 
        "update_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
        "metadata": {},
        "data": {}
      },
      {
        "update_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce" 
        "metadata": {},
        "data": {}
      }
    ]
}

或者参数传update_id, 查询某次更新记录
{
"update_id":  "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}    
返回结果
{
    "code":200,
    { 
      "metadata": {},
      "data": {}
    }
}    
```
    
### 获取FCH地址的存储列表  
>接口名称: /api/get_drive_id
```


参数  
{
"fch_addr":  "f4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}   

	    
返回结果：
{
   "code":200,
   "drive_id": ["f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6", "f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6"]	
}
```


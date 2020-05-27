# freedriveJ
java freedrive offchian data sotrage services


### 通用  
>BaseURL: http://116.62.126.223:8442     
所有接口都是post请求.
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
"metadata":"61869fb46ccc915c36e2366d77ef8d", (hex 字符串)
"data": "010101010",(hex 字符串)
"signature": sign(data) 用fch_addr 签名data字段内容的签名
}   
   
返回结果：         
{
  "code": 200,
  "drive_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}   

curl example:
curl http://116.62.126.223:8442/api/put  -X POST  -d @put.json  --header "Content-Type:application/json"
    
```


### 更新drive_id的内容  
>接口名称: /api/update
```
参数  
{
"fch_addr": ["F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT"], 
"metadata":"044bfc161869fb46ccc915c36e2366d77ef8d",(hex 字符串)
"data": "010101010",(hex 字符串)
"signature": sign(data),用fch_addr 签名data字段内容的签名
"drive_id":  需要更新的drive_id
}   
   
返回结果：         
{
  "code": 200,
  "update_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}   

curl example:
curl http://116.62.126.223:8442/api/update  -X POST  -d @update.json  --header "Content-Type:application/json"  
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
```   
或者参数传update_id, 查询某次更新记录    
```
{
"fch_addr": "F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT", 
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
```
curl example:
curl http://116.62.126.223:8442/api/get -X POST  -d 'fch_addr=F8Z2aQkHkBFhb3GQfEWV7L88yMuApj7jMK&drive_id=8d6cc0f1f6aa1f4535262f65466871a5865b0c94bb49ea5c5695917545aead93'      
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

curl example:
curl http://116.62.126.223:8442/api/get_drive_id -X POST  -d 'fch_addr=F8Z2aQkHkBFhb3GQfEWV7L88yMuApj7jMK ' 

```


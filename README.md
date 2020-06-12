# freedriveJ -- own your own data.
java implementation freedrive, see [architecture](./Freedrive-architecture.pdf)
### 目录
[1. put](#put)  
[2. update](#update)  
[3. get](#get)  
[4. get_drive_id](#get-drive-id)  
[5. get_balance](#get-balance)  
[6. fronze_drive_id](#froze-drive_id)  

### 通用  
>URL: http://freedrive.fchwallet.com:8442       
所有接口都是post请求.
```
错误码：  
{"code":"100101","验证时间超时"}  
{"code":"100102","验证错误"}  
{"code":"100457","付费失败"}
{"code":"200212","上传失败，请联系客服解决"}
{"code":"200211","data大于5M"}
{"code":"200212","用户积分不足"}
{"code":"1001","sign验证失败"}
{"code":"400","参数错误"}
{"code":"400","参数错误"}
{"code":"1000","当前操作没有权限"}
{"code":"1002","该drive_id不存在"}
{"code":"1005","当前地址找不到相应记录"}
{"code":"1006","找不到更新记录"}
{"code":"1009","请传正确的json数据"}

```	  

### put   
>存数据到freedrive      
>接口名称: /api/put
```
参数类型： ["application/json"]  
{
"fch_addr": ["F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT"], 
"metadata":"61869fb46ccc915c36e2366d77ef8d", (hex 字符串)
"data": "010101010",(hex 字符串)
"signature": sign(data) 用fch_addr 签名data字段内容的签名
}   
   
返回结果         
{
  "code": 200,
  "drive_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
}   
```
curl example
```
curl http://freedrive.fchwallet.com:8442/api/put  -X POST  -d @put.json  --header "Content-Type:application/json"
```


### update
>更新drive_id的内容  
>接口名称: /api/update
```
参数类型: ["application/json"]    
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
```
curl example
```
curl http://freedrive.fchwallet.com:8442/api/update  -X POST  -d @update.json  --header "Content-Type:application/json"
```



### get
>从freedrive获取存储内容   
>接口名称: /api/get

查询单个drive_id的所有变更记录
```
参数类型: ["application/x-www-form-urlencoded"]  
'fch_addr=F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT&drive_id=1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce'
  
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
参数类型: ["application/x-www-form-urlencoded"]  

'fch_addr=F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT&update_id=1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce'
    
返回结果
{
    "code":200,
    { 
      "metadata": {},
      "data": {}
    }
}

```  
curl example
```
curl http://freedrive.fchwallet.com:8442/api/get -X POST  -d 'fch_addr=F8Z2aQkHkBFhb3GQfEWV7L88yMuApj7jMK&drive_id=8d6cc0f1f6aa1f4535262f65466871a5865b0c94bb49ea5c5695917545aead93'      
```
    
### get drive id
>获取FCH地址的存储列表     
>接口名称: /api/get_drive_id
```
参数类型: ["application/x-www-form-urlencoded"]    

'fch_addr=f4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce'
   
	    
返回结果：
{
   "code":200,
   "drive_id": ["f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6", "f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6"]	
}
```
curl example
```
curl http://freedrive.fchwallet.com:8442/api/get_drive_id -X POST  -d 'fch_addr=F8Z2aQkHkBFhb3GQfEWV7L88yMuApj7jMK ' 
```

### get balance
>获取FCH地址的积分余额     
>接口名称: /api/get_balance
```
参数类型: ["application/x-www-form-urlencoded"]    
'fch_addr=f4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce'

返回结果:
{
   "code": 200,
   "balance": 12345	
}
```



### frozen drive_id
>冻结drive_id, 冻结后不再被修改。     
>接口名称: /api/frozen_drive_id
```
参数类型: ["application/json"]    
{
  'drive_id=f4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce'
}

返回结果:
{
   "code": 200,
}
```
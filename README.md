# freedriveJ -- own your own data.
java implementation freedrive, see [architecture](./Freedrive-architecture.pdf)
### 目录
[1. put](#put)  
[2. update](#update)  
[3. get](#get)  
[4. get_drive_id](#get-drive_id)  
[5. get_balance](#get-balance)  
[6. get_tx_history](#get-tx-history)  
[7. terminate_drive_id](#terminate-drive_id)  
[8. get_auth](#get-auth)  
[9. auth](#auth)  

### 通用  
>URL: http://freedrive.fchwallet.com:8442       
所有接口都是post请求.
```
所有接口的参数签名(signature)字段计算规则:
1) params_concat =  p1 + p2 ... + timestamp
2) hash = sha256(params_concat)
3) signature = ecdsa(hash, addr_private), 用私钥签名hash  

错误码：  
{"code":"100101","验证时间超时"}  
{"code":"100102","验证错误"}  
{"code":"100457","付费失败"}
{"code":"200212","上传失败，请联系客服解决"}
{"code":"200211","data大于5M"}
{"code":"200214","用户积分不足"}
{"code":"1001","sign验证失败"}
{"code":"400","参数错误"}
{"code":"400","参数错误"}
{"code":"1000","当前操作没有权限"}
{"code":"1002","该drive_id不存在"}
{"code":"1005","当前地址找不到相应记录"}
{"code":"1006","找不到更新记录"}
{"code":"1009","请传正确的json数据"}
{"code":"200213","token余额和链上不对应请稍后重试"}
{"code":"100456","该driveid已经结束"}

计费说明: (每次请求即时扣除)
{put:                  10积分}
{update:               10积分}
{get:                  2积分}
{get_drive_id:         2积分}
{terminate_drive_id:   10积分}
{auth:                 10积分}
{get_auth:             2积分}

更新日志：
2020/4/10
发布第一个原型版本
2020/5/24
接口重构调整为
put, update, get, get_drive_id 
2020/6/18
1) get 接口增加"type"字段
2) 增加计费相关接口, get_balance, get_tx_history
3) 增加终止修改drive_id: terminate_drive_id
2020/7/1
1) 所有接口增加timestamp字段防止重复请求,且按新签名规则签名所有参数
2) 增加授权接口: get_auth, auth
```

### put   
>存数据到freedrive      
>接口名称: /api/put
```
参数类型： ["application/json"]  
{
"addr": ["F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT"], 
"metadata":"61869fb46ccc915c36e2366d77ef8d", (hex 字符串)
"data": "010101010",(hex 字符串),
"timestamp": "1593550887",
"signature": "xxxxxxx"
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
"addr": ["F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT"], 
"metadata":"044bfc161869fb46ccc915c36e2366d77ef8d",(hex 字符串)
"data": "010101010",(hex 字符串),
"drive_id":  需要更新的drive_id,
"timestamp": "1593550887",
"signature": "xxxxxxxxx"
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

查询单个drive_id的所有变更记录(type = 1 data的数据为链接 type = 0  data数据为正常数据)
```
参数类型: ["application/json"]  
{
  "addr":"F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT",
  "drive_id":"1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce",
  "timestamp":"1593550887",
  "signature":"xxxxxxxx"	
}

  
返回结果：
{
    "code":200,
    "put":
    {
      "metadata": {},
      "data": "0101",
      "type": 0
    }
    "update":
    [
      { 
        "update_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce"
        "metadata": {},
        "data": "http://xxx.xxx.xxx",
        "type": 1
      },
      {
        "update_id": "1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce" 
        "metadata": {},
        "data": "0101",
        "type": 0
      }
    ]
}
```   
或者参数传update_id, 查询某次更新记录    
```
参数类型: ["application/json"]  
{
  "addr":"F9A9TgNE2ixYhQmEnB15BNYcEuCvZvzqxT",
  "drive_id":"1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce",
  "timestamp":"1593550887",
  "signature":"xxxxxxx"	
}

    
返回结果
{
    "code":200,
    { 
      "metadata": {},
      "data": {},
      "type": 0
    }
}

```  
curl example
```
curl http://freedrive.fchwallet.com:8442/api/get -X POST  -d @get.json --header "Content-Type:application/json"      
```
    
### get drive_id
>获取FCH地址的存储列表     
>接口名称: /api/get_drive_id
```
参数类型: ["application/json"]    
{
  "addr":"F8Z2aQkHkBFhb3GQfEWV7L88yMuApj7jMK",
  "drive_id":"8d6cc0f1f6aa1f4535262f65466871a5865b0c94bb49ea5c5695917545aead93",
  "timestamp":"1593550887",
  "signature":"xxxxxxxxxxxxx"	
}

   
	    
返回结果：
{
   "code":200,
   "drive_id": ["f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6", "f613da5785cfcfbb5c4d47e8dd11156712c8b9fa169881ec4c805ea4f6f1b6b6"]	
}
```
curl example
```
curl http://freedrive.fchwallet.com:8442/api/get_drive_id -X POST  -d @get_drive_id.json --header "Content-Type:application/json" ' 
```

### get balance
>获取FCH地址的积分余额     
>接口名称: /api/get_balance
```
参数类型: ["application/json"]    
{
  "addr":"F8Z2aQkHkBFhb3GQfEWV7L88yMuApj7jMK",
  "timestamp":"1593550887",
  "signature":"xxxxxxxxxxx"	
}


返回结果:
{
   "code": 200,
   "balance": 12345	
}
```

curl example
```
curl http://freedrive.fchwallet.com:8442/api/get_balance -X POST  -d @get_balance --header "Content-Type:application/json" ' 
```

### get tx history
>获取余额变更记录     
>接口名称: /api/get_tx_history
```
参数类型: ["application/json"]    
{
  "addr":"1QrD3JVeeJxT56coCwCoPxi7Bm91unnyM",
  "timestamp":"1593550887",
  "signature":"xxxxxxxxxxxx"	
}
	


返回结果:
{
   "code": 200,
   "data":[{"type":"put", "change": -10, "timestamp":2020-06-17 19:09:02},
	   {"type":"update", "change":-10,"timestamp":2020-06-17 19:09:02},
	   {"type":"terminate_drive_id", "change":-10,"timestamp":2020-06-17 19:09:02},
	   {"type":"get", "change":-2,"timestamp":2020-06-17 19:09:02},
	   {"type":"get_drive_id", "change":-2,"timestamp":2020-06-17 19:09:02},
	   {"type":"charge", "change":100,"timestamp":2020-06-17 19:09:02},
	  ]
}
```

curl example
```
curl http://freedrive.fchwallet.com:8442/api/get_tx_history -X POST  -d @tx_history.json --header "Content-Type:application/json"
```

### terminate drive_id
>终止drive_id, 终止后无法再被修改。     
>接口名称: /api/terminate_drive_id
```
参数类型: ["application/json"]    
{
  "addr':"1QrD3JVeeJxT56coCwCoPxi7Bm91unnyM",	
  "drive_id":"f4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce",
  "timestamp":"1593550887",
  "signature":"xxxxxxxxxxxxx"
}

返回结果:
{
   "code": 200,
}
```

curl example
```
curl http://freedrive.fchwallet.com:8442/api/terminate_drive_id  -X POST  -d @terminate_drive_id.json  --header "Content-Type:application/json"
```




### get auth 
>获取drive_id 授权信息。     
>接口名称: /api/get_auth

参数类型: ["application/x-www-form-urlencoded"]    
'addr=1QrD3JVeeJxT56coCwCoPxi7Bm91unnyM&drive_id=xxxxxxxxxx&timestamp=1593550887&signature=xxxxxxxxxxxx'	
{
  "addr":"",
  "drive_id":"",
  "timestamp":"",
  "signature":""	
}

```
返回结果:
{
  "code": 200,
  "data": {	
  "drive_id":"f4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce",
  "admin":[addr1, addr2, ..., addrn],
  "member":[addr11, addr22, ..., addrn2]
  }
}

admin 只有admin权限的地址才能更改授权
member 可以有多个地址，可以修改drive_id, 但不能修改权限

```


### auth 
>授权addrs, drive_id     
>接口名称: /api/auth
```
参数类型: ["application/json"]    
{
  "addr":"1QrD3JVeeJxT56coCwCoPxi7Bm91unnyM",	
  "drive_id":"f4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce",
  "admin":[addr1, addr2, ..., addrn],
  "member":[addr11, addr22, ..., addrn2],
  "timestamp":"1593550887",
  "signature":"xxxxxxxxxxxxx"
}


admin 只有admin权限的地址才能更改授权
member 可以有多个地址，可以修改drive_id, 但不能修改权限

返回结果:
{
   "code": 200,
}
```

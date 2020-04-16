/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyz.upload.app.core.common.constant.dictmap;

import com.xyz.upload.app.core.common.constant.dictmap.base.AbstractDictMap;

/**
 * 部门的映射
 *
 * @author fengshuonan
 * @date 2017-05-06 15:01
 */
public class WithdrawalMap extends AbstractDictMap {

    @Override
    public void init() {
        put("id", "提现ID");
        put("aliAccount", "提现账号");
        put("aliName", "提现人姓名");
        put("userid", "用户ID");
        put("score", "提现积分");
        put("status", "提现状态");
        put("remarks", "备注");
        put("createTime", "提现发起时间");
    }

    @Override
    protected void initBeWrapped() {
    }
}

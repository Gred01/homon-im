/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   ohun@live.cn (夜色)
 */

package com.max.homon.kit.netty.base;



import com.max.homon.core.enums.ClientType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.crypto.Cipher;

/**
* 会话上下文，存储客户端相关信息
*@Author Gred
*@Date 2020/3/16 22:49
*@version 1.0
**/
@Setter
@Getter
@ToString
public final class SessionContext {
    public String osName;
    public String osVersion;
    public String clientVersion;
    public String deviceId;
    public String userId;
    public String tags;
    /*** 心跳 ***/
    public int heartbeat;
    //public Cipher cipher;
    private byte clientType;

    public void changeCipher(Cipher cipher) {

    }

    public boolean handshakeOk() {
        return deviceId != null && deviceId.length() > 0;
    }

    public int getClientType() {
        if (clientType == 0) {
            clientType = (byte) ClientType.find(osName).type;
        }
        return clientType;
    }

}

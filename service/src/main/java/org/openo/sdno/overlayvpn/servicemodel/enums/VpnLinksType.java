/*
 * Copyright 2017 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.sdno.overlayvpn.servicemodel.enums;

import org.openo.sdno.overlayvpn.servicemodel.sbi.NetIpsecConnection;
import org.openo.sdno.overlayvpn.servicemodel.sbi.NetVxlanConnection;

/**
 * The vpn connection type.<br>
 * 
 * @author
 * @version SDNO 0.5 Jan 17, 2017
 */
public enum VpnLinksType {
    IPSEC("ipsec", NetIpsecConnection.class), VXLAN("vxlan", NetVxlanConnection.class);

    private String type;

    @SuppressWarnings("rawtypes")
    private Class connection;

    @SuppressWarnings("rawtypes")
    private VpnLinksType(String type, Class connection) {
        this.type = type;
        this.connection = connection;
    }

    /**
     * Get VpnLinksType by type name.<br>
     * 
     * @param type The type name
     * @return The VpnLinksType object
     * @since SDNO 0.5
     */
    public static VpnLinksType fromType(String type) {
        for(VpnLinksType vpnLinks : VpnLinksType.values()) {
            if(vpnLinks.type.equalsIgnoreCase(type)) {
                return vpnLinks;
            }
        }
        return null;
    }

    public Class<?> getConnection() {
        return connection;
    }

}
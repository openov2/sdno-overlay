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

package org.openo.sdno.overlayvpn.model.v2.route;

import org.openo.sdno.overlayvpn.inventory.sdk.model.annotation.MOResType;
import org.openo.sdno.overlayvpn.verify.annotation.AInt;
import org.openo.sdno.overlayvpn.verify.annotation.AString;
import org.openo.sdno.overlayvpn.verify.annotation.AUuid;

@MOResType(infoModelName = "sbinestaticroute")
public class SbiNeStaticRoute extends SbiRouteNetModel {

    @AString(require = true, max = 255)
    private String destIp = null;

    @AString(require = true, scope = "true,false")
    private String enableDhcp = null;

    @AString(min = 0, max = 255)
    private String nextHop = null;

    @AString(min = 0, max = 255)
    private String outInterface = null;

    @AInt(min = 0, max = 255)
    private String priority = null;

    @AUuid
    private String nqa = null;

    /**
     * Constructor<br/>
     * 
     * @since SDNO 0.5
     */
    public SbiNeStaticRoute() {
        super();
    }

    /**
     * destination IP address
     * 
     * @return destIp
     **/
    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    /**
     * enable dhcp or not
     * 
     * @return enableDhcp
     **/
    public String getEnableDhcp() {
        return enableDhcp;
    }

    public void setEnableDhcp(String enableDhcp) {
        this.enableDhcp = enableDhcp;
    }

    /**
     * next hop IP address
     * 
     * @return nextHop
     **/
    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    /**
     * outbound interface
     * 
     * @return outInterface
     **/
    public String getOutInterface() {
        return outInterface;
    }

    public void setOutInterface(String outInterface) {
        this.outInterface = outInterface;
    }

    /**
     * the priority of the route
     * 
     * @return priority
     **/
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * network quality analyzer
     * 
     * @return nqa
     **/
    public String getNqa() {
        return nqa;
    }

    public void setNqa(String nqa) {
        this.nqa = nqa;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SbiNeStaticRoute {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    destIp: ").append(toIndentedString(destIp)).append("\n");
        sb.append("    enableDhcp: ").append(toIndentedString(enableDhcp)).append("\n");
        sb.append("    nextHop: ").append(toIndentedString(nextHop)).append("\n");
        sb.append("    outInterface: ").append(toIndentedString(outInterface)).append("\n");
        sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
        sb.append("    nqa: ").append(toIndentedString(nqa)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if(o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

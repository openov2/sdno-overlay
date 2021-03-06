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

package org.openo.sdno.overlayvpn.servicemodel.base;

/**
 * Policy route action model.<br>
 * 
 * @author
 * @version SDNO 0.5 Jan 10, 2017
 */
public class PolicyAction {

    private String policy;

    private IP nextHopIp;

    private String outInterFace;

    /**
     * Constructor<br>
     * 
     * @since SDNO 0.5
     */
    public PolicyAction() {
        super();
    }

    /**
     * Constructor<br>
     * 
     * @param nextHopIp nextHopIp to set
     * @param outInterFace outInterFace to set
     * @since SDNO 0.5
     */
    public PolicyAction(IP nextHopIp, String outInterFace) {
        super();
        this.nextHopIp = nextHopIp;
        this.outInterFace = outInterFace;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public IP getNextHopIp() {
        return nextHopIp;
    }

    public void setNextHopIp(IP nextHopIp) {
        this.nextHopIp = nextHopIp;
    }

    public String getOutInterFace() {
        return outInterFace;
    }

    public void setOutInterFace(String outInterFace) {
        this.outInterFace = outInterFace;
    }

}

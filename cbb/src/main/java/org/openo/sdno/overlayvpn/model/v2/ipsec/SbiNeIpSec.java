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

package org.openo.sdno.overlayvpn.model.v2.ipsec;

import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openo.sdno.overlayvpn.inventory.sdk.model.annotation.MOResType;
import org.openo.sdno.overlayvpn.inventory.sdk.model.annotation.NONInvField;
import org.openo.sdno.overlayvpn.verify.annotation.AString;

@MOResType(infoModelName = "ipsec_sbi_neipsec")
public class SbiNeIpSec extends SbiIpSecNetModel {

    @AString(min = 0, max = 36)
    private String externalIpSecId;

    @JsonProperty(value = "sourceIfId")
    @AString(require = true, min = 1, max = 36)
    private String soureIfName;

    @JsonIgnore
    private String destIfName;

    @AString(require = true)
    private String sourceAddress;

    @AString(require = true)
    private String peerAddress;

    @NONInvField
    @NotNull
    private SbiIkePolicy ikePolicy;

    @NONInvField
    @NotNull
    private SbiIpSecPolicy ipSecPolicy;

    @AString(require = true, scope = "work,protect")
    private String workType = "work";

    @AString(min = 0, max = 4096)
    private String sourceLanCidrs;

    @AString(min = 0, max = 4096)
    private String peerLanCidrs;

    @AString(require = true, scope = "true,false")
    private String isTemplateType;

    @NONInvField
    private String nqa;

    @AString(require = true, scope = "localcpe,cloudcpe,vpc,dc-r")
    private String localNeRole;

    private String tenantName;

    private String protectionPolicy;

    @AString(scope = "true,false")
    private String qosPreClassify;

    private String regionId;

    /**
     * Get externalIpSecId
     * 
     * @return externalIpSecId
     **/
    public String getExternalIpSecId() {
        return externalIpSecId;
    }

    public void setExternalIpSecId(String externalIpSecId) {
        this.externalIpSecId = externalIpSecId;
    }

    /**
     * Get sourceIfName
     * 
     * @return sourceIfName
     **/
    public String getSoureIfName() {
        return soureIfName;
    }

    public void setSoureIfName(String soureIfName) {
        this.soureIfName = soureIfName;
    }

    /**
     * Get destIfName
     * 
     * @return destIfName
     **/
    public String getDestIfName() {
        return destIfName;
    }

    public void setDestIfName(String destIfName) {
        this.destIfName = destIfName;
    }

    /**
     * Get sourceAddress
     * 
     * @return sourceAddress
     **/
    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    /**
     * Get peerAddress
     * 
     * @return peerAddress
     **/
    public String getPeerAddress() {
        return peerAddress;
    }

    public void setPeerAddress(String peerAddress) {
        this.peerAddress = peerAddress;
    }

    /**
     * Get ikePolicy
     * 
     * @return ikePolicy
     **/
    public SbiIkePolicy getIkePolicy() {
        return ikePolicy;
    }

    public void setIkePolicy(SbiIkePolicy ikePolicy) {
        this.ikePolicy = ikePolicy;
    }

    /**
     * Get ipSecPolicy
     * 
     * @return ipSecPolicy
     **/
    public SbiIpSecPolicy getIpSecPolicy() {
        return ipSecPolicy;
    }

    public void setIpSecPolicy(SbiIpSecPolicy ipSecPolicy) {
        this.ipSecPolicy = ipSecPolicy;
    }

    /**
     * work type(work,protect)
     * 
     * @return workType
     **/
    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    /**
     * Get sourceLanCidrs
     * 
     * @return sourceLanCidrs
     **/
    public String getSourceLanCidrs() {
        return sourceLanCidrs;
    }

    public void setSourceLanCidrs(String sourceLanCidrs) {
        this.sourceLanCidrs = sourceLanCidrs;
    }

    /**
     * Get peerLanCidrs
     * 
     * @return peerLanCidrs
     **/
    public String getPeerLanCidrs() {
        return peerLanCidrs;
    }

    public void setPeerLanCidrs(String peerLanCidrs) {
        this.peerLanCidrs = peerLanCidrs;
    }

    /**
     * boolean(true,false)
     * 
     * @return isTemplateType
     **/
    public String getIsTemplateType() {
        return isTemplateType;
    }

    public void setIsTemplateType(String isTemplateType) {
        this.isTemplateType = isTemplateType;
    }

    /**
     * Get nqa
     * 
     * @return nqa
     **/
    public String getNqa() {
        return nqa;
    }

    public void setNqa(String nqa) {
        this.nqa = nqa;
    }

    /**
     * local ne role(thincpe,cloudcpe,vpc)
     * 
     * @return localNeRole
     **/
    public String getLocalNeRole() {
        return localNeRole;
    }

    public void setLocalNeRole(String localNeRole) {
        this.localNeRole = localNeRole;
    }

    /**
     * Get tenantName
     * 
     * @return tenantName
     **/
    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /**
     * Get protectionPolicy
     * 
     * @return protectionPolicy
     **/
    public String getProtectionPolicy() {
        return protectionPolicy;
    }

    public void setProtectionPolicy(String protectionPolicy) {
        this.protectionPolicy = protectionPolicy;
    }

    /**
     * boolean(true,false)
     * 
     * @return qosPreClassify
     **/
    public String getQosPreClassify() {
        return qosPreClassify;
    }

    public void setQosPreClassify(String qosPreClassify) {
        this.qosPreClassify = qosPreClassify;
    }

    /**
     * Get regionId
     * 
     * @return regionId
     **/
    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

}

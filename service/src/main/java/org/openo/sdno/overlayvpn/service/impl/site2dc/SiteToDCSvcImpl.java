/*
 * Copyright (c) 2016, Huawei Technologies Co., Ltd.
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
 */

package org.openo.sdno.overlayvpn.service.impl.site2dc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.overlayvpn.consts.CommConst;
import org.openo.sdno.overlayvpn.dao.common.InventoryDao;
import org.openo.sdno.overlayvpn.errorcode.ErrorCode;
import org.openo.sdno.overlayvpn.model.common.enums.AdminStatus;
import org.openo.sdno.overlayvpn.model.servicemodel.Connection;
import org.openo.sdno.overlayvpn.model.servicemodel.EndpointGroup;
import org.openo.sdno.overlayvpn.model.servicemodel.OverlayVpn;
import org.openo.sdno.overlayvpn.model.servicemodel.SiteToDc;
import org.openo.sdno.overlayvpn.model.servicemodel.Vpc;
import org.openo.sdno.overlayvpn.osdriver.OSDriverConfigUtil;
import org.openo.sdno.overlayvpn.result.ResultRsp;
import org.openo.sdno.overlayvpn.security.authentication.TokenDataHolder;
import org.openo.sdno.overlayvpn.service.impl.overlayvpnsvc.connection.ConnectionSvcImpl;
import org.openo.sdno.overlayvpn.service.impl.overlayvpnsvc.endpointgroup.EndPointGrpSvcImpl;
import org.openo.sdno.overlayvpn.service.impl.overlayvpnsvc.overlayvpn.OverlayVpnSvcImpl;
import org.openo.sdno.overlayvpn.service.inf.overlayvpn.ISiteToDC;
import org.openo.sdno.overlayvpn.util.check.CheckStrUtil;
import org.openo.sdno.overlayvpn.util.exception.ThrowOverlayVpnExcpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Site To DC service implementation<br/>
 * <p>
 * Implements Site to DC between enterprise Site to Tenant VPC in DC or vDC.
 * </p>
 *
 * @author
 * @version SDNO 0.5 May 24, 2016
 */
@Service
public class SiteToDCSvcImpl implements ISiteToDC {

    @Autowired
    private OverlayVpnSvcImpl overlayVpnSvc;

    @Autowired
    private ConnectionSvcImpl connectionSvc;

    @Autowired
    private EndPointGrpSvcImpl endPointGrpSvc;

    @Autowired
    private SiteToDCOverlayVPN siteToDCOverlayVPN;

    @Autowired
    private InventoryDao<OverlayVpn> inventoryDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteToDCSvcImpl.class);

    /**
     * Constructor<br/>
     *
     * @since SDNO 0.5
     */
    public SiteToDCSvcImpl() {
        super();
    }

    /**
     * Site To DC create operation<br/>
     *
     * @param httpContext - HTTP context for security token
     * @param siteToDC - Overlay VPN information
     * @return Success or Failure and SiteToDC information
     * @throws ServiceException - when deploy the service fails or update database fails
     * @since SDNO 0.5
     */
    @Override
    public ResultRsp<SiteToDc> create(HttpServletRequest req, HttpServletResponse resp, SiteToDc siteToDc)
            throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();

        // 1.Create overlay VPN and store in database in CREATING state.
        OverlayVpn overlayVpn = siteToDCOverlayVPN.createOverlayVpn(req, resp, siteToDc);

        // 2.Load OSDriver Info
        OSDriverConfigUtil.LoadOSDriverConfigData();

        // 3. Create VxLan Connection
        Connection connection = siteToDCOverlayVPN.createVxLanVpnConnection(req, resp, overlayVpn);
        List<EndpointGroup> vxLanEpgList = siteToDCOverlayVPN.createEpgForVxLan(req, resp, siteToDc, overlayVpn);

        @SuppressWarnings("unchecked")
        List<String> vxLanEpgIdList = new ArrayList<String>(CollectionUtils.collect(vxLanEpgList, new Transformer() {

            @Override
            public Object transform(Object arg0) {
                return ((EndpointGroup)arg0).getUuid();
            }
        }));

        connection.setEpgIds(vxLanEpgIdList);
        connection.setEndpointGroups(vxLanEpgList);

        // 4. Create VPC & VPC Subnet
        Vpc vpcNetwork = siteToDCOverlayVPN.createVpcAndSubnet(req, siteToDc);

        // 5. Create IpSec Connection
        Connection ipSecConnection = siteToDCOverlayVPN.createIpSecConnection(req, resp, overlayVpn);
        List<EndpointGroup> ipSecEpgList =
                siteToDCOverlayVPN.createEpgForIpSec(req, resp, siteToDc, overlayVpn, vpcNetwork);

        @SuppressWarnings("unchecked")
        List<String> ipSecEpgIdList = new ArrayList<String>(CollectionUtils.collect(ipSecEpgList, new Transformer() {

            @Override
            public Object transform(Object arg0) {
                return ((EndpointGroup)arg0).getUuid();
            }
        }));

        ipSecConnection.setEpgIds(ipSecEpgIdList);
        ipSecConnection.setEndpointGroups(ipSecEpgList);

        // 6. Update and deploy OverlayVPN for Site To DC for VxLAN &IPsec service
        overlayVpn.setAdminStatus(AdminStatus.ACTIVE.getName());
        ResultRsp<OverlayVpn> resultRsp = overlayVpnSvc.deploy(req, resp, overlayVpn);
        ThrowOverlayVpnExcpt.checkRspThrowException(resultRsp);

        // 7. Update database
        ResultRsp<OverlayVpn> resultDbRsp = inventoryDao.update(overlayVpn, null);
        ThrowOverlayVpnExcpt.checkRspThrowException(resultDbRsp);

        LOGGER.info("Exit query Site2DC method. Cost time = " + (System.currentTimeMillis() - infterEnterTime));

        return new ResultRsp<SiteToDc>(ErrorCode.OVERLAYVPN_SUCCESS, siteToDc);

    }

    @Override
    public ResultRsp<List<SiteToDc>> batchQuery(HttpServletRequest req, HttpServletResponse resp, String tenantId,
            String filter) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultRsp<SiteToDc> deploy(HttpServletRequest req, HttpServletResponse resp, SiteToDc request)
            throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultRsp<SiteToDc> undeploy(HttpServletRequest req, HttpServletResponse resp, SiteToDc request)
            throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Query Overlay VPN information for Site to DC scenario<br/>
     *
     * @param httpContext - HTTP Context to get the tenant ID
     * @param uuid - UUID of the overlay VPN
     * @param tenantId - Tenant ID
     * @return - SiteToDC information
     * @throws ServiceException- when resource do not exist or fetching from database fails
     * @since SDNO 0.5
     */
    @Override
    public ResultRsp<SiteToDc> query(HttpServletRequest req, HttpServletResponse resp, String uuid, String tenantId)
            throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();
        LOGGER.info("Enter query Site2DC method. Begin time = " + infterEnterTime);

        SiteToDc oSite2Dc = new SiteToDc("");

        // 1. Query the Overlay VPN from database - Query common information
        ResultRsp<OverlayVpn> overlayVpnRsp = overlayVpnSvc.query(req, resp, uuid, tenantId);
        OverlayVpn oSite2DcVpn = overlayVpnRsp.getData();
        if(null == oSite2DcVpn) {
            ThrowOverlayVpnExcpt.throwResNotExistAsNotFound("Overlay VPN", uuid);
        }

        oSite2Dc.setUuid(oSite2DcVpn.getUuid());
        oSite2Dc.setName(oSite2DcVpn.getName());

        // 2. Query Site information
        for(String strConnectionId : oSite2DcVpn.getConnectionIds()) {

            // 2.1. Validate the UUID
            CheckStrUtil.checkUuidStr(strConnectionId);

            // 2.2. Query the Connection from database
            ResultRsp<Connection> vpnConnRsp = connectionSvc.query(req, resp, strConnectionId, tenantId);
            Connection queryedVpnConn = vpnConnRsp.getData();
            if(null == queryedVpnConn) {
                continue;
            }

            // 2.3. Query EPG and gateway information
            for(String strEpgId : queryedVpnConn.getEpgIds()) {

                // 2.3.1 Query the endpoint group from database
                ResultRsp<EndpointGroup> endpointGrpRsp = endPointGrpSvc.query(req, resp, strEpgId, tenantId);
                EndpointGroup invEpg = endpointGrpRsp.getData();
                if(null == invEpg) {
                    continue;
                }

                // 2.3.3 Query Port/VLAN information from endpoint list
                if(null != invEpg.getGatewayId()) {
                    oSite2Dc.getSite().setSiteGatewayId(invEpg.getGatewayId());
                }

                if(null != invEpg.getType()) {
                    if(invEpg.getType().equals("cidr")) {
                        oSite2Dc.getSite().setSiteTypeAddress(invEpg.getCidr());
                        oSite2Dc.getSite().setPortAndVlan(invEpg.getEndpoints());
                        oSite2Dc.getSite().setSitevCPE(invEpg.getNeId());
                    }
                }

            }
        }

        LOGGER.info("Exit query Site2DC method. Cost time = " + (System.currentTimeMillis() - infterEnterTime));

        return new ResultRsp<SiteToDc>(ErrorCode.OVERLAYVPN_SUCCESS, oSite2Dc);
    }

    /**
     * Update Overlay VPN Name or Description<br/>
     *
     * @param httpContext - HTTP Context for security token
     * @param newReq - Site2DC information with update name or description
     * @param oldReq - Old Site2DC information
     * @return - Updated Site2DC information
     * @throws ServiceException - when updating the name or description in database fails
     * @since SDNO 0.5
     */
    @Override
    public ResultRsp<SiteToDc> update(HttpServletRequest req, HttpServletResponse resp, SiteToDc newReq,
            SiteToDc oldReq) throws ServiceException {

        String tenantIdFromToken = TokenDataHolder.getTenantID();

        // 1. Query old overlay VPN
        ResultRsp<OverlayVpn> overlayVpnRsp = overlayVpnSvc.query(req, resp, oldReq.getUuid(), tenantIdFromToken);
        OverlayVpn oldBasicCloudVpn = overlayVpnRsp.getData();
        if(null == oldBasicCloudVpn) {
            ThrowOverlayVpnExcpt.throwResNotExistAsNotFound("Overlay VPN", oldReq.getUuid());
        }

        // 2. Prepare new overlay VPN
        OverlayVpn newOverlayVpn = new OverlayVpn();
        newOverlayVpn.copyBasicData(oldBasicCloudVpn);

        // 3.1 Copy name to new overlay VPN
        String name = newReq.getName();
        if(StringUtils.hasLength(name)) {
            if(name.length() > CommConst.NAME_MAX_LENGTH) {
                ThrowOverlayVpnExcpt.throwParmaterInvalid("name", name);
            }
            newOverlayVpn.setName(name);
        }

        // 3.2 Copy description to new overlay VPN
        String description = newReq.getDescription();
        if(StringUtils.hasLength(description)) {
            if(description.length() > CommConst.DESCRIPTION_MAX_LENGTH) {
                ThrowOverlayVpnExcpt.throwParmaterInvalid("description", description);
            }
            newOverlayVpn.setDescription(description);
        }

        // TODO:?? Whether need admin status here

        // 4. Update overlay VPN to database
        ResultRsp<OverlayVpn> resultRsp = overlayVpnSvc.update(req, resp, newOverlayVpn, oldBasicCloudVpn);

        ThrowOverlayVpnExcpt.checkRspThrowException(resultRsp);

        return new ResultRsp<SiteToDc>(ErrorCode.OVERLAYVPN_SUCCESS, newReq);
    }

    /**
     * Undeploy and delete the Overlay VPN for Site to DC Scenario<br/>
     *
     * @param httpContext - HTTP Context to get the tenant id
     * @param siteToDc - Contains the Site and DC information
     * @return - Success or Failure Code
     * @throws ServiceException - when undeploy or deleting the VPN from database fails
     * @since SDNO 0.5
     */
    @Override
    public ResultRsp<String> delete(HttpServletRequest req, HttpServletResponse resp, SiteToDc siteToDc)
            throws ServiceException {

        long infterEnterTime = System.currentTimeMillis();
        LOGGER.info("Enter delete Site2DC method. Begin time = " + infterEnterTime);

        // 1. UnDeploy the VXLAN service on adapter/controller and update the VPN Status to
        // inactive.It also gets all the connection IDs for the overlayVPN from database.
        ResultRsp<OverlayVpn> oOverlayVpn = siteToDCOverlayVPN.updateVpnStatus(req, resp, siteToDc);
        if(null == oOverlayVpn.getData()) {
            ThrowOverlayVpnExcpt.throwResNotExistAsNotFound("Overlay VPN", siteToDc.getUuid());
        }

        // 2. Delete all EPGIDs in the connection and then delete connection
        for(String strConnectionId : oOverlayVpn.getData().getConnectionIds()) {

            // 2.1. Get Tenant ID
            String tenantId = TokenDataHolder.getTenantID();

            // 2.2. Validate the UUID
            CheckStrUtil.checkUuidStr(strConnectionId);

            // 2.3. Query the EPG ids from database by connection ID filter
            ResultRsp<Connection> vpnConnRsp = connectionSvc.query(req, resp, strConnectionId, tenantId);
            Connection queryedVpnConn = vpnConnRsp.getData();
            if(null == queryedVpnConn) {
                continue;
            }

            // 2.4. Delete the EPG ids from database and adapter database
            for(String strEpgId : queryedVpnConn.getEpgIds()) {
                siteToDCOverlayVPN.deleteEpg(req, resp, strEpgId);
            }

            // 2.5 Delete the connection from database
            siteToDCOverlayVPN.deleteConnection(req, resp, strConnectionId);

        }

        // 3. Delete OverlayVPN from database
        ResultRsp<String> resultRsp = siteToDCOverlayVPN.deleteOverlayVpn(req, resp, siteToDc.getUuid());

        LOGGER.info("Exit delete Site2DC method. cost time = " + (System.currentTimeMillis() - infterEnterTime));

        return resultRsp;
    }

    public void setOverlayVpnSvc(OverlayVpnSvcImpl overlayVpnSvc) {
        this.overlayVpnSvc = overlayVpnSvc;
    }

    public void setConnectionSvc(ConnectionSvcImpl connectionSvc) {
        this.connectionSvc = connectionSvc;
    }

    public void setEndPointGrpSvc(EndPointGrpSvcImpl endPointGrpSvc) {
        this.endPointGrpSvc = endPointGrpSvc;
    }

    public void setSiteToDCOverlayVPN(SiteToDCOverlayVPN siteToDCOverlayVPN) {
        this.siteToDCOverlayVPN = siteToDCOverlayVPN;
    }
}
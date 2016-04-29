/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.pc.analytics.config;

/**
 * Configure DAS configurations to publish data to DAS an do the analytics
 * (initiator class in the module)
 */

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.pc.analytics.config.clients.LoginAdminServiceClient;
import org.wso2.carbon.pc.analytics.config.clients.ReceiverAdminServiceClient;
import org.wso2.carbon.pc.analytics.config.clients.StreamAdminServiceClient;
import org.wso2.carbon.pc.analytics.config.utils.DASConfigurationUtils;
import org.wso2.carbon.pc.core.ProcessCenterException;
import org.wso2.carbon.registry.core.utils.RegistryUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Arrays;

public class DASConfigClient {

    String streamName;
    String stremaVersion;
    String streamId;
    String streamDescription;
    String streamNickName;
    String receiverName;
    JSONArray processVariablesJObArr;
    String DASUrl = null;
    private static final Log log = LogFactory.getLog(DASConfigClient.class);

    /**
     * Configure WSO2 DAS for analytics, by creating an Event Stream, Event Receiver for each process.
     * In the event stream a field for processInstanceId too would be set.
     *
     * @param DASconfigDetails Data given by the user for the configurations
     * @throws ProcessCenterException
     * @throws RemoteException
     * @throws LogoutAuthenticationExceptionException
     */
    public void configDAS(String DASconfigDetails, String processName, String processVersion)
            throws ProcessCenterException, RemoteException, LogoutAuthenticationExceptionException {

        JSONObject processInfo = null;
        LoginAdminServiceClient loginServiceClient = null;
        String dasUsername = null;
        //String dasPassword=null;
        char[] dasPassword = null;

        try {
            dasUsername = DASConfigurationUtils.getDASUserName();
            //dasPassword=DASConfigurationUtils.getDASPassword();
            dasPassword = DASConfigurationUtils.getDASPassword().toCharArray();
            DASUrl = DASConfigurationUtils.getDASURL();
            processInfo = new JSONObject(DASconfigDetails);
            streamName = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_NAME);
            stremaVersion = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_VERSION);
            streamId = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_ID);
            streamDescription = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_DESCRIPTION);
            streamNickName = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_NICK_NAME);
            receiverName = processInfo.getString(AnalyticsConfigConstants.EVENT_RECEIVER_NAME);
            processVariablesJObArr = processInfo.getJSONArray(AnalyticsConfigConstants.PROCESS_VARIABLES);

            //login to DAS
            loginServiceClient = new LoginAdminServiceClient(DASUrl);
            String session = loginServiceClient.authenticate(dasUsername, dasPassword);

            //remove the password from memory
            Arrays.fill(dasPassword, ' ');
            dasPassword = null;

            //create event stream
            StreamAdminServiceClient streamAdminServiceClient = new StreamAdminServiceClient(DASUrl, session,
                    streamName, stremaVersion, streamId, streamNickName, streamDescription, processVariablesJObArr);
            streamAdminServiceClient.createEventStream();
            log.info("Created the Event Stream: " + streamId + " in WSO2 DAS on :" + DASUrl);

            //create event receiver
            ReceiverAdminServiceClient receiverAdminServiceClient = new ReceiverAdminServiceClient(DASUrl, session,
                    receiverName, streamId, AnalyticsConfigConstants.WSO2_EVENT);
            receiverAdminServiceClient.deployEventReceiverConfiguration();
            log.info("Created the Event Receiver: " + receiverName + "for the " + streamId + " in WSO2 DAS");

            //now send the REST Post call to the WSO2 BPS to communicate the analytics configuration details to the BPS from PC
            BPSConfigRestClient.post(DASconfigDetails, processName, processVersion);

            //logging out from DAS Admin Services
            loginServiceClient.logOut();

        } catch (LoginAuthenticationExceptionException e) {
            String errMsg = "Error in Login to DAS at :" + DASUrl + "trying to login with username:" + dasUsername;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg, e);
        } catch (LogoutAuthenticationExceptionException e) {
            String errMsg = "Error in Logout from DAS at :" + DASUrl;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg, e);
        } catch (AxisFault | JSONException | XMLStreamException e) {
            String errMsg = "Error in DAS configuration, using :" + DASconfigDetails;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg, e);
        } catch (RemoteException e) {
            String errMsg = "Error in DAS configuration, using :" + DASconfigDetails;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg, e);
        } catch (IOException e) {
            String errMsg = "Error in DAS configuration, using :" + DASconfigDetails;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg, e);
        } catch (ProcessCenterException e) {
            log.error(e.getMessage(), e);
            throw new ProcessCenterException(e.getMessage(), e);
        } finally {
            loginServiceClient.logOut();
        }
    }
}
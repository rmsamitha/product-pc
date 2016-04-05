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

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;

public class DASConfigClient {

    String streamName;
    String stremaVersion;
    String streamId;
    String streamDescription;
    String streamNickName;
    String receiverName;
    JSONArray processVariablesJObArr;
    String backEndUrl = null;
    private static final Log log = LogFactory.getLog(DASConfigClient.class);

    public void configDAS(String DASconfigDetails)
            throws ProcessCenterException, RemoteException, LogoutAuthenticationExceptionException {

        JSONObject processInfo = null;
        LoginAdminServiceClient login = null;
        String dasUsername="admin";
        String dasPassword="admin";
        try {
            backEndUrl = DASConfigurationUtils.getURL();


            processInfo = new JSONObject(DASconfigDetails);
            streamName = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_NAME);
            stremaVersion = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_VERSION);
            streamId = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_ID);
            streamDescription = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_DESCRIPTION);
            streamNickName = processInfo.getString(AnalyticsConfigConstants.EVENT_STREAM_NICK_NAME);
            receiverName = processInfo.getString(AnalyticsConfigConstants.EVENT_RECEIVER_NAME);
            processVariablesJObArr = processInfo.getJSONArray(AnalyticsConfigConstants.PROCESS_VARIABLES);

            System.setProperty("javax.net.ssl.trustStore",
                    "/home/samithac/wso2-products/wso2das-3.0.1/repository/resources/security/wso2carbon.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");

            //login to DAS
            login = new LoginAdminServiceClient(backEndUrl);
            String session = login.authenticate(dasUsername, dasPassword);


            //create event stream
            StreamAdminServiceClient streamAdminServiceClient = new StreamAdminServiceClient(backEndUrl, session, streamName, stremaVersion,
                    streamId, streamNickName, streamDescription, processVariablesJObArr);
            streamAdminServiceClient.createEventStream();
            log.info("Created the Event Stream: " + streamId + " in WSO2 DAS on :"+backEndUrl);

            //create event receiver
            ReceiverAdminServiceClient receiverAdminServiceClient = new ReceiverAdminServiceClient(backEndUrl, session, receiverName, streamId,
                    AnalyticsConfigConstants.WSO2_EVENT);
            receiverAdminServiceClient.deployEventReceiverConfiguration();
            log.info("Created the Event Receiver: " + receiverName + "for the " + streamId + " in WSO2 DAS");

            //logging out from DAS Admin Services
            login.logOut();

        }  catch(LoginAuthenticationExceptionException e){
            String errMsg="Error in Login to DAS at :"+backEndUrl+ "trying to login with username:"+dasUsername+ "and password :"+dasPassword;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg,e);
        } catch(LogoutAuthenticationExceptionException e){
            String errMsg="Error in Logout from DAS at :"+backEndUrl;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg,e);
        } catch (AxisFault |JSONException |XMLStreamException  e) {
            String errMsg="Error in DAS configuration, using :"+DASconfigDetails;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg,e);
        } catch (RemoteException  e) {
            String errMsg="Error in DAS configuration, using :"+DASconfigDetails;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg,e);
        } catch (IOException e) {
            String errMsg="Error in DAS configuration, using :"+DASconfigDetails;
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg,e);
        }catch(ProcessCenterException e){
            log.error(e.getMessage(), e);
            throw new ProcessCenterException(e.getMessage(),e);
        }finally {
            login.logOut();
        }
    }
}
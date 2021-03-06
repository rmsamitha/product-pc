/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.wso2.pc.integration.tests.publisher.processes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.pc.integration.test.utils.base.PCIntegrationBaseTest;
import org.wso2.pc.integration.test.utils.base.PCIntegrationConstants;
import org.wso2.pc.integration.test.utils.base.TestUtils;
import org.wso2.pc.integration.test.utils.base.GenericRestClient;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the login functionality of publisher
 */
public class LoginTestCase extends PCIntegrationBaseTest {

    private static final Log log = LogFactory.getLog(LoginTestCase.class);
    private TestUserMode userMode;
    String jSessionId;
    GenericRestClient genericRestClient;
    Map<String, String> headerMap;
    Map<String, String> queryMap;
    String publisherUrl;
    private String publisherUrlForVersion;

    @Factory(dataProvider = "userModeProvider")
    public LoginTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        genericRestClient = new GenericRestClient();
        headerMap = new HashMap<>();
        queryMap = new HashMap<>();
        publisherUrl = automationContext.getContextUrls().getSecureServiceUrl().replace("services",
                PCIntegrationConstants.DESIGNER_APIS);
        publisherUrlForVersion = automationContext.getContextUrls().getSecureServiceUrl().
                replace("services", PCIntegrationConstants.DESIGNER_ASSETS);
    }

    @Test(groups = {"wso2.pc"}, description = "Login to publisher")
    public void loginPublisher() throws JSONException, XPathExpressionException {
        JSONObject objSessionPublisher =
                new JSONObject(TestUtils.authenticate(
                        publisherUrl, genericRestClient,
                        automationContext.getSuperTenant().getTenantAdmin().getUserName(),
                        automationContext.getSuperTenant().getTenantAdmin().getPassword(),
                        queryMap,
                        headerMap).getEntity(String.class));
        jSessionId = objSessionPublisher.getJSONObject("data").getString("sessionId");
        Assert.assertNotNull(jSessionId, "Invalid JSessionID received.Cannot login.");
    }

    @DataProvider
    private static Object[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN}
//                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }
}

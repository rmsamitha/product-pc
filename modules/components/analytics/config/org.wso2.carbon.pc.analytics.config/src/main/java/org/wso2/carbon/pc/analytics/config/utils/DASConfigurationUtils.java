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

package org.wso2.carbon.pc.analytics.config.utils;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.pc.analytics.config.AnalyticsConfigConstants;
import org.wso2.carbon.pc.core.ProcessCenterException;
import org.wso2.carbon.pc.core.ProcessStoreConstants;
import org.wso2.carbon.pc.core.internal.ProcessCenterServerHolder;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.wso2.carbon.pc.core.ProcessStore;

/**
 * Utils file for Analytics configuration related properties
 */
public class DASConfigurationUtils {
    private static final Log log = LogFactory.getLog(DASConfigurationUtils.class);

    /*public static void setPropertyDASAnalyticsConfigured(String processName, String processVersion) {
        PrivilegedCarbonContext context = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        Registry registry = context.getRegistry(RegistryType.SYSTEM_GOVERNANCE);
        Resource resource;
        String processAssetPath = "processes/" + processName + "/" + processVersion;

        try {
            if (registry.resourceExists(processAssetPath)) {
                resource = registry.get(processAssetPath);
                if (resource.getProperty("isDASConfiguredForAnalytics") == null) {
                    resource.addProperty("isDASConfiguredForAnalytics", "true");
                    registry.put(processAssetPath, resource);
                }
            }
        } catch (RegistryException e) {
            log.error("Error working with SYSTEM_GOVERNANCE registry property -isDASConfiguredForAnalytics");
        }
    }*/

    public static boolean isDASAnalyticsConfigured(String processName, String processVersion) {
        PrivilegedCarbonContext context = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        Registry registry = context.getRegistry(RegistryType.SYSTEM_GOVERNANCE);
        Resource resource;
        String processAssetPath = "processes/" + processName + "/" + processVersion;

        try {
            if (registry.resourceExists(processAssetPath)) {
                resource = registry.get(processAssetPath);
                return Boolean.parseBoolean(resource.getProperty("isDASConfiguredForAnalytics"));
            } else {
                return false;
            }
        } catch (RegistryException e) {
            log.error("Error in getting SYSTEM_GOVERNANCE registry property- isDASConfiguredForAnalytics ");
        }
        return true;
    }

    public static void setPropertyDASAnalyticsConfigured(String processName, String processVersion) throws ProcessCenterException {
        String processContent=null;
        ProcessStore ps=new ProcessStore();
        try {
            RegistryService registryService = ProcessCenterServerHolder.getInstance().getRegistryService();

            if (registryService != null) {
                UserRegistry reg = registryService.getGovernanceSystemRegistry();

                //JSONObject processInfo = new JSONObject(processVariableDetails);
                String processAssetPath = ProcessStoreConstants.PROCESS_ASSET_ROOT +processName + "/" +
                        processVersion;
                org.wso2.carbon.registry.core.Resource resource = reg.get(processAssetPath);
                processContent = new String((byte[]) resource.getContent());
                Document doc = ps.stringToXML(processContent);

                //JSONObject processVariablesJOb = processInfo.getJSONObject("processVariables");

                //Iterator<?> keys = processVariablesJOb.keys();
                //saving pracess variable name,type as sub elements
                //while (keys.hasNext()) {
                    //String variableName = (String) keys.next();
                    //if (Debugger.isEnabled())
                    //log.debug(variableName);
                    //String variableType = processVariablesJOb.get(variableName).toString();
                    //JSONObject processVariableJOb= (JSONObject) processVariablesArray.get(i);
                    Element rootElement = doc.getDocumentElement();
                    Element propertiesElement = ps.append(doc, rootElement, "properties", AnalyticsConfigConstants.METADATA_NAMESPACE);
                    ps.appendText(doc, propertiesElement, "isDASAnalyticsConfigured", AnalyticsConfigConstants.METADATA_NAMESPACE, "true");

                    String newProcessContent = ps.xmlToString(doc);
                    resource.setContent(newProcessContent);
                    reg.put(processAssetPath, resource);
                //}
                if(log.isDebugEnabled()){
                    log.debug("Successfully set the property isDASAnalyticsConfigured true.");
                }
            }
        } catch (TransformerException | JSONException | org.wso2.carbon.registry.core.exceptions.RegistryException e) {
            /* add this again and resolve compile errors later
            String errMsg = "Failed to save processVariables with info,\n"+processVariableDetails+"\n,to the process.rxt";
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg,e);*/
        } catch (Exception e) {
            String errMsg="Failed to convert "+processContent+ " registry resource to XML";
            log.error(errMsg, e);
            throw new ProcessCenterException(errMsg,e);
        }
    }

    /**
     * Get content of pc.xml file as a string
     *
     * @return the content of pc.xml file as a String
     * @throws IOException
     * @throws XMLStreamException
     */
    private static OMElement getConfigElement() throws IOException, XMLStreamException {
        String carbonConfigDirPath = CarbonUtils.getCarbonConfigDirPath();
        String pcConfigPath = carbonConfigDirPath + File.separator +
                AnalyticsConfigConstants.PC_CONFIGURATION_FILE_NAME;
        File configFile = new File(pcConfigPath);
        String configContent = FileUtils.readFileToString(configFile);
        return AXIOMUtil.stringToOM(configContent);
    }

    public static String getURL() throws IOException, XMLStreamException {
        OMElement configElement = getConfigElement();
        OMElement analyticsElement = configElement.getFirstChildWithName(new QName(AnalyticsConfigConstants.ANALYTICS));
        if (analyticsElement != null) {
            String baseUrl = analyticsElement.getFirstChildWithName(new QName(AnalyticsConfigConstants.CONFIG_BASE_URL))
                    .getText();
            if (baseUrl != null && !baseUrl.isEmpty()) {
                if (baseUrl.endsWith(File.separator)) {
                    //baseUrl += File.separator;
                    return baseUrl.substring(0, baseUrl.length() - 1);
                }
                return baseUrl;
            }
        }
        return null;
    }

    public static String getAuthorizationHeader() throws IOException, XMLStreamException {
        String requestHeader = "Basic ";
        OMElement configElement = getConfigElement();
        SecretResolver secretResolver = SecretResolverFactory.create(configElement, false);
        OMElement analyticsElement = configElement.getFirstChildWithName(new QName(AnalyticsConfigConstants.ANALYTICS));

        String userName = null;
        String password = null;
        if (analyticsElement != null) {
            userName = analyticsElement.getFirstChildWithName(new QName(AnalyticsConfigConstants.CONFIG_USER_NAME))
                    .getText();
            if (secretResolver != null && secretResolver.isInitialized()) {
                if (secretResolver.isTokenProtected(AnalyticsConfigConstants.SECRET_ALIAS)) {
                    password = secretResolver.resolve(AnalyticsConfigConstants.SECRET_ALIAS);
                } else {
                    password = analyticsElement
                            .getFirstChildWithName(new QName(AnalyticsConfigConstants.CONFIG_PASSWORD)).getText();
                }
            } else {
                password = analyticsElement.getFirstChildWithName(new QName(AnalyticsConfigConstants.CONFIG_PASSWORD))
                        .getText();
            }
        }
        if (userName != null && password != null) {
            String headerPortion = userName + ":" + password;
            byte[] encodedBytes = headerPortion.getBytes("UTF-8");
            String encodedString = DatatypeConverter.printBase64Binary(encodedBytes);
            requestHeader += encodedString;
            return requestHeader;
        }
        return null;
    }
}
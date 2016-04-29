package org.wso2.carbon.pc.analytics.config;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.apache.openjpa.persistence.jest.JSONObject;
import org.json.JSONException;
import org.wso2.carbon.pc.analytics.config.utils.DASConfigurationUtils;
import org.wso2.carbon.registry.core.utils.RegistryUtils;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by samithac on 24/3/16.
 */
public class BPSConfigRestClient {
    private static final Log log = LogFactory.getLog(BPSConfigRestClient.class);

    /**
     * Send post request to a BPS BPMN rest web service
     *
     * @param message        is the request message that need to be sent to the web service
     * @param processName
     * @param processVersion
     * @return the result as a String
     */
    public static void post(String message, String processName, String processVersion)
            throws IOException, XMLStreamException {

        String bpsurl = DASConfigurationUtils.getBPSURL();
        RegistryUtils.setTrustStoreSystemProperties();
        HttpClient httpClient = new HttpClient();
        String requestUrl = bpsurl + AnalyticsConfigConstants.BPS_PROCESS_VAR_PUBLISH_REST_PATH + processName + "_" + processVersion;

        PostMethod postRequest = new PostMethod(requestUrl);
        postRequest.setRequestHeader("Authorization", DASConfigurationUtils.getAuthorizationHeader());
        StringRequestEntity input = new StringRequestEntity(message, "application/json", "UTF-8");
        postRequest.setRequestEntity(input);

        int returnCode = httpClient.executeMethod(postRequest);

        //deal with the response
        if (returnCode != HttpStatus.SC_OK) {
            String errorCode =
                    "Failed : Sending the REST Post call to the WSO2 BPS to communicate the analytics configuration details to the BPS from PC\n: HTTP error code : " + returnCode;
            throw new RuntimeException(errorCode);
        }

        InputStreamReader reader = new InputStreamReader((postRequest.getResponseBodyAsStream()));
        BufferedReader br = new BufferedReader(reader);

        String output = null;
        StringBuilder totalOutput = new StringBuilder();

        if (log.isDebugEnabled()) {
            log.debug("Output from Server .... \n");
        }

        while ((output = br.readLine()) != null){
            totalOutput.append(output);
        }

        if(output.equals("Success")){
            log.info("BPS was acknowleged the Analytics Configuration details");
        }

        postRequest.releaseConnection();
        if (br != null) {
            try {
                br.close();
            } catch (Exception e) {
                String errMsg = "BPS Config Rest client BufferedReader close exception.";
                log.error(errMsg, e);
            }
        }
    }
}

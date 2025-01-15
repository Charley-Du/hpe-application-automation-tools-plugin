/*
 * Certain versions of software accessible here may contain branding from Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.
 * This software was acquired by Micro Focus on September 1, 2017, and is now offered by OpenText.
 * Any reference to the HP and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * Copyright 2012-2024 Open Text
 *
 * The only warranties for products and services of Open Text and
 * its affiliates and licensors ("Open Text") are as may be set forth
 * in the express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or
 * omissions contained herein. The information contained herein is subject
 * to change without notice.
 *
 * Except as specifically indicated otherwise, this document contains
 * confidential information and a valid license is required for possession,
 * use or copying. If this work is provided to the U.S. Government,
 * consistent with FAR 12.211 and 12.212, Commercial Computer Software,
 * Computer Software Documentation, and Technical Data for Commercial Items are
 * licensed to the U.S. Government under vendor's standard commercial license.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ___________________________________________________________________
 */

package com.microfocus.application.automation.tools.commonResultUpload.rest;

import com.microfocus.adm.performancecenter.plugins.common.rest.RESTConstants;
import com.microfocus.application.automation.tools.commonResultUpload.CommonUploadLogger;
import com.microfocus.application.automation.tools.rest.RestClient;
import com.microfocus.application.automation.tools.sse.common.RestXmlUtils;
import com.microfocus.application.automation.tools.sse.sdk.ResourceAccessLevel;
import com.microfocus.application.automation.tools.sse.sdk.Response;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Map;

public class CreateAlmEntityEntityRequest extends BasicPostEntityRequest {

    public CreateAlmEntityEntityRequest(RestClient client, CommonUploadLogger logger) {
        super(client, logger, "Create");
    }

    @Override
    public Map<String, String> perform(String restPrefix, Map<String, String> valueMap) {
        String url = client.buildRestRequest(restPrefix);
        Response response = client.httpPost(
                url,
                getDataBytes(valueMap),
                getHeaders(),
                ResourceAccessLevel.PROTECTED);
        return handleResult(response, valueMap, restPrefix);
    }
    @Override
    public Map<String, Map<String, String>> bulkCreate(String restPrefix, Map<String, Map<String, String>> valueMap) {
        String url = client.buildRestRequest(restPrefix);

        Map<String,String> headers = getHeaders();
        headers.put(RESTConstants.CONTENT_TYPE,RESTConstants.APP_XML_BULK);

        Response response = client.httpPost(
                url,
                getBulkDataBytes(valueMap),
                headers,
                ResourceAccessLevel.PROTECTED);

        return handleBulkResult(response, valueMap, restPrefix);
    }

    private byte[] getBulkDataBytes(Map<String, Map<String, String>> valueMap) {
        StringBuilder builder = new StringBuilder("<Entities>");
        for (Map.Entry<String, Map<String, String>> entry : valueMap.entrySet()) {
            StringBuilder entityBuilder = new StringBuilder("<Entity><Fields>");
            for (Map.Entry<String, String> entity : entry.getValue().entrySet()) {
                entityBuilder.append(RestXmlUtils.fieldXml(entity.getKey(), StringEscapeUtils.escapeXml10(entity.getValue())));
            }
            entityBuilder.append("</Fields></Entity>");
            builder.append(entityBuilder.toString());
        }
        builder.append("</Entities>");
        logger.info("Request body: " + builder.toString());
        return builder.toString().getBytes();
    }

    private Map<String, Map<String, String>> handleBulkResult(Response response, Map<String, Map<String, String>> valueMap, String restPrefix) {
        return null;
    }
}

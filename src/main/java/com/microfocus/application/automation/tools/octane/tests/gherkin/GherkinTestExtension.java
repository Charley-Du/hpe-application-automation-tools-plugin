/*
 *  Certain versions of software accessible here may contain branding from
 *  Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.
 *  This software was acquired by Micro Focus on September 1, 2017, and is now
 *  offered by OpenText.
 *  Any reference to the HP and Hewlett Packard Enterprise/HPE marks is historical
 *  in nature, and the HP and Hewlett Packard Enterprise/HPE marks are the
 *  property of their respective owners.
 *  OpenText is a trademark of Open Text.
 *  __________________________________________________________________
 *  MIT License
 *
 *  Copyright 2012-2025 Open Text.
 *
 *  The only warranties for products and services of Open Text and
 *  its affiliates and licensors ("Open Text") are as may be set forth
 *  in the express warranty statements accompanying such products and services.
 *  Nothing herein should be construed as constituting an additional warranty.
 *  Open Text shall not be liable for technical or editorial errors or
 *  omissions contained herein. The information contained herein is subject
 *  to change without notice.
 *
 *  Except as specifically indicated otherwise, this document contains
 *  confidential information and a valid license is required for possession,
 *  use or copying. If this work is provided to the U.S. Government,
 *  consistent with FAR 12.211 and 12.212, Commercial Computer Software,
 *  Computer Software Documentation, and Technical Data for Commercial Items are
 *  licensed to the U.S. Government under vendor's standard commercial license.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ___________________________________________________________________
 */
package com.microfocus.application.automation.tools.octane.tests.gherkin;

import com.hp.octane.integrations.testresults.GherkinUtils;
import com.hp.octane.integrations.testresults.XmlWritableTestResult;
import com.microfocus.application.automation.tools.octane.actions.cucumber.CucumberResultsService;
import com.microfocus.application.automation.tools.octane.actions.cucumber.CucumberTestResultsAction;
import com.microfocus.application.automation.tools.octane.configuration.SDKBasedLoggerProvider;
import com.microfocus.application.automation.tools.octane.tests.OctaneTestsExtension;
import com.microfocus.application.automation.tools.octane.tests.TestProcessingException;
import com.microfocus.application.automation.tools.octane.tests.TestResultContainer;
import hudson.Extension;
import hudson.model.Run;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Extension
public class GherkinTestExtension extends OctaneTestsExtension {
	private static Logger logger = SDKBasedLoggerProvider.getLogger(GherkinTestExtension.class);

	@Override
	public boolean supports(Run<?, ?> build) {
		if (build.getAction(CucumberTestResultsAction.class) != null) {
			logger.debug("CucumberTestResultsAction found, gherkin results expected");
			return true;
		} else {
			logger.debug("CucumberTestResultsAction not found, no gherkin results expected");
			return false;
		}
	}

	@Override
	public TestResultContainer getTestResults(Run<?, ?> build, String jenkinsRootUrl) throws
			TestProcessingException, IOException, InterruptedException {
		try {
			List<File> gherkinFiles = GherkinUtils.findGherkinFilesByTemplateWithCounter(build.getRootDir().getAbsolutePath(),
					CucumberResultsService.GHERKIN_NGA_RESULTS + "%s.xml", 0);
			List<XmlWritableTestResult> testResults = GherkinUtils.parseFiles(gherkinFiles);
			return new TestResultContainer(testResults.iterator(), null);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new TestProcessingException("Error while processing gherkin test results", e);
		}
	}

	private List<File> findGherkinFiles(File buildDir){
		List<File> result = new ArrayList<>();
		int i = 0;
		File gherkinTestResultsFile = new File(buildDir, CucumberResultsService.getGherkinResultFileName(i));

		while (gherkinTestResultsFile.exists()) {
			result.add(gherkinTestResultsFile);
			i++;
			gherkinTestResultsFile = new File(buildDir, CucumberResultsService.getGherkinResultFileName(i));
		}

		return result;
	}
}

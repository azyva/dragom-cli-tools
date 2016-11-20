/*
 * Copyright 2015 - 2017 AZYVA INC. INC.
 *
 * This file is part of Dragom.
 *
 * Dragom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Dragom.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.azyva.dragom.test.integration.setupjenkinsjobs;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.model.ArtifactGroupId;
import org.azyva.dragom.model.Model;
import org.azyva.dragom.model.Module;
import org.azyva.dragom.model.ModuleVersion;
import org.azyva.dragom.model.Version;
import org.azyva.dragom.model.plugin.ArtifactInfoPlugin;
import org.azyva.dragom.model.plugin.JenkinsJobInfoPlugin;
import org.azyva.dragom.model.plugin.ScmPlugin;
import org.azyva.dragom.model.plugin.impl.SimpleJenkinsJobInfoPluginBaseImpl;
import org.azyva.dragom.reference.ReferenceGraph;

public class TestJenkinsJobInfoPluginImpl extends SimpleJenkinsJobInfoPluginBaseImpl implements JenkinsJobInfoPlugin {

	public TestJenkinsJobInfoPluginImpl(Module module) {
		super(module);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTemplate() {
		return "build/ci/template-build-job";
	}

	@Override
	public Map<String, String> getMapTemplateParam(ReferenceGraph referenceGraph, Version version) {
		Model model;
		Map<String, String> mapTemplateParam;
		ScmPlugin scmPlugin;
		ArtifactInfoPlugin artifactInfoPlugin;
		ArtifactGroupId artifactGroupId;
		List<ReferenceGraph.Referrer> listReferrer;
		StringBuilder stringBuilder;

		model = ExecContextHolder.get().getModel();
		mapTemplateParam = new HashMap<String, String>();

		scmPlugin = this.getModule().getNodePlugin(ScmPlugin.class, null);

		artifactInfoPlugin = this.getModule().getNodePlugin(ArtifactInfoPlugin.class, null);
		artifactGroupId = artifactInfoPlugin.getSetDefiniteArtifactGroupIdProduced().iterator().next();

		mapTemplateParam.put("URL_GIT_REPOS", scmPlugin.getScmUrl(null));
		mapTemplateParam.put("BRANCH", version.getVersion());
		mapTemplateParam.put("GROUP_ID", artifactGroupId.getGroupId());
		mapTemplateParam.put("ARTIFACT_ID", artifactGroupId.getArtifactId());

		listReferrer = referenceGraph.getListReferrer(new ModuleVersion(this.getModule().getNodePath(), version));
		stringBuilder = new StringBuilder();

		for (ReferenceGraph.Referrer referrer: listReferrer) {
			Module module;
			JenkinsJobInfoPlugin jenkinsJobInfoPlugin;

			module = model.getModule(referrer.getModuleVersion().getNodePath());
			jenkinsJobInfoPlugin = module.getNodePlugin(JenkinsJobInfoPlugin.class, null);

			if (stringBuilder.length() != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(jenkinsJobInfoPlugin.getJobFullName(referrer.getModuleVersion().getVersion()));
		}

		mapTemplateParam.put("DOWNSTREAM_JOBS", stringBuilder.toString());

		return mapTemplateParam;
	}

	@Override
	public Reader getReaderConfig(ReferenceGraph referenceGraph, Version version) {
		return null;
	}
}

/*
Take information from dragom.properties file in module (jdk, maven version, etc.)
*/
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.azyva.dragom.jenkins.JenkinsClient;

/**
 * Implementation of {@link JenkinsClient} for testing purposes.
 *
 * @author David Raymond
 */
public class JenkinsClientTestDouble implements JenkinsClient {
	/**
	 * User.
	 */
	private String user;

	/**
	 * Password.
	 */
	private String password;

	/**
	 * Set of folders.
	 */
	private static Set<String> setFolder = new HashSet<String>();

	/**
	 * Set of jobs.
	 */
	private static Set<String> setJob = new HashSet<String>();

	/**
	 * Constructor.
	 */
	public JenkinsClientTestDouble() {
	}

	@Override
	public void setBaseUrl(String baseUrl) {
		System.out.println("JenkinsClientTestDouble.setBaseUrl(" + baseUrl + ") called.");
	}

	@Override
	public void setUser(String user) {
		System.out.println("JenkinsClientTestDouble.setUser(" + user + ") called.");
		this.user = user;
	}

	@Override
	public void setPassword(String password) {
		System.out.println("JenkinsClientTestDouble.setPassword(" + password + ") called.");
		this.password = password;
	}

	@Override
	public boolean validateCredentials() {
		return this.user.equals("correct-user") && this.password.equals("correct-password");
	}

	@Override
	public ItemType getItemType(String item) {
		if (JenkinsClientTestDouble.setFolder.contains(item)) {
			return ItemType.FOLDER;
		} else if (JenkinsClientTestDouble.setJob.contains(item)) {
			return ItemType.NOT_FOLDER;
		} else {
			return null;
		}
	}

	@Override
	public boolean deleteItem(String item) {
		ItemType itemType;

		itemType = this.getItemType(item);

		if (itemType == null) {
			return false;
		}

		switch (this.getItemType(item)) {
		case FOLDER:
			Iterator<String> iteratorJob;

			JenkinsClientTestDouble.setFolder.remove(item);

			iteratorJob = JenkinsClientTestDouble.setJob.iterator();

			while (iteratorJob.hasNext()) {
				String folder;
				String job;

				folder = item + '/';
				job = iteratorJob.next();

				if (job.startsWith(folder)) {
					iteratorJob.remove();
				}
			}
			return true;

		case NOT_FOLDER:
			JenkinsClientTestDouble.setJob.remove(item);
			return true;

		default:
			throw new RuntimeException("Should not get here.");
		}
	}

	@Override
	public void createUpdateJobFromTemplate(String template, String job, Map<String, String> mapTemplateParam) {
		ItemType itemType;

		System.out.println("JenkinsClientTestDouble.createUpdateJobFromTemplate(" + template + ", " + job + ", ...) called with the following template parameters");

		for (Map.Entry<String, String> mapEntry: mapTemplateParam.entrySet()) {
			System.out.println("Key: " + mapEntry.getKey() + "  Value: " + mapEntry.getValue());
		}

		this.validateParentFolderExists(job);

		itemType = this.getItemType(job);

		if (itemType == null) {
			System.out.println("Job did not exist");
		} else if (itemType == ItemType.NOT_FOLDER) {
			System.out.println("Job already existed");
		} else {
			throw new RuntimeException("Job " + job + " is a folder.");
		}

		JenkinsClientTestDouble.setJob.add(job);
	}

	@Override
	public void createJob(String job, Reader readerConfig) {
		System.out.println("JenkinsClientTestDouble.createJob(" + job + ", <reader>)) called.");

		this.validateParentFolderExists(job);

		if (this.getItemType(job) != null) {
			throw new RuntimeException("Job " + job + " already exists.");
		}

		JenkinsClientTestDouble.setJob.add(job);
}

	@Override
	public void updateJob(String job, Reader readerConfig) {
		ItemType itemType;

		System.out.println("JenkinsClientTestDouble.updateJob(" + job + ", <reader>)) called.");

		this.validateParentFolderExists(job);

		itemType = this.getItemType(job);

		if (itemType == null) {
			throw new RuntimeException("Job " + job + " does not exist.");
		} else if (itemType == ItemType.FOLDER) {
			throw new RuntimeException("Job " + job + " is a folder.");
		}
	}

	@Override
	public void createUpdateJob(String job, Reader readerConfig) {
		ItemType itemType;

		System.out.println("JenkinsClientTestDouble.createUpdateJob(" + job + ", <reader>)) called.");

		this.validateParentFolderExists(job);

		itemType = this.getItemType(job);

		if (itemType == null) {
			System.out.println("Job did not exist.");
			JenkinsClientTestDouble.setJob.add(job);
		} else if (itemType == ItemType.NOT_FOLDER){
			System.out.println("Job already existed.");
		} else {
			throw new RuntimeException("Job " + job + " is a folder.");
		}
	}

	@Override
	public Build build(String job, Map<String, String> mapBuildParam) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isFolderEmpty(String folder) {
		if (this.getItemType(folder) != ItemType.FOLDER) {
			throw new RuntimeException("Folder " + folder + " does not exist.");
		}

		folder = folder + '/';

		for (String job: JenkinsClientTestDouble.setJob) {
			if (job.startsWith(folder)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean createSimpleFolder(String folder) {
		ItemType itemType;

		System.out.println("JenkinsClientTestDouble.createSimpleFolder(" + folder + ") called.");

		this.validateParentFolderExists(folder);

		itemType = this.getItemType(folder);

		if (itemType == null) {
			System.out.println("Folder did not exist.");
			JenkinsClientTestDouble.setFolder.add(folder);
			return true;
		} else if (itemType == ItemType.FOLDER){
			System.out.println("Folder already existed.");
			return false;
		} else {
			throw new RuntimeException("Folder " + folder + " is a job.");
		}
	}

	/**
	 * Validates that the parent folder of an item exists.
	 *
	 * @param item Item.
	 */
	private void validateParentFolderExists(String item) {
		int indexItemName;
		String folder;
		ItemType itemType;

		indexItemName = item.lastIndexOf('/');

		if (indexItemName == -1) {
			return;
		}

		folder = item.substring(0, indexItemName);

		itemType = this.getItemType(folder);

		if (itemType == null) {
			throw new RuntimeException("Parent folder " + folder + " does not exist.");
		} else if (itemType == ItemType.NOT_FOLDER) {
			throw new RuntimeException("Parent folder " + folder + " is a job.");
		}
	}

	/**
	 * Creates an initial folder.
	 *
	 * @param folder
	 */
	public static void createInitialFolder(String folder) {
		JenkinsClientTestDouble.setFolder.add(folder);
	}

	/**
	 * Creates an initial job.
	 * @param folder
	 */
	public static void createInitialJob(String job) {
		JenkinsClientTestDouble.setJob.add(job);
	}

	/**
	 * Prints folers and jobs.
	 */
	public static void printContents() {
		for (String folder: JenkinsClientTestDouble.setFolder) {
			System.out.println("Folder: " + folder);
		}
		for (String job: JenkinsClientTestDouble.setJob) {
			System.out.println("Job: " + job);
		}
	}
}

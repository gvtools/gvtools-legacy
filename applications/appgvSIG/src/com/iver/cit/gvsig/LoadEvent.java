package com.iver.cit.gvsig;

import com.iver.cit.gvsig.project.Project;

/**
 * @author fergonco
 */
public class LoadEvent {

	private Project project;

	public LoadEvent(Project proj) {
		this.project = proj;
	}

	public Project getProject() {
		return project;
	}

}

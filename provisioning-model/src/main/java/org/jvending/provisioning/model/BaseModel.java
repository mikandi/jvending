package org.jvending.provisioning.model;

import org.hibernate.Session;

public abstract class BaseModel {

	protected Session session;
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}
	
	public void close() {
		if(session != null) {
			session.close();
		}
	}
}

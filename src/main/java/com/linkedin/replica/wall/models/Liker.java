package com.linkedin.replica.wall.models;

public class Liker {
	private String likerId;
	private String likerName;
	private String likerProfilePictureUrl;

	public String getLikerId() {
		return likerId;
	}

	public String getLikerName() {
		return likerName;
	}

	public String getLikerProfilePictureUrl() {
		return likerProfilePictureUrl;
	}

	public void setLikerId(String likerId) {
		this.likerId = likerId;
	}

	public void setLikerName(String likerName) {
		this.likerName = likerName;
	}

	public void setLikerProfilePictureUrl(String likerProfilePictureUrl) {
		this.likerProfilePictureUrl = likerProfilePictureUrl;
	}

	public void set(String attributeName, Object val) {
		if(val == null)
			return;
		switch (attributeName) {
		case "likerId":
			setLikerId(val.toString());
			break;
		case "likerName":
			setLikerName(val.toString());
			break;
		case "likerProfilePictureUrl":
			setLikerProfilePictureUrl(val.toString());
			break;
		}
	}

	@Override
	public String toString() {
		return "liker [likerId=" + likerId + ", likerName=" + likerName + ", likerProfilePictureUrl="
				+ likerProfilePictureUrl + "]";
	}

}

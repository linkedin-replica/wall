package com.linkedin.replica.wall.models;

public class Author {
	private String authorName;
	private String authorProfilePictureUrl;

	public Author(String authorName, String authorProfilePictureUrl) {
		this.authorName = authorName;
		this.authorProfilePictureUrl = authorProfilePictureUrl;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorProfilePictureUrl() {
		return authorProfilePictureUrl;
	}

	public void setAuthorProfilePictureUrl(String authorProfilePictureUrl) {
		this.authorProfilePictureUrl = authorProfilePictureUrl;
	}

}

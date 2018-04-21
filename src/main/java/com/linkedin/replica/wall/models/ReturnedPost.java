package com.linkedin.replica.wall.models;

import java.util.ArrayList;

public class ReturnedPost {
	private String postId;
	private String authorId;
	private String text;
	private ArrayList<String> images;
	private ArrayList<String> videos;
	private int commentsCount;
	private long timestamp;
	private boolean isCompanyPost;

	private String authorName;
	private String authorProfilePictureUrl;
	private String headLine;
	private int likesCount;
	private boolean liked;

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<String> getImages() {
		return images;
	}

	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	public ArrayList<String> getVideos() {
		return videos;
	}

	public void setVideos(ArrayList<String> videos) {
		this.videos = videos;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isCompanyPost() {
		return isCompanyPost;
	}

	public void setCompanyPost(boolean isCompanyPost) {
		this.isCompanyPost = isCompanyPost;
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

	public String getHeadLine() {
		return headLine;
	}

	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public void set(String attributeName, Object val){
		switch(attributeName){
			case "postId" : setPostId(val.toString()); break;
			case "authorId" : setAuthorId(val.toString()); break;
			case "text" : setText(val.toString()); break;
			case "images" : setImages((ArrayList<String>) val); break;
			case "videos" :  setVideos((ArrayList<String>) val); break;
			case "commentsCount" : setCommentsCount(Integer.parseInt(val.toString())); break;
			case "timestamp" : setTimestamp(Long.parseLong(val.toString())); break;
			case "isCompanyPost" : setCompanyPost(Boolean.getBoolean(val.toString())); break;
			case "authorName" : setAuthorName(val.toString()); break;
			case "authorProfilePictureUrl" : setAuthorProfilePictureUrl(val.toString()); break;
			case "likesCount" : setLikesCount(Integer.parseInt(val.toString())); break;
			case "headLine" : setHeadLine(val.toString()); break;
			case "liked" : setLiked(Boolean.getBoolean(val.toString())); break;
		}
	}

	@Override
	public String toString() {
		return "ReturnedPost [postId=" + postId + ", authorId=" + authorId + ", text=" + text + ", images=" + images
				+ ", videos=" + videos + ", commentsCount=" + commentsCount + ", timestamp=" + timestamp
				+ ", isCompanyPost=" + isCompanyPost + ", authorName=" + authorName + ", authorProfilePictureUrl="
				+ authorProfilePictureUrl + ", headLine=" + headLine + ", likesCount=" + likesCount + ", liked="
				+ liked + "]";
	}
	
}

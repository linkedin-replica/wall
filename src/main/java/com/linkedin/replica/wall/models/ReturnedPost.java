package com.linkedin.replica.wall.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ReturnedPost implements Comparable<ReturnedPost> {
	private String postId;
	private String authorId;
	private String text;
	private String title;
	private ArrayList<String> images;
	private ArrayList<String> videos;
	private int commentsCount;
	private long timestamp;
	private boolean isCompanyPost;

	private String authorName;
	private String authorProfilePictureUrl;
	private String headline;
	private ArrayList<Liker> likers;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public ArrayList<Liker> getLikers() {
		return likers;
	}

	public void setLikers(Object val) {
		this.likers = new ArrayList<Liker>();
		ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String,Object>>) val;
		for(int i=0; i<list.size(); ++i){
			HashMap<String, Object> map = list.get(i);
			Iterator<String> iter = map.keySet().iterator();
			Liker liker = new Liker();
			likers.add(liker);
			while(iter.hasNext()){
				String key = iter.next();
				Object value = map.get(key);
				liker.set(key, value);
			}
		}
	}
	
	public void set(String attributeName, Object val){
		if(val == null)
			return;
		switch(attributeName){
			case "postId" : setPostId(val.toString()); break;
			case "authorId" : setAuthorId(val.toString()); break;
			case "text" : setText(val.toString()); break;
			case "title" : setTitle(val.toString()); break;
			case "images" : setImages((ArrayList<String>) val); break;
			case "videos" :  setVideos((ArrayList<String>) val); break;
			case "commentsCount" : setCommentsCount(((Number) val).intValue()); break;
			case "timestamp" : setTimestamp(((Number) val).longValue()); break;
			case "isCompanyPost" : setCompanyPost((Boolean) val); break;
			case "authorName" : setAuthorName(val.toString()); break;
			case "authorProfilePictureUrl" : setAuthorProfilePictureUrl(val.toString()); break;
			case "likers" : setLikers(val);; break;
			case "headline" : setHeadline(val.toString()); break;
			case "liked" : setLiked((Boolean) val); break;
		}
	}

		
	@Override
	public String toString() {
		return "ReturnedPost [postId=" + postId + ", authorId=" + authorId + ", text=" + text + ", title=" + title + ", images=" + images
				+ ", videos=" + videos + ", commentsCount=" + commentsCount + ", timestamp=" + timestamp
				+ ", isCompanyPost=" + isCompanyPost + ", authorName=" + authorName + ", authorProfilePictureUrl="
				+ authorProfilePictureUrl + ", headline=" + headline + ", likers=" + likers + ", liked=" + liked + "]";
	}



	@Override
	public int compareTo(ReturnedPost o) {
		return Long.compare( o.getTimestamp(),this.getTimestamp());
	}
}

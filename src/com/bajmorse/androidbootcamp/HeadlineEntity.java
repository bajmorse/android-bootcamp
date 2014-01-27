package com.bajmorse.androidbootcamp;	

import com.google.gson.annotations.Expose;

public class HeadlineEntity {

	// --- HEADLINE --- // 
	
	@Expose
	private String headline = null;
	
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public String getHeadline() {
		return headline;
	}

	// --- LINK --- //
	
	private String link = null;
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getLink() {
		return link;
	}
	
	// --- PICTURE --- //
	
	@Expose
	private String pictureURL = null;
	
	public String getPictureURL() {
		return pictureURL;
	}
	
	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}
	
}

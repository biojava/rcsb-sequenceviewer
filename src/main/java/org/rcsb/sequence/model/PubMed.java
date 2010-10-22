package org.rcsb.sequence.model;

import java.io.Serializable;


public class PubMed implements Serializable{

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 944156914139173452L;

	String journalTitle;
	String issueNumber;
	String medlinePages;
	String authorList;
	String articleTitle;
	String publishedYear;
	String publishedMonth;
	String publishedDay;
	String volume;
	Integer pubmedId;
	public String getJournalTitle() {
		return journalTitle;
	}
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}
	public String getIssueNumber() {
		return issueNumber;
	}
	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}
	public String getMedlinePages() {
		return medlinePages;
	}
	public void setMedlinePages(String medlinePages) {
		this.medlinePages = medlinePages;
	}
	public String getAuthorList() {
		return authorList;
	}
	public void setAuthorList(String authorList) {
		this.authorList = authorList;
	}
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public String getPublishedYear() {
		return publishedYear;
	}
	public void setPublishedYear(String publishedYear) {
		this.publishedYear = publishedYear;
	}
	public String getPublishedMonth() {
		return publishedMonth;
	}
	public void setPublishedMonth(String publishedMonth) {
		this.publishedMonth = publishedMonth;
	}
	public String getPublishedDay() {
		return publishedDay;
	}
	public void setPublishedDay(String publishedDay) {
		this.publishedDay = publishedDay;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public Integer getPubmedId() {
		return pubmedId;
	}
	public void setPubmedId(Integer pubmedId) {
		this.pubmedId = pubmedId;
	}
	
	
	
}

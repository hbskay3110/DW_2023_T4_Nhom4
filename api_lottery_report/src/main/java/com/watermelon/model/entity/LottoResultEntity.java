package com.watermelon.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="result_lottery_aggregates")
public class LottoResultEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
    private String location;
    private String weekday;
    private Date date;
    private String prize;
    private String result;
    private String region;
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @CreationTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    
    public LottoResult toLottoResult() {
        return LottoResult.fromEntity(this);
    }
    public LottoResultEntity() {
		// TODO Auto-generated constructor stub
	}
    
	public LottoResultEntity(int id, String location, String weekday, Date date, String prize, String result,
			String region, Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.location = location;
		this.weekday = weekday;
		this.date = date;
		this.prize = prize;
		this.result = result;
		this.region = region;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getWeekday() {
		return weekday;
	}
	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getPrize() {
		return prize;
	}
	public void setPrize(String prize) {
		this.prize = prize;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}

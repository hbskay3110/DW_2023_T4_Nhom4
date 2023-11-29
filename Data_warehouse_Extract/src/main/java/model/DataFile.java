package model;

import java.sql.Date;
import java.sql.Timestamp;

public class DataFile {
    private int id;
    private int idConfig;
    private String note;
    private String status;
    private Date dateRun;
    private Timestamp createdAt;
    private String createdByModul;

    // Constructors, getters, and setters can be generated based on your needs

    public DataFile() {
    }

    public DataFile(int idConfig, String note, String status, Date dateRun, Timestamp createdAt, String createdByModul) {
        this.idConfig = idConfig;
        this.note = note;
        this.status = status;
        this.dateRun = dateRun;
        this.createdAt = createdAt;
        this.createdByModul = createdByModul;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdConfig() {
		return idConfig;
	}

	public void setIdConfig(int idConfig) {
		this.idConfig = idConfig;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateRun() {
		return dateRun;
	}

	public void setDateRun(Date dateRun) {
		this.dateRun = dateRun;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedByModul() {
		return createdByModul;
	}

	public void setCreatedByModul(String createdByModul) {
		this.createdByModul = createdByModul;
	}

	@Override
	public String toString() {
		return "DataFile [id=" + id + ", idConfig=" + idConfig + ", note=" + note + ", status=" + status + ", dateRun="
				+ dateRun + ", createdAt=" + createdAt + ", createdByModul=" + createdByModul + "]";
	}

	
    
}

package dao;

import java.sql.Timestamp;

public class DataConfig {
	private int id;
	private String fileName;
	private String code;
	private String description;
	private String sourcePath;
	private String location;
	private String separation;
	private String format;
	private String databaseNameStaging;
	private String databaseNameDatawarehouse;
	private String databaseNameMart;
	private String serverName;
	private String port;
	private String user;
	private String pass;
	private String columnsStagingTemp;
	private String typeColumnsStagingTemp;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private String createdBy;
	private String updatedBy;
	private boolean flag;

	// Constructors, getters, and setters can be generated based on your needs

	public DataConfig() {
	}

	public DataConfig(String fileName, String code, String description, String sourcePath, String location,
			String separation, String format, String databaseNameStaging, String databaseNameDatawarehouse,
			String databaseNameMart, String serverName, String port, String user, String pass,
			String columnsStagingTemp, String typeColumnsStagingTemp, Timestamp createdAt, Timestamp updatedAt,
			String createdBy, String updatedBy, boolean flag) {
		this.fileName = fileName;
		this.code = code;
		this.description = description;
		this.sourcePath = sourcePath;
		this.location = location;
		this.separation = separation;
		this.format = format;
		this.databaseNameStaging = databaseNameStaging;
		this.databaseNameDatawarehouse = databaseNameDatawarehouse;
		this.databaseNameMart = databaseNameMart;
		this.serverName = serverName;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.columnsStagingTemp = columnsStagingTemp;
		this.typeColumnsStagingTemp = typeColumnsStagingTemp;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.flag = flag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSeparation() {
		return separation;
	}

	public void setSeparation(String separation) {
		this.separation = separation;
	}

	public String getFormat() {
		return "."+format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDatabaseNameStaging() {
		return databaseNameStaging;
	}

	public void setDatabaseNameStaging(String databaseNameStaging) {
		this.databaseNameStaging = databaseNameStaging;
	}

	public String getDatabaseNameDatawarehouse() {
		return databaseNameDatawarehouse;
	}

	public void setDatabaseNameDatawarehouse(String databaseNameDatawarehouse) {
		this.databaseNameDatawarehouse = databaseNameDatawarehouse;
	}

	public String getDatabaseNameMart() {
		return databaseNameMart;
	}

	public void setDatabaseNameMart(String databaseNameMart) {
		this.databaseNameMart = databaseNameMart;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getColumnsStagingTemp() {
		return columnsStagingTemp;
	}

	public void setColumnsStagingTemp(String columnsStagingTemp) {
		this.columnsStagingTemp = columnsStagingTemp;
	}

	public String getTypeColumnsStagingTemp() {
		return typeColumnsStagingTemp;
	}

	public void setTypeColumnsStagingTemp(String typeColumnsStagingTemp) {
		this.typeColumnsStagingTemp = typeColumnsStagingTemp;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	// Getter and setter for boolean property
    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

	@Override
	public String toString() {
		return "DataConfig [id=" + id + ", fileName=" + fileName + ", code=" + code + ", description=" + description
				+ ", sourcePath=" + sourcePath + ", location=" + location + ", separation=" + separation + ", format="
				+ format + ", databaseNameStaging=" + databaseNameStaging + ", databaseNameDatawarehouse="
				+ databaseNameDatawarehouse + ", databaseNameMart=" + databaseNameMart + ", serverName=" + serverName
				+ ", port=" + port + ", user=" + user + ", pass=" + pass + ", columnsStagingTemp=" + columnsStagingTemp
				+ ", typeColumnsStagingTemp=" + typeColumnsStagingTemp + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", flag=" + flag + "]";
	}
	

}


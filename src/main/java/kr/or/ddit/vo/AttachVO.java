package kr.or.ddit.vo;

import java.util.Date;

import org.apache.ibatis.annotations.ConstructorArgs;


public class AttachVO {
	
	private int seq;
	private String tid;
	private String attachName;
	private int attachSize;
	private String attachType;
	private Date reqistDate;
	
	
	public AttachVO() {}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getAttachName() {
		return attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}

	public int getAttachSize() {
		return attachSize;
	}

	public void setAttachSize(int attachSize) {
		this.attachSize = attachSize;
	}

	public String getAttachType() {
		return attachType;
	}

	public void setAttachType(String attachType) {
		this.attachType = attachType;
	}

	public Date getReqistDate() {
		return reqistDate;
	}

	public void setReqistDate(Date reqistDate) {
		this.reqistDate = reqistDate;
	}

	@Override
	public String toString() {
		return "AttachVO [seq=" + seq + ", tid=" + tid + ", attachName=" + attachName + ", attachSize=" + attachSize
				+ ", attachType=" + attachType + ", reqistDate=" + reqistDate + "]";
	}

	
	
}

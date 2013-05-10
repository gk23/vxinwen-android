package net.vxinwen.bean;

import java.sql.Timestamp;

public class News extends Entity{
	private String[][] sections;
    /**
     * 暂时没用，以后用于用户定制新闻，组件替代category字段
     */
    private String tags;
    private Timestamp modifyTime;

	public Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setSections(String[][] sections) {
		this.sections = sections;
	}

	public String[][] getSections() {
		return sections;
	}
}

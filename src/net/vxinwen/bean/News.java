package net.vxinwen.bean;

import java.sql.Timestamp;

public class News{
	private String[][] sections;
    /**
     * 暂时没用，以后用于用户定制新闻，组件替代category字段
     */
    private String tags;
    private Timestamp modifyTime;
    private long id;
    /**
     * 内容所属类别，如头条、体育、段子等
     */
    private String category;
    private String image;
    private String title;
    /**
     * 摘要内容
     */
    private String summary;
    /**
     * 原文内容
     */    
    private String body;

    /**
     * 文章来源，如网易、新浪、我爱笑话网
     */
    private String source;
    private Timestamp publishTime;
    private String url;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String imageAddress) {
        this.image = imageAddress;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public Timestamp getPublishTime() {
        return publishTime;
    }
    public void setPublishTime(Timestamp publishTime) {
        this.publishTime = publishTime;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
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

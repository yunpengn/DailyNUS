package ind.hailin.dailynus.entity;

import java.io.Serializable;
import java.util.Date;

public class ChatDialogues implements Serializable{
    private Integer id;

    private Integer senderId;

    private Integer receiverId;

    private Boolean isRead;

    private Integer receiverType;

    private Integer mimeType;

    private String message;

    private String filename;

    private String url;

    private Date updatedAt;

    private String bak1;

    private String bak2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Integer getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(Integer receiverType) {
        this.receiverType = receiverType;
    }

    public Integer getMimeType() {
        return mimeType;
    }

    public void setMimeType(Integer mimeType) {
        this.mimeType = mimeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename == null ? null : filename.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBak1() {
        return bak1;
    }

    public void setBak1(String bak1) {
        this.bak1 = bak1 == null ? null : bak1.trim();
    }

    public String getBak2() {
        return bak2;
    }

    public void setBak2(String bak2) {
        this.bak2 = bak2 == null ? null : bak2.trim();
    }

    @Override
    public String toString() {
        return "ChatDialogues{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", isRead=" + isRead +
                ", receiverType=" + receiverType +
                ", mimeType=" + mimeType +
                ", message='" + message + '\'' +
                ", filename='" + filename + '\'' +
                ", url='" + url + '\'' +
                ", updatedAt=" + updatedAt +
                ", bak1='" + bak1 + '\'' +
                ", bak2='" + bak2 + '\'' +
                '}';
    }
}
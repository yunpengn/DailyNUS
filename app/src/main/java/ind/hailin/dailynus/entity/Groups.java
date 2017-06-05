package ind.hailin.dailynus.entity;

import java.io.Serializable;
import java.util.Date;

public class Groups implements Serializable{
    private Integer id;

    private String name;

    private Integer creatorId;

    private Boolean isModuleGroup;

    private Integer membersCount;

    private String description;

    private Date updatedAt;

    private String bak1;

    private String bak2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Boolean getIsModuleGroup() {
        return isModuleGroup;
    }

    public void setIsModuleGroup(Boolean isModuleGroup) {
        this.isModuleGroup = isModuleGroup;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
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
        return "Groups{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creatorId=" + creatorId +
                ", isModuleGroup=" + isModuleGroup +
                ", membersCount=" + membersCount +
                ", description='" + description + '\'' +
                ", updatedAt=" + updatedAt +
                ", bak1='" + bak1 + '\'' +
                ", bak2='" + bak2 + '\'' +
                '}';
    }
}
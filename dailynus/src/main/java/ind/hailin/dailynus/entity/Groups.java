package ind.hailin.dailynus.entity;

import java.util.Date;

public class Groups {
    private Integer id;

    private String name;

    private Integer creatorId;

    private Boolean isModuleGroup;

    private Integer membersCount;

    private String avatar;

    private Date updatedAt;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
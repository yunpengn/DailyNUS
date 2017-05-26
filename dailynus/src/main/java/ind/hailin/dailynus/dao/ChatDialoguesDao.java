package ind.hailin.dailynus.dao;

import ind.hailin.dailynus.entity.ChatDialogues;

public interface ChatDialoguesDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ChatDialogues record);

    int insertSelective(ChatDialogues record);

    ChatDialogues selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChatDialogues record);

    int updateByPrimaryKey(ChatDialogues record);
}
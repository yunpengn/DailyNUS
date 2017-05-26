package ind.hailin.dailynus.dao;

import ind.hailin.dailynus.entity.Groups;

public interface GroupsDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Groups record);

    int insertSelective(Groups record);

    Groups selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Groups record);

    int updateByPrimaryKey(Groups record);
}
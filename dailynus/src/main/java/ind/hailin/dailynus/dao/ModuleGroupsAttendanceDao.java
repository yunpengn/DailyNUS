package ind.hailin.dailynus.dao;

import ind.hailin.dailynus.entity.ModuleGroupsAttendance;

public interface ModuleGroupsAttendanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ModuleGroupsAttendance record);

    int insertSelective(ModuleGroupsAttendance record);

    ModuleGroupsAttendance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ModuleGroupsAttendance record);

    int updateByPrimaryKey(ModuleGroupsAttendance record);
}
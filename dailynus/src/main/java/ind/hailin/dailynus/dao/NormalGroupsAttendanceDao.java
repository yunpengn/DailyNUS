package ind.hailin.dailynus.dao;

import ind.hailin.dailynus.entity.NormalGroupsAttendance;

public interface NormalGroupsAttendanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(NormalGroupsAttendance record);

    int insertSelective(NormalGroupsAttendance record);

    NormalGroupsAttendance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NormalGroupsAttendance record);

    int updateByPrimaryKey(NormalGroupsAttendance record);
}
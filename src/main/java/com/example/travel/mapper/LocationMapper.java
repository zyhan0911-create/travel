package com.example.travel.mapper;

import com.example.travel.entity.LocationItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LocationMapper {

    @Select("SELECT * FROM location_item")
    List<LocationItem> selectAll();

    @Insert("INSERT INTO location_item(name, category, latitude, longitude, address, image_url, description, city) " +
            "VALUES(#{name}, #{category}, #{latitude}, #{longitude}, #{address}, #{imageUrl}, #{description}, #{city})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LocationItem item);

    @Update("UPDATE location_item SET name=#{name}, category=#{category}, latitude=#{latitude}, " +
            "longitude=#{longitude}, address=#{address}, image_url=#{imageUrl}, description=#{description}, city=#{city} WHERE id=#{id}")
    int update(LocationItem item);

    @Delete("DELETE FROM location_item WHERE id=#{id}")
    int delete(Integer id);

    @Select("<script>" +
            "SELECT * FROM location_item " +
            "<where>" +
            "  <if test='city != null and city != \"\" and city != \"全部\"'> city = #{city} </if>" +
            "</where>" +
            "</script>")
    List<LocationItem> selectByCity(@Param("city") String city);
}
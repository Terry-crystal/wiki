package com.example.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface DocMapperCust {

    void increaseViewCount(@Param("id") Long id);    //注解中的id才是对应底层中的#id参数

    void increaseVoteCount(@Param("id") Long id);

    void updateEbookInfo();

}

package com.changgou.goods.dao;

import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecMapper extends Mapper<Spec> {


    /**
     * 根据商品分类名称查询规格列表
     * @param categoryName
     * @return
     */
    @Select("SELECT name,options FROM tb_spec WHERE template_id in(SELECT template_id FROM tb_category WHERE name=#{categoryName})")
    List<Spec> findListByCategoryName(String categoryName);
}

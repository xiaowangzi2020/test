package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.utils.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id){
        return  spuMapper.selectByPrimaryKey(id);
    }


    @Autowired
    private IdWorker idWorker;

    /**
     * 增加
     * @param goods
     */
    @Override
    @Transactional
    public void add(Goods goods){


        /*
         "spu": {
      	"name": "这个是商品名称",
      	"caption": "这个是副标题",
		"brandId": 1115,
		"category1Id": 558,
		"category2Id": 559,
		"category3Id": 560,
		"freightId": 10,
		"image": "http://www.changgou.com/image/1.jpg",
         "images": "http://www.changgou.com/image/1.jpg,http://www.changgou.com/image/2.jpg",
		"introduction": "这个是商品详情，html代码",
         "paraItems": "{"出厂年份":"2019","赠品":"充电器"}",
		"saleService": "七天包退,闪电退货",
		"sn": "020102331",
		"specItems":  "{"颜色":["红","绿"],"机身内存":["64G","8G"]}",
		"templateId": 42
	}
         */

        Spu spu = goods.getSpu();
        //分布式ID
        spu.setId(String.valueOf(idWorker.nextId()));
        //是否删除
        spu.setIsDelete("0");
        //是否上架
        spu.setIsMarketable("0");
        //审核状态
        spu.setStatus("0");

        this.spuMapper.insert(spu);

        //添加sku
        this.addSku(goods);
    }


    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;


    /**
     * 添加sku
     * @param goods
     */
    private void addSku(Goods goods) {

        /*
        "skuList": [{
		"sn": "10192010292",
         "num": 100,
      	 "alertNum": 20,
		 "price": 900000,
         "spec": "{"颜色":"红","机身内存":"64G"}",
         "image": "http://www.changgou.com/image/1.jpg",
         "images": "http://www.changgou.com/image/1.jpg,http://www.changgou.com/image/2.jpg",
		"status": "1",
		"weight": 130
	},
    {
		"sn": "10192010293",
         "num": 100,
      	 "alertNum": 20,
		 "price": 600000,
         "spec": "{"颜色":"蓝","机身内存":"128G"}",
         "image": "http://www.changgou.com/image/1.jpg",
         "images": "http://www.changgou.com/image/1.jpg,http://www.changgou.com/image/2.jpg",
		"status": "1",
		"weight": 130
	}
  ]
         */


        List<Sku> skuList = goods.getSkuList();

        for (Sku sku : skuList) {
            //分布式ID
            sku.setId(String.valueOf(idWorker.nextId()));

            sku.setCreateTime(new Date());
            sku.setUpdateTime(new Date());
            sku.setSpuId(goods.getSpu().getId());
            sku.setCategoryId(goods.getSpu().getCategory3Id());

            //设置分类名
            Category category = categoryMapper.selectByPrimaryKey(goods.getSpu().getCategory3Id());
            sku.setCategoryName(category.getName());

            //设置品牌名
            Brand brand = brandMapper.selectByPrimaryKey(goods.getSpu().getBrandId());
            sku.setBrandName(brand.getName());


            //设置规格
            if (sku.getSpec() == null || "".equals(sku.getSpec())) {
                sku.setSpec("{}");
            }

            String skuName = goods.getSpu().getName();

            Map<String, String> specMap = JSON.parseObject(sku.getSpec(), Map.class);

            for (String value : specMap.keySet()) {
                skuName += " "+ value;
            }

            sku.setSpec(skuName);

            skuMapper.insertSelective(sku);

        }


    }


    /**
     * 修改
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}

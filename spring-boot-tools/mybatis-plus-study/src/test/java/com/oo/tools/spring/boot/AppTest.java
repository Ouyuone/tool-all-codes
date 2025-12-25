package com.oo.tools.spring.boot;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oo.tools.spring.boot.entity.UserInfoEntity;
import com.oo.tools.spring.boot.enums.SexEnum;
import com.oo.tools.spring.boot.model.UserPageRes;
import com.oo.tools.spring.boot.repository.UserInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Unit test for simple App.
 */
@Transactional
@SpringBootTest
public class AppTest {
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Test
    public void test_enum_converter() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setSex(SexEnum.MALE);
        userInfoEntity.setMobile("12331231");
        userInfoEntity.setName("Ni");
        userInfoRepository.insert(userInfoEntity);
        Long id = userInfoEntity.getId();
        Assertions.assertNotNull(id);
        
        
        UserInfoEntity userInfoOldEntity = userInfoRepository.selectById(id);
        
        userInfoOldEntity.setSex(SexEnum.FEMALE);
        int updateOk = userInfoRepository.updateById(userInfoOldEntity);
        
        Assertions.assertTrue(updateOk > 0);
        
        userInfoOldEntity = userInfoRepository.selectById(id);
        
        Assertions.assertEquals(SexEnum.FEMALE, userInfoOldEntity.getSex());
        
    }
    
    
    @Test
    public void test_page() {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserInfoEntity> userInfoEntityPage = userInfoRepository.selectPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page.of(1, 10), null);
        long current = userInfoEntityPage.getCurrent();
        Assertions.assertEquals(1,current);
        
        // 使用 PageHelper 进行分页查询
        // 方式1：显式调用 PageHelper.startPage() 方法（推荐方式）
        UserPageRes pageRequest = new UserPageRes();
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        
        // 执行查询，返回类型是 List，但 PageHelper 会自动将结果包装在 Page 对象中
        // 注意：分页参数通过 PageHelper.startPage() 设置，不需要在方法参数中传递
        List<UserInfoEntity> list = userInfoRepository.findPageQuery();
        
        // 获取分页信息（推荐使用 PageInfo）
        PageInfo<UserInfoEntity> pageInfo = PageInfo.of(list);
        
        // 验证分页信息
        Assertions.assertEquals(1, pageInfo.getPageNum());
        Assertions.assertEquals(10, pageInfo.getPageSize());
        Assertions.assertTrue(pageInfo.getTotal() >= 0);
        Assertions.assertNotNull(pageInfo.getList());
    }
    
    /**
     * 测试通过方法参数传递分页信息的分页查询
     * 使用 PageHelper 的 support-methods-arguments 功能
     * 不需要显式调用 PageHelper.startPage()，PageHelper 会自动从方法参数中识别分页信息
     */
    @Test
    public void test_page_by_param() {
        // 创建分页请求参数
        UserPageRes pageRequest = new UserPageRes();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(5);
        
        // 执行查询，PageHelper 会自动从 pageRequest 参数中识别分页信息
        // 注意：不需要调用 PageHelper.startPage()，因为配置了 support-methods-arguments: true
        List<UserInfoEntity> list = userInfoRepository.findPageQueryByParam(pageRequest);
        
        // 获取分页信息
        PageInfo<UserInfoEntity> pageInfo = PageInfo.of(list);
        
        // 验证分页信息
        Assertions.assertEquals(1, pageInfo.getPageNum(), "页码应该是 1");
        Assertions.assertEquals(5, pageInfo.getPageSize(), "每页大小应该是 5");
        Assertions.assertTrue(pageInfo.getTotal() >= 0, "总记录数应该大于等于 0");
        Assertions.assertNotNull(pageInfo.getList(), "结果列表不应该为 null");
        Assertions.assertTrue(pageInfo.getList().size() <= 5, "当前页记录数应该小于等于每页大小");
        
        // 测试第二页
        pageRequest.setPageNum(2);
        pageRequest.setPageSize(5);
        List<UserInfoEntity> list2 = userInfoRepository.findPageQueryByParam(pageRequest);
        PageInfo<UserInfoEntity> pageInfo2 = PageInfo.of(list2);
        Assertions.assertEquals(2, pageInfo2.getPageNum(), "页码应该是 2");
        Assertions.assertEquals(5, pageInfo2.getPageSize(), "每页大小应该是 5");
    }
    
    /**
     * 测试返回 Page 类型的分页查询方法
     * 使用 PageHelper 的 support-methods-arguments 功能
     * 方法直接返回 Page 类型，可以直接使用 Page 对象的方法获取分页信息
     */
    @Test
    public void test_page_return_page() {
        // 创建分页请求参数
        UserPageRes pageRequest = new UserPageRes();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(5);
        
        // 执行查询，返回类型是 Page，PageHelper 会自动从 pageRequest 参数中识别分页信息
        // 注意：不需要调用 PageHelper.startPage()，因为配置了 support-methods-arguments: true
        Page<UserInfoEntity> page = userInfoRepository.findPageQueryReturnPage(pageRequest);
        
        // 直接使用 Page 对象的方法获取分页信息
        Assertions.assertEquals(1, page.getPageNum(), "页码应该是 1");
        Assertions.assertEquals(5, page.getPageSize(), "每页大小应该是 5");
        Assertions.assertTrue(page.getTotal() >= 0, "总记录数应该大于等于 0");
        Assertions.assertNotNull(page.getResult(), "结果列表不应该为 null");
        Assertions.assertTrue(page.getResult().size() <= 5, "当前页记录数应该小于等于每页大小");
        
        // Page 对象实现了 List 接口，可以直接当作 List 使用
        Assertions.assertEquals(page.getResult().size(), page.size(), "Page 的 size 应该等于结果列表的大小");
        
        // 测试第二页
        pageRequest.setPageNum(2);
        pageRequest.setPageSize(5);
        Page<UserInfoEntity> page2 = userInfoRepository.findPageQueryReturnPage(pageRequest);
        Assertions.assertEquals(2, page2.getPageNum(), "页码应该是 2");
        Assertions.assertEquals(5, page2.getPageSize(), "每页大小应该是 5");
        
        // 验证分页信息
        Assertions.assertTrue(page2.getTotal() == page.getTotal(), "总记录数应该相同");
        Assertions.assertTrue(page2.getPages() == page.getPages(), "总页数应该相同");
    }
    
}

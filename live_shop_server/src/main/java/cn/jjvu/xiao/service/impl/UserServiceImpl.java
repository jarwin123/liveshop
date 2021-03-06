package cn.jjvu.xiao.service.impl;

import cn.jjvu.xiao.core.model.PageRequest;
import cn.jjvu.xiao.core.model.PageResult;
import cn.jjvu.xiao.dao.LoginLogMapper;
import cn.jjvu.xiao.dao.MenuMapper;
import cn.jjvu.xiao.dao.UserMapper;
import cn.jjvu.xiao.pojo.LoginLog;
import cn.jjvu.xiao.pojo.Menu;
import cn.jjvu.xiao.pojo.User;
import cn.jjvu.xiao.pojo.UserExample;
import cn.jjvu.xiao.service.MenuService;
import cn.jjvu.xiao.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private MenuService menuService;

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public int save(User record) {
        return 0;
    }

    @Override
    public int delete(Number id) {
        return userMapper.deleteByPrimaryKey((Integer) id);
    }

    @Override
    public int delete(Set<Number> ids) {
        for (Number id : ids) {
            userMapper.deleteByPrimaryKey((Integer) id);
        }
        return ids.size();
    }

    @Override
    public int update(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public User findById(Number T) {
        return userMapper.selectByPrimaryKey((Integer) T);
    }

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public User login(String loginname, String passwd, LoginLog loginLog) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(loginname);
        User user =  userMapper.selectByExample(example).get(0);
        Date now = new Date();
        loginLog.setCreateTime(now);
        loginLog.setLastUpdateTime(now);
        loginLog.setStatus("0");
        if (null != user) {
            loginLog.setCreateBy(user.getId().toString());
            loginLog.setLastUpdateBy(user.getId().toString());
            loginLog.setUserName(user.getNickName());
        }
        else {
            loginLog.setCreateBy(loginname);
            loginLog.setLastUpdateBy(loginname);
            loginLog.setUserName("用户账号或者密码不正确");;
        }
        loginLogMapper.insertSelective(loginLog);
        return user;
    }

    @Override
    public User findByName(String username) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(username);
        List<User> users = userMapper.selectByExample(example);
        return users.size() ==  0 ? null : users.get(0);
    }

    @Override
    public Set<String> findPermissions(String userName) {
        Set<String> perms = new HashSet<>();
        List<Menu> sysMenus = menuService.findByUser(userName);
        for(Menu sysMenu:sysMenus) {
            if(sysMenu.getPerms() != null && !"".equals(sysMenu.getPerms())) {
                perms.add(sysMenu.getPerms());
            }
        }
        return perms;
    }

    @Override
    public List<User> findUserRoles(Long userId) {
        return null;
    }

    @Override
    public File createUserExcelFile(int page, int size) {
        return null;
    }
}

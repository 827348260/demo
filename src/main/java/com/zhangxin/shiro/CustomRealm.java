package com.zhangxin.shiro;

import com.zhangxin.bean.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * https://www.zifangsky.cn/770.html
 * @author zhangxin
 *         Created on 17/3/8.
 */
public class CustomRealm extends AuthorizingRealm {
    /**
     * 返回一个唯一的Realm名称
     * */
    @Override
    public String getName() {
        return "CustomRealm";
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = null;
        User userBO = (User) principalCollection.fromRealm(getName()).iterator().next();
        if(userBO != null){
            info = new SimpleAuthorizationInfo();
            List<User> roleBOs = userBO.getUsrRoleBOs();  //所有角色
            if(roleBOs != null && roleBOs.size() > 0){
                List<String> roleNames = new ArrayList<>();  //所有角色名
                Set<String> funcCodes = new HashSet<>();  //该用户拥有的所有角色的所有权限的code集合

                for(UsrRoleBO roleBO : roleBOs){
                    roleNames.add(roleBO.getRolename());

                    List<UsrFunc> funcs = roleBO.getFuncs();  //一个角色的所有权限的code集合
                    if(funcs != null && funcs.size() > 0){
                        for(UsrFunc f : funcs){
                            funcCodes.add(f.getCode());
                        }
                    }
                }
                //添加所有的角色和权限
                info.addRoles(roleNames);
                info.addStringPermissions(funcCodes);
            }
        }
        return info;
    }

    /**
     * 认证
     * 在这里由于登录之后在session中已经存在用户信息了，同时这里的token里的认证信息也是在
     * cn.zifangsky.security.LoginFilter的createToken方法中从session获取的用户信息然后再添加的。
     * 因此这里就省略了对token里的信息的校验步骤，直接从session中获取userBO并返回认证之后的凭证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        UsrUserBO userBO = (UsrUserBO) request.getSession().getAttribute("userBO");
        SimpleAuthenticationInfo info = null;
        if(userBO != null){
            info = new SimpleAuthenticationInfo(userBO, userBO.getPassword(), getName());
        }

        return info;
    }

}
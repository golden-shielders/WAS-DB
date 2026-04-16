package com.golden_shielders.server.repository;


import com.golden_shielders.server.entity.WebSiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public WebSiteUser findUserByUserName(String userName){
        String sql = "select * from web_site_user where user_name ='" + userName  + "'";
        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) ->{
                    WebSiteUser user = new WebSiteUser();
                    user.setId(rs.getInt("id"));
                    user.setUserName(rs.getString("user_name"));
                    user.setPw(rs.getString("pw"));
                    user.setRole(rs.getString("role"));
                    return user;
                });
    }
}

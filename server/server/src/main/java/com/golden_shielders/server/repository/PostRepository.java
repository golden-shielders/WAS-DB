package com.golden_shielders.server.repository;

import com.golden_shielders.server.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Post> findPostInfoList(int page, int size, String sort) {
        String sql = "SELECT * FROM post ORDER BY " + sort + " LIMIT " + size + " OFFSET " + (page * size);
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Post p = new Post();
                    p.setId(rs.getInt("id"));
                    p.setTitle(rs.getString("title"));
                    p.setAuthorName(rs.getString("author_name"));
                    return p;
                });
    }

    public Post findPostInfoById(int id) {
        String sql = "SELECT * FROM post WHERE id='" + id + "'";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Post p = new Post();
            p.setId(rs.getInt("id"));
            p.setTitle(rs.getString("title"));
            p.setContent(rs.getString("content"));
            p.setAuthorName(rs.getString("author_name"));
            return p;
        });
    }

    // 생성
    public Integer save(Post post) {
        String sql = "INSERT INTO post (title, content, author_name) VALUES ('"
                + post.getTitle() + "', '"
                + post.getContent() + "', '"
                + post.getAuthorName() + "')";
        jdbcTemplate.update(sql);
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    // 수정
    public Post update(Post post) {
        String sql = "UPDATE post SET title='" + post.getTitle()
                + "', content='" + post.getContent()
                + "' WHERE id=" + post.getId();
        jdbcTemplate.update(sql);
        return post;
    }

    // 삭제
    public void deleteById(int id) {
        String sql = "DELETE FROM post WHERE id=" + id;
        jdbcTemplate.update(sql);
    }
}
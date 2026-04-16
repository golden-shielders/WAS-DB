package com.golden_shielders.server.repository;


import com.golden_shielders.server.entity.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LocalStorageRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(UploadFile uploadFile) {
        // 파라미터 바인딩(?)을 제거하고 데이터를 직접 SQL 문자열에 삽입
        String sql = "INSERT INTO upload_file (post_id, original_name, stored_name, `file_path`) VALUES ("
                + uploadFile.getPostId() + ", '"
                + uploadFile.getOriginalName() + "', '"
                + uploadFile.getStoredName() + "', '"
                + uploadFile.getFilePath() + "')";

        // 아규먼트 없이 쿼리 문자열만 그대로 실행
        jdbcTemplate.update(sql);
    }

    public List<UploadFile> findFilesByPostId(int id) {
        // 사용자가 입력한 id를 SQL 문에 직접 더하여 SQL Injection에 취약하게 만듦
        String sql = "SELECT * FROM upload_file WHERE post_id = " + id;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UploadFile file = new UploadFile();
            file.setId(rs.getInt("id"));
            file.setPostId(rs.getInt("post_id"));
            file.setOriginalName(rs.getString("original_name"));
            file.setStoredName(rs.getString("stored_name"));
            file.setFilePath(rs.getString("file_path"));
            return file;
        });
    }
}
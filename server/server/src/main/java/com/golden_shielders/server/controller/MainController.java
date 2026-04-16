package com.golden_shielders.server.controller;

import com.golden_shielders.server.Dto.LoginDTO;
import com.golden_shielders.server.Dto.PostDTO;
import com.golden_shielders.server.Dto.PostSummary;
import com.golden_shielders.server.config.JwtUtil;
import com.golden_shielders.server.entity.Post;
import com.golden_shielders.server.service.LocalStorageService;
import com.golden_shielders.server.service.PostService;
import com.golden_shielders.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class MainController {
    private final PostService postService;
    private final UserService userService;
    private final LocalStorageService localStorageService;
    private final JwtUtil jwtUtil;  // 추가


    @GetMapping("/posts")
    public ResponseEntity<List<PostSummary>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        return ResponseEntity.ok(postService.getPostList(page, size, sort));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostInfoByPostId(@PathVariable int id){
        return ResponseEntity.ok(postService.getPostDetailInfoById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginDTO userInfo) {
        String token = userService.checkLogin(userInfo.getUserName(), userInfo.getPassword());
        if (token == null) {

            return ResponseEntity.status(401).body("로그인 실패");
        }
        return ResponseEntity.ok(token);
    }

    // 생성
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestHeader("Authorization") String token,
            @RequestParam ("title") String title,
            @RequestParam ("content") String content,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        String userName = jwtUtil.getUserName(token.substring(7));
        Post post = new Post();
        post.setContent(content);
        post.setAuthorName(userName);
        post.setTitle(title);
        int id = postService.createPost(post, userName);
        if (files!= null && files.length>0)
            localStorageService.storeFiles(files, id);

        return ResponseEntity.ok(id);
    }

    // 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(
            @RequestHeader("Authorization") String token,
            @PathVariable int id,
            @RequestBody Post post) {
        String userName = jwtUtil.getUserName(token.substring(7));
        return ResponseEntity.ok(postService.updatePost(id, post, userName));
    }

    // 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        String userName = jwtUtil.getUserName(token.substring(7));
        postService.deletePost(id, userName);
        return ResponseEntity.noContent().build();
    }

    //토큰 Validation check
    @PostMapping("/token")
    public ResponseEntity<?> checkTokenValidation(@RequestHeader("Authorization") String token){
        boolean ret = jwtUtil.validateToken(token);
        if (ret) return ResponseEntity.ok("Valid Token");
        else return ResponseEntity.status(401).body("Invalid Token");
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestParam String filePath) {
        File file = new File(filePath).getAbsoluteFile();
        Resource resource = new FileSystemResource(file);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

package com.golden_shielders.server.service;

import com.golden_shielders.server.Dto.FileDTO;
import com.golden_shielders.server.Dto.PostDTO;
import com.golden_shielders.server.Dto.PostSummary;
import com.golden_shielders.server.entity.Post;
import com.golden_shielders.server.entity.UploadFile;
import com.golden_shielders.server.repository.LocalStorageRepository;
import com.golden_shielders.server.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LocalStorageRepository localStorageRepository;

    public List<PostSummary> getPostList(int page, int size, String sort) {
        List<Post> postInfos = postRepository.findPostInfoList(page, size, sort);
        List<PostSummary> simplePostInfos = new ArrayList<>();
        for (int i = 0; i < postInfos.size(); i++) {
            PostSummary summary = new PostSummary();
            summary.setTitle(postInfos.get(i).getTitle());
            summary.setId(postInfos.get(i).getId());
            summary.setAuthorName(postInfos.get(i).getAuthorName());
            simplePostInfos.add(summary);
        }
        return simplePostInfos;
    }

    public PostDTO getPostDetailInfoById(int id) {
        List<UploadFile> uploadFiles = localStorageRepository.findFilesByPostId(id);
        List<FileDTO> files = new ArrayList<>();
        for(int i = 0; i < uploadFiles.size(); i++){
            FileDTO dto = new FileDTO();
            dto.setId(uploadFiles.get(i).getId());
            dto.setFullPath(uploadFiles.get(i).getFilePath());
            dto.setOriginalName(uploadFiles.get(i).getOriginalName());
            files.add(dto);
        }
        Post post = postRepository.findPostInfoById(id);

        PostDTO postDTO = new PostDTO();
        postDTO.setAttachments(files);
        postDTO.setAuthorName(post.getAuthorName());
        postDTO.setId(post.getId());
        postDTO.setContent(post.getContent());
        postDTO.setTitle(post.getTitle());
        return postDTO;
    }

    // 생성
    @Transactional
    public Integer createPost(Post post, String userName) {
        post.setAuthorName(userName);
        return postRepository.save(post);
    }

    // 수정
    @Transactional
    public Post updatePost(int id, Post post, String userName) {
        Post existPost = postRepository.findPostInfoById(id);
        if (!existPost.getAuthorName().equals(userName)) {
            throw new RuntimeException("수정 권한이 없습니다");
        }
        post.setId(id);
        return postRepository.update(post);
    }

    // 삭제
    @Transactional
    public void deletePost(int id, String userName) {
        Post existPost = postRepository.findPostInfoById(id);
        if (!existPost.getAuthorName().equals(userName)) {
            throw new RuntimeException("삭제 권한이 없습니다");
        }
        postRepository.deleteById(id);
    }
}
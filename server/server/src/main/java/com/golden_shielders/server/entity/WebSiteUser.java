package com.golden_shielders.server.entity;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class WebSiteUser {
    Integer id;
    String userName;
    String pw;
    String role;
}
